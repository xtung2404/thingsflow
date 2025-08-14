package com.example.thingsflow.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

class LayoutZoomPan @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewBox.OnBoxClickListener {

    private var scaleFactor = 1.0f
    private var translateX = 0f
    private var translateY = 0f
    private var contentWidth = 0f
    private var contentHeight = 0f

    private val gestureDetector: GestureDetector

    var onBoxClickListener: ViewBox.OnBoxClickListener? = null

    var boxList: ArrayList<FBox> = arrayListOf()
        set(value) {
            field = value
            setupFlowLayout()
            invalidate()
        }

    private val boundingBoxes = mutableMapOf<Int, RectF>()
    private val boxPositions = mutableMapOf<FBox, RectF>()
    private val segmentPositions = mutableMapOf<Int, RectF>()
    private val firstBoxInSegment = mutableMapOf<Int, FBox>()

    private val connectionPaint = Paint().apply {
        color = Color.parseColor("#8A2BE2")
        strokeWidth = dpToPx(4f)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val boundingBoxPaint = Paint().apply {
        color = resources.getColor(R.color.bg_segment, null)
        style = Paint.Style.FILL
        strokeWidth = dpToPx(2f)
        isAntiAlias = true
    }

    private val horizontalSpacing = dpToPx(100f)
    private val verticalSpacing = dpToPx(20f)
    private val groupPadding = dpToPx(16f)
    private val startXPadding = dpToPx(20f)
    private val topMargin = dpToPx(50f)

    private lateinit var actionBoxesBySegId: Map<Int, List<FBoxActionControlDevice>>
    private val segmentHeightCache = mutableMapOf<Int, Float>()

    init {
        gestureDetector = GestureDetector(context, GestureListener())
        setWillNotDraw(false)
    }

    private fun setupFlowLayout() {
        removeAllViews()
        boundingBoxes.clear()
        boxPositions.clear()
        segmentPositions.clear()
        firstBoxInSegment.clear()
        segmentHeightCache.clear()

        val nonNullBoxes = boxList.filterNotNull()
        val eventBox = nonNullBoxes.firstOrNull { it is FBoxEventDevice } as? FBoxEventDevice
        actionBoxesBySegId = nonNullBoxes
            .filterIsInstance<FBoxActionControlDevice>()
            .groupBy { it.segId }

        eventBox?.let {
            val view = createViewForFBox(it)
            addView(view)
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            view.x = startXPadding
            view.y = topMargin
            boxPositions[it] = RectF(view.x, view.y, view.x + view.measuredWidth, view.y + view.measuredHeight)
        }

        // Giai đoạn 1: Tính toán chiều cao của tất cả các segment
        val allSegIds = actionBoxesBySegId.keys
        allSegIds.forEach { segId ->
            calculateSegmentHeight(segId, actionBoxesBySegId)
        }

        // Giai đoạn 2: Bố cục các segment và box dựa trên chiều cao đã tính
        val queue = LinkedList<Pair<Int, FBox?>>()
        val processed = mutableSetOf<Int>()

        eventBox?.targetSegId?.let { queue.add(Pair(it, eventBox)) }

        val branchYPositions = mutableMapOf<Int, Float>()

        while (queue.isNotEmpty()) {
            val (segId, parentBox) = queue.poll()
            if (processed.contains(segId)) continue
            processed.add(segId)

            val group = actionBoxesBySegId[segId] ?: continue

            val boxesInSegment = group.sortedBy { it.positiveSegId != 0 }

            var maxGroupWidth = 0f
            var groupHeight = 0f
            boxesInSegment.forEach { box ->
                val tempView = createViewForFBox(box)
                tempView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                maxGroupWidth = max(maxGroupWidth, tempView.measuredWidth.toFloat())
                groupHeight += tempView.measuredHeight + verticalSpacing
            }
            if (boxesInSegment.isNotEmpty()) groupHeight -= verticalSpacing

            val totalGroupWidth = maxGroupWidth + 2 * groupPadding
            val contentHeightOfSegment = groupHeight + 2 * groupPadding

            val parentBoxRect = parentBox?.let { boxPositions[it] }

            val groupLeft = if (parentBoxRect != null) parentBoxRect.right + horizontalSpacing else startXPadding

            var boundingBoxTop = 0f
            if (parentBox is FBoxActionControlDevice) {
                val branches = mutableListOf<Int>()
                if (parentBox.positiveSegId != 0) branches.add(parentBox.positiveSegId)
                if (parentBox.negativeSegId != 0) branches.add(parentBox.negativeSegId)

                if (branches.isNotEmpty()) {
                    var totalBranchesHeight = 0f
                    branches.forEach { branchSegId ->
                        totalBranchesHeight += segmentHeightCache[branchSegId] ?: 0f
                    }
                    totalBranchesHeight += (branches.size - 1) * verticalSpacing

                    val startY = parentBoxRect!!.centerY() - totalBranchesHeight / 2

                    var currentY = startY
                    branches.forEach { branchSegId ->
                        val segmentHeight = segmentHeightCache[branchSegId] ?: 0f
                        branchYPositions[branchSegId] = currentY
                        currentY += segmentHeight + verticalSpacing
                    }
                    boundingBoxTop = branchYPositions[segId] ?: startY
                } else {
                    boundingBoxTop = parentBoxRect!!.centerY() - contentHeightOfSegment / 2
                }
            } else if (parentBox is FBoxEventDevice) {
                boundingBoxTop = parentBoxRect!!.top
            } else {
                boundingBoxTop = topMargin
            }

            val segmentRect = RectF(groupLeft, boundingBoxTop, groupLeft + totalGroupWidth, boundingBoxTop + contentHeightOfSegment)
            boundingBoxes[segId] = segmentRect
            segmentPositions[segId] = segmentRect

            var currentBoxY = boundingBoxTop + groupPadding
            boxesInSegment.forEach { box ->
                val view = createViewForFBox(box)
                addView(view)
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

                view.x = groupLeft + groupPadding
                view.y = currentBoxY
                boxPositions[box] = RectF(view.x, view.y, view.x + view.measuredWidth, view.y + view.measuredHeight)

                if (firstBoxInSegment[segId] == null) {
                    firstBoxInSegment[segId] = box
                }

                currentBoxY += view.measuredHeight + verticalSpacing
            }

            boxesInSegment.forEach { box ->
                if (box is FBoxActionControlDevice) {
                    if (box.positiveSegId != 0 && !processed.contains(box.positiveSegId)) {
                        queue.add(Pair(box.positiveSegId, box))
                    }
                    if (box.negativeSegId != 0 && !processed.contains(box.negativeSegId)) {
                        queue.add(Pair(box.negativeSegId, box))
                    }
                }
            }

            contentWidth = max(contentWidth, groupLeft + totalGroupWidth)
        }

        contentHeight = calculateContentBounds().height()
    }

    private fun calculateSegmentHeight(segId: Int, actionBoxesBySegId: Map<Int, List<FBoxActionControlDevice>>): Float {
        if (segmentHeightCache.containsKey(segId)) {
            return segmentHeightCache[segId]!!
        }

        val group = actionBoxesBySegId[segId] ?: return 0f
        var groupHeight = 0f
        var maxChildBranchHeight = 0f

        group.forEach { box ->
            val tempView = createViewForFBox(box)
            tempView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            groupHeight += tempView.measuredHeight + verticalSpacing

            if (box is FBoxActionControlDevice) {
                val branches = mutableListOf<Int>()
                if (box.positiveSegId != 0) branches.add(box.positiveSegId)
                if (box.negativeSegId != 0) branches.add(box.negativeSegId)

                if (branches.isNotEmpty()) {
                    var currentBranchHeight = 0f
                    branches.forEach { branchId ->
                        currentBranchHeight += calculateSegmentHeight(branchId, actionBoxesBySegId)
                    }
                    currentBranchHeight += (branches.size - 1) * verticalSpacing
                    maxChildBranchHeight = max(maxChildBranchHeight, currentBranchHeight)
                }
            }
        }
        if (group.isNotEmpty()) groupHeight -= verticalSpacing

        val height = max(groupHeight, maxChildBranchHeight) + 2 * groupPadding
        segmentHeightCache[segId] = height
        return height
    }

    private fun createViewForFBox(fBox: FBox): ViewBox {
        return ViewBox(context).apply {
            this.fBox = fBox
            this.background = null
            this.onBoxClickListener = this@LayoutZoomPan
        }
    }

    private fun calculateContentBounds(): RectF {
        if (childCount == 0) return RectF()
        var minX = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            minX = min(minX, child.x)
            maxX = max(maxX, child.x + child.width)
            minY = min(minY, child.y)
            maxY = max(maxY, child.y + child.height)
        }
        return RectF(minX, minY, maxX, maxY)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(translateX, translateY)
        drawBoundingBoxes(canvas)
        super.dispatchDraw(canvas)
        drawConnections(canvas)
        canvas.restore()
    }

