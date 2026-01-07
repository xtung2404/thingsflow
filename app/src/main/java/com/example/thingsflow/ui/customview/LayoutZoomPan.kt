package com.example.thingsflow.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

class LayoutZoomPan @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private var translateX = 0f
    private var translateY = 0f
    private var contentWidth = 0f
    private var contentHeight = 0f

    private val gestureDetector: GestureDetector

    var onBoxClickListener: ViewBox.OnBoxClickListener? = null
    var onBoxActionListener: OnBoxActionListener? = null

    var isEditMode: Boolean = false
        set(value) {
            field = value
            invalidate() // redraw khi đổi mode
        }
    var boxList: ArrayList<FBox?> = arrayListOf()
        set(value) {
            field = value
            setupFlowLayout()
            invalidate()
        }

    private val boundingBoxes = mutableMapOf<String, RectF>()
    private val boxPositions = mutableMapOf<FBox, RectF>()
    private val segmentPositions = mutableMapOf<String, RectF>()
    private val firstBoxInSegment = mutableMapOf<String, FBox>()

    private val connectionPaint = Paint().apply {
        color = resources.getColor(R.color.flow_box_connections, null)
        strokeWidth = dpToPx(1f)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val boundingBoxPaint = Paint().apply {
        color = resources.getColor(R.color.bg_segment, null)
        style = Paint.Style.FILL
        strokeWidth = dpToPx(2f)
        isAntiAlias = true
    }

    private val horizontalSpacing = dpToPx(50f)
    private val verticalSpacing = dpToPx(20f)
    private val groupPadding = dpToPx(16f)
    private val startXPadding = dpToPx(20f)
    private val topMargin = dpToPx(50f)

    private lateinit var actionBoxesBySegId: Map<String, List<FBoxAction>>
    private val segmentHeightCache = mutableMapOf<String, Float>()

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
        contentWidth = 0f

        val nonNullBoxes = boxList.filterNotNull()
        val eventBox = nonNullBoxes.firstOrNull { it is FBoxEventDevice } as? FBoxEventDevice
        actionBoxesBySegId = nonNullBoxes
            .filterIsInstance<FBoxAction>()
            .groupBy { it.segId }

        // 1) Thêm EventBox (không có ô xám)
        eventBox?.let {
            val view = createViewForFBox(it)
            addView(view)
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            view.x = startXPadding
            view.y = topMargin
            boxPositions[it] =
                RectF(view.x, view.y, view.x + view.measuredWidth, view.y + view.measuredHeight)
        }

        // 2) Tính chiều cao tất cả segment
        actionBoxesBySegId.keys.forEach { segId ->
            calculateSegmentHeight(segId, actionBoxesBySegId)
        }

        // 3) Bố trí các segment theo BFS
        val queue = LinkedList<Pair<String, FBox?>>()
        val processed = mutableSetOf<String>()
        eventBox?.targetSegId?.let { queue.add(Pair(it, eventBox)) }
        val branchYPositions = mutableMapOf<String, Float>()

        while (queue.isNotEmpty()) {
            val (segId, parentBox) = queue.poll()
            if (processed.contains(segId)) continue
            processed.add(segId)

            val group = actionBoxesBySegId[segId] ?: continue

            // Ưu tiên box có nhánh
            val boxesInSegment = group.sortedWith(
                compareByDescending<FBoxAction> { (!it.positiveSegId.isNullOrEmpty() || !it.negativeSegId.isNullOrEmpty()) }
            )

            // đo kích thước group
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

            // X -> ngay sau parent
            val parentBoxRect = parentBox?.let { boxPositions[it] }
            val groupLeft =
                if (parentBoxRect != null) parentBoxRect.right + horizontalSpacing else startXPadding

            // ✅ Y -> căn top segment con = top ô xám của segment cha (nếu có)
            var boundingBoxTop = topMargin
            if (parentBox != null && parentBoxRect != null) {
                if (parentBox is FBoxAction) {
                    val parentSegTop = segmentPositions[parentBox.segId]?.top ?: parentBoxRect.top

                    val branches = mutableListOf<String>()
                    if (parentBox.positiveSegId.isNotEmpty()) branches.add(parentBox.positiveSegId)
                    if (parentBox.negativeSegId.isNotEmpty()) branches.add(parentBox.negativeSegId)

                    if (branches.isNotEmpty()) {
                        var currentY = parentSegTop // ← bắt đầu từ đỉnh ô xám cha
                        branches.forEach { branchSegId ->
                            branchYPositions[branchSegId] = currentY
                            val segH = segmentHeightCache[branchSegId] ?: 0f
                            currentY += segH + verticalSpacing
                        }
                        boundingBoxTop = branchYPositions[segId] ?: parentSegTop
                    } else {
                        // cha không rẽ nhánh → con duy nhất: top = top ô xám cha
                        boundingBoxTop = parentSegTop
                    }
                } else {
                    // parent là Event (không có ô xám) → dùng top của event
                    boundingBoxTop = parentBoxRect.top
                }
            }

            // tạo ô xám segment
            val segmentRect = RectF(
                groupLeft, boundingBoxTop,
                groupLeft + totalGroupWidth, boundingBoxTop + contentHeightOfSegment
            )
            boundingBoxes[segId] = segmentRect
            segmentPositions[segId] = segmentRect

            // đặt các box trong segment
            var currentBoxY = boundingBoxTop + groupPadding
            boxesInSegment.forEach { box ->
                val view = createViewForFBox(box)
                addView(view)
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

                view.x = groupLeft + groupPadding
                view.y = currentBoxY
                boxPositions[box] =
                    RectF(view.x, view.y, view.x + view.measuredWidth, view.y + view.measuredHeight)

                if (firstBoxInSegment[segId] == null) {
                    firstBoxInSegment[segId] = box
                }
                currentBoxY += view.measuredHeight + verticalSpacing
            }

            // queue nhánh
            boxesInSegment.forEach { box ->
                if (!box.positiveSegId.isNullOrEmpty() && !processed.contains(box.positiveSegId)) {
                    queue.add(Pair(box.positiveSegId, box))
                }
                if (!box.negativeSegId.isNullOrEmpty() && !processed.contains(box.negativeSegId)) {
                    queue.add(Pair(box.negativeSegId, box))
                }
            }

            contentWidth = max(contentWidth, groupLeft + totalGroupWidth)
        }

        // 4) Căn lại eventBox theo top của box đầu tiên ở target segment (nếu bạn muốn vậy)
        eventBox?.let { ev ->
            val firstTargetBox = firstBoxInSegment[ev.targetSegId]
            val targetRect = firstTargetBox?.let { boxPositions[it] }
            val evView = findViewByFBox(ev)
            if (targetRect != null && evView != null) {
                evView.y = targetRect.top
                boxPositions[ev] = RectF(
                    evView.x,
                    evView.y,
                    evView.x + evView.measuredWidth,
                    evView.y + evView.measuredHeight
                )
            }
        }

        contentHeight = calculateContentBounds().height()
    }

    private fun calculateSegmentHeight(
        segId: String,
        map: Map<String, List<FBoxAction>>
    ): Float {
        if (segmentHeightCache.containsKey(segId)) {
            return segmentHeightCache[segId]!!
        }
        val group = map[segId] ?: return 0f
        var groupHeight = 0f
        var maxChildBranchHeight = 0f

        group.forEach { box ->
            val tempView = createViewForFBox(box)
            tempView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            groupHeight += tempView.measuredHeight + verticalSpacing

            val branches = mutableListOf<String>()
            if (!box.positiveSegId.isNullOrEmpty()) branches.add(box.positiveSegId)
            if (!box.negativeSegId.isNullOrEmpty()) branches.add(box.negativeSegId)

            if (branches.isNotEmpty()) {
                var currentBranchHeight = 0f
                branches.forEach { branchId ->
                    currentBranchHeight += calculateSegmentHeight(branchId, map)
                }
                currentBranchHeight += (branches.size - 1) * verticalSpacing
                maxChildBranchHeight = max(maxChildBranchHeight, currentBranchHeight)
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
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
    }

    private fun findViewByFBox(target: FBox): ViewBox? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewBox && child.fBox == target) return child
        }
        return null
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
        if (isEditMode) {
            drawAddRemoveButtons(canvas) // ✅ chỉ vẽ khi edit
        }
        canvas.restore()
    }

    private fun drawBoundingBoxes(canvas: Canvas) {
        boundingBoxes.values.forEach { rect ->
            canvas.drawRect(rect, boundingBoxPaint)
        }
    }

    private fun drawConnections(canvas: Canvas) {
        val circleRadius = dpToPx(6f)
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.flow_box_connections, null)
            style = Paint.Style.FILL
        }
        val path = Path()
        val spacing = dpToPx(16f) // Phải khớp với spacing trong drawAddRemoveButtons

        // Vẽ kết nối từ EventBox
        val eventBox = boxList.firstOrNull { it is FBoxEventDevice } as? FBoxEventDevice
        if (eventBox != null) {
            val startBoxRect = boxPositions[eventBox]
            val firstBox = firstBoxInSegment[eventBox.targetSegId]
            val endBoundingBox = segmentPositions[eventBox.targetSegId]
            if (startBoxRect != null && firstBox != null && endBoundingBox != null) {
                val endBoxRect = boxPositions[firstBox]
                if (endBoxRect != null) {
                    val startX = startBoxRect.right
                    val startY = startBoxRect.centerY() // Event Box luôn nối từ trung điểm cạnh phải

                    val endX = endBoundingBox.left
                    val endY = endBoxRect.centerY()
                    val midX = startX + (endX - startX) / 2

                    path.reset()
                    path.moveTo(startX, startY)
                    path.lineTo(midX, startY)
                    path.lineTo(midX, endY)
                    path.lineTo(endX, endY)

                    canvas.drawPath(path, connectionPaint)
                    canvas.drawCircle(startX, startY, circleRadius, circlePaint)
                    canvas.drawCircle(endX, endY, circleRadius, circlePaint)
                }
            }
        }

        // Vẽ kết nối giữa các ActionBoxes
        boxList.filterIsInstance<FBoxAction>().forEach { box ->
            val startBoxRect = boxPositions[box]
            if (startBoxRect != null) {
                val rightCx = startBoxRect.right // Tọa độ X xuất phát luôn là cạnh phải

                // Mặc định là trung điểm (cho FBoxAction thường)
                var startYPositive = startBoxRect.centerY()
                var startYNegative = startBoxRect.centerY()

                if (box is FBoxActionConditionGeneral || box is FBoxActionConditionDeviceState) {
                    // Điều chỉnh cho Condition Box: xuất phát từ tâm nút "+"
                    startYPositive = startBoxRect.centerY() - spacing // Top/Positive Branch
                    startYNegative = startBoxRect.centerY() + spacing // Bottom/Negative Branch
                }

                // Nhánh Positive
                if (!box.positiveSegId.isNullOrEmpty()) {
                    val firstBox = firstBoxInSegment[box.positiveSegId]
                    val endBoundingBox = segmentPositions[box.positiveSegId]
                    if (firstBox != null && endBoundingBox != null) {
                        val endBoxRect = boxPositions[firstBox]
                        if (endBoxRect != null) {
                            val startX = rightCx
                            val startY = startYPositive // SỬA: Lấy Y cho nhánh Positive

                            val endX = endBoundingBox.left
                            val endY = endBoxRect.centerY()
                            val midX = startX + (endX - startX) / 2

                            path.reset()
                            path.moveTo(startX, startY)
                            path.lineTo(midX, startY)
                            path.lineTo(midX, endY)
                            path.lineTo(endX, endY)

                            canvas.drawPath(path, connectionPaint)
                            canvas.drawCircle(startX, startY, circleRadius, circlePaint)
                            canvas.drawCircle(endX, endY, circleRadius, circlePaint)
                        }
                    }
                }

                // Nhánh Negative
                if (!box.negativeSegId.isNullOrEmpty()) {
                    val firstBox = firstBoxInSegment[box.negativeSegId]
                    val endBoundingBox = segmentPositions[box.negativeSegId]
                    if (firstBox != null && endBoundingBox != null) {
                        val endBoxRect = boxPositions[firstBox]
                        if (endBoxRect != null) {
                            val startX = rightCx
                            val startY = startYNegative // SỬA: Lấy Y cho nhánh Negative

                            val endX = endBoundingBox.left
                            val endY = endBoxRect.centerY()
                            val midX = startX + (endX - startX) / 2

                            path.reset()
                            path.moveTo(startX, startY)
                            path.lineTo(midX, startY)
                            path.lineTo(midX, endY)
                            path.lineTo(endX, endY)

                            canvas.drawPath(path, connectionPaint)
                            canvas.drawCircle(startX, startY, circleRadius, circlePaint)
                            canvas.drawCircle(endX, endY, circleRadius, circlePaint)
                        }
                    }
                }
            }
        }
    }
    private fun drawAddRemoveButtons(canvas: Canvas) {
        val buttonRadius = dpToPx(12f)
        val circleDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        val circlePositivePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#458500")
            style = Paint.Style.FILL
        }
        val circleNegativePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#B00020")
            style = Paint.Style.FILL
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = dpToPx(12f)
            textAlign = Paint.Align.CENTER
        }

        boxList.filterIsInstance<FBox>().forEach { box ->
            val rect = boxPositions[box] ?: return@forEach
            val centerY = rect.centerY()

            if (box is FBoxEvent) {
                if (box is FBoxEventDevice) {
                    if (box.attrTypes == null &&
                        box.devType == 0 &&
                        box.devId.isNullOrEmpty()
                        ) {
                        return@forEach
                    }
                 }
                if (box.targetSegId.isNullOrEmpty()) {
                    val rect = boxPositions[box] ?: return@forEach
                    val centerY = rect.centerY()

                    // Nút "+" ngay trung điểm cạnh phải
                    val rightCx = rect.right
                    val rightCy = centerY
                    canvas.drawCircle(rightCx, rightCy, buttonRadius, circleDefaultPaint)
                    canvas.drawText("+", rightCx, rightCy + (textPaint.textSize / 3), textPaint)
                }
            }

                if (box is FBoxActionConditionGeneral) {
                    // Vị trí ngang vẫn là cạnh phải
                    val rightCx = rect.right

                    // ✅ Điều chỉnh khoảng cách dọc rất nhỏ (ví dụ 2dp) để gần trung điểm
                    val spacing = dpToPx(16f)

                    if (box.positiveSegId.isNullOrEmpty() && box.negativeSegId.isNullOrEmpty()) {
                        // Nút "-" ở cạnh trái (Giữ nguyên)
                        val leftCx = rect.left
                        canvas.drawCircle(leftCx, centerY, buttonRadius, circleDefaultPaint)
                        canvas.drawText("-", leftCx, centerY + (textPaint.textSize / 3), textPaint)
                    }
                    if (box.positiveSegId.isNullOrEmpty()) {
                        // Nút "+" trên (Positive - Nằm ngay trên centerY)
                        val topCy = centerY - spacing
                        canvas.drawCircle(rightCx, topCy, buttonRadius, circlePositivePaint)
                        canvas.drawText("+", rightCx, topCy + (textPaint.textSize / 3), textPaint)
                    }
                    if (box.negativeSegId.isNullOrEmpty()) {
                        // Nút "+" dưới (Negative - Nằm ngay dưới centerY)
                        val bottomCy = centerY + spacing
                        canvas.drawCircle(rightCx, bottomCy, buttonRadius, circleNegativePaint)
                        canvas.drawText("+", rightCx, bottomCy + (textPaint.textSize / 3), textPaint)
                    }
                    return@forEach
                }
//            }

            // ✅ 1. Nút "+" cho box không có nhánh con
            if (box is FBoxAction && box.positiveSegId.isNullOrEmpty() && box.negativeSegId.isNullOrEmpty()) {
                val rightCx = rect.right
                val rightCy = centerY
                canvas.drawCircle(rightCx, rightCy, buttonRadius, circleDefaultPaint)
                canvas.drawText("+", rightCx, rightCy + (textPaint.textSize / 3), textPaint)

                val leftCx = rect.left
                val leftCy = centerY
                canvas.drawCircle(leftCx, leftCy, buttonRadius, circleDefaultPaint)
                canvas.drawText("-", leftCx, leftCy + (textPaint.textSize / 3), textPaint)
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEditMode && event.action == MotionEvent.ACTION_UP) {
            val touchX = event.x - translateX
            val touchY = event.y - translateY
            val buttonRadius = dpToPx(12f)
            val spacing = dpToPx(16f) // Phải khớp với spacing trong drawAddRemoveButtons

            boxList.filterIsInstance<FBox>().forEach { box ->
                val rect = boxPositions[box] ?: return@forEach
                val centerY = rect.centerY()
                val leftCx = rect.left
                val rightCx = rect.right

                // ✅ Xử lý FBoxActionConditionGeneral (có 2 nút +)
                if (box is FBoxActionConditionGeneral || box is FBoxActionConditionGeneral) {
                    if (box.positiveSegId.isNullOrEmpty() && box.negativeSegId.isNullOrEmpty()) {

                        // --- Nút "-" (Remove) ---
                        val distToLeft = (touchX - leftCx) * (touchX - leftCx) +
                                (touchY - centerY) * (touchY - centerY)
                        if (distToLeft <= buttonRadius * buttonRadius) {
                            onBoxActionListener?.onRemoveBoxClicked(box)
                            return true
                        }
                    }

                    if (box.positiveSegId.isNullOrEmpty()) {
                        // --- Nút "+" trên (Positive) ---
                        val topCy = centerY - spacing
                        val distToTopRight = (touchX - rightCx) * (touchX - rightCx) +
                                (touchY - topCy) * (touchY - topCy)
                        if (distToTopRight <= buttonRadius * buttonRadius) {
                            // Gọi listener với nhánh POSITIVE
                            onBoxActionListener?.onAddBoxPositiveClicked(box, OnBoxActionListener.NewSegType.POSITIVE)
                            return true
                        }
                    }

                    if (box.negativeSegId.isNullOrEmpty()) {
                        // --- Nút "+" dưới (Negative) ---
                        val bottomCy = centerY + spacing
                        val distToBottomRight = (touchX - rightCx) * (touchX - rightCx) +
                                (touchY - bottomCy) * (touchY - bottomCy)
                        if (distToBottomRight <= buttonRadius * buttonRadius) {
                            // Gọi listener với nhánh NEGATIVE
                            onBoxActionListener?.onAddBoxPositiveClicked(box, OnBoxActionListener.NewSegType.NEGATIVE)
                            return true
                        }
                    }

                    // Nếu là Condition Box đã xử lý xong, chuyển sang box tiếp theo
                    return@forEach
                }

                // ✅ Xử lý FBoxEvent (Có 1 nút +)
                if (box is FBoxEvent) {
                    if (box.id != null && box.targetSegId.isNullOrEmpty()) {
                        // Check nút "+"
                        val distToRight = (touchX - rightCx) * (touchX - rightCx) +
                                (touchY - centerY) * (touchY - centerY)
                        if (distToRight <= buttonRadius * buttonRadius) {
                            // Gọi listener với nhánh DEFAULT
                            onBoxActionListener?.onAddBoxPositiveClicked(box, OnBoxActionListener.NewSegType.DEFAULT)
                            return true
                        }
                    }
                }

                // ✅ Xử lý FBoxAction thường (Có 1 nút +)
                if (box is FBoxAction) {
                    if (box.positiveSegId.isNullOrEmpty() && box.negativeSegId.isNullOrEmpty()) {
                        // Check nút "-" (Remove)
                        val distToLeft = (touchX - leftCx) * (touchX - leftCx) +
                                (touchY - centerY) * (touchY - centerY)
                        if (distToLeft <= buttonRadius * buttonRadius) {
                            onBoxActionListener?.onRemoveBoxClicked(box)
                            return true
                        }

                        // Check nút "+" (Add)
                        val distToRight = (touchX - rightCx) * (touchX - rightCx) +
                                (touchY - centerY) * (touchY - centerY)
                        if (distToRight <= buttonRadius * buttonRadius) {
                            // Gọi listener với nhánh DEFAULT
                            onBoxActionListener?.onAddBoxPositiveClicked(box,
                                OnBoxActionListener.NewSegType.DEFAULT
                            )
                            return true
                        }
                    }
                }
            }
            gestureDetector.onTouchEvent(event)
            return true
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
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
            val touchX = e.x - translateX
            val touchY = e.y - translateY

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is ViewBox) {
                    val childRect = RectF(
                        child.x, child.y,
                        child.x + child.width, child.y + child.height
                    )
                    if (childRect.contains(touchX, touchY)) {
                        // Gọi trực tiếp listener của box
                        onBoxClickListener?.onBoxClick(child.fBox)
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

    interface OnBoxActionListener {
        enum class NewSegType() {
            DEFAULT,
            POSITIVE,
            NEGATIVE
        }
        fun onAddBoxPositiveClicked(box: FBox, newSegType: NewSegType)
        fun onRemoveBoxClicked(box: FBox)
    }
}