    private fun drawBoundingBoxes(canvas: Canvas) {
        boundingBoxes.values.forEach { rect ->
            canvas.drawRect(rect, boundingBoxPaint)
        }
    }

    private fun drawConnections(canvas: Canvas) {
        val eventBox = boxList.firstOrNull { it is FBoxEventDevice } as? FBoxEventDevice

        if (eventBox != null) {
            val startBoxRect = boxPositions[eventBox]
            val firstBox = firstBoxInSegment[eventBox.targetSegId]
            val endBoundingBox = segmentPositions[eventBox.targetSegId]

            if (startBoxRect != null && firstBox != null && endBoundingBox != null) {
                val endBoxRect = boxPositions[firstBox]
                if (endBoxRect != null) {
                    val startX = startBoxRect.right
                    val startY = startBoxRect.centerY()
                    val endX = endBoundingBox.left
                    val endY = endBoxRect.centerY()
                    canvas.drawLine(startX, startY, endX, endY, connectionPaint)
                }
            }
        }

        boxList.filterIsInstance<FBoxActionControlDevice>().forEach { box ->
            val startBoxRect = boxPositions[box]
            if (startBoxRect != null) {
                if (box.positiveSegId != 0) {
                    val firstBox = firstBoxInSegment[box.positiveSegId]
                    val endBoundingBox = segmentPositions[box.positiveSegId]
                    if (firstBox != null && endBoundingBox != null) {
                        val endBoxRect = boxPositions[firstBox]
                        if (endBoxRect != null) {
                            val startX = startBoxRect.right
                            val startY = startBoxRect.centerY()
                            val endX = endBoundingBox.left
                            val endY = endBoxRect.centerY()
                            canvas.drawLine(startX, startY, endX, endY, connectionPaint)
                        }
                    }
                }

                if (box.negativeSegId != 0) {
                    val firstBox = firstBoxInSegment[box.negativeSegId]
                    val endBoundingBox = segmentPositions[box.negativeSegId]
                    if (firstBox != null && endBoundingBox != null) {
                        val endBoxRect = boxPositions[firstBox]
                        if (endBoxRect != null) {
                            val startX = startBoxRect.right
                            val startY = startBoxRect.centerY()
                            val endX = endBoundingBox.left
                            val endY = endBoxRect.centerY()
                            canvas.drawLine(startX, startY, endX, endY, connectionPaint)
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onBoxClick(box: FBox?) {
        onBoxClickListener?.onBoxClick(box)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            val newTranslateX = translateX - distanceX
            val newTranslateY = translateY

            val effectiveContentWidth = contentWidth * scaleFactor
            val maxPanX = 0f
            val minPanX = width - effectiveContentWidth

            translateX = if (effectiveContentWidth > width) {
                newTranslateX.coerceIn(minPanX, maxPanX)
            } else {
                0f
            }

            translateY = newTranslateY

            invalidate()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is ViewBox) {
                    val childRect = RectF(child.x + translateX, child.y + translateY, child.x + child.width + translateX, child.y + child.height + translateY)
                    if (childRect.contains(e.x, e.y)) {
                        child.performClick()
                        return true
                    }
                }
            }
            return false
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}