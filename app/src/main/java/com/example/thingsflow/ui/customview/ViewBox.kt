package com.example.thingsflow.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGPT
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGemini
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionCodeFunction
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionTime
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDLearn
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRecognize
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRemove
import rogo.iot.module.flowcommon.box.action.FBoxActionHandlerAnotherBox
import rogo.iot.module.flowcommon.box.action.FBoxActionPublishMqtt
import rogo.iot.module.flowcommon.box.action.FBoxActionSendWebSocket
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventCameraStreaming
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventFaceID
import rogo.iot.module.flowcommon.box.event.FBoxEventHttpServerApi
import rogo.iot.module.flowcommon.box.event.FBoxEventIORS232
import rogo.iot.module.flowcommon.box.event.FBoxEventIORS485
import rogo.iot.module.flowcommon.box.event.FBoxEventMqtt
import rogo.iot.module.flowcommon.box.event.FBoxEventSchedule
import rogo.iot.module.flowcommon.box.event.FBoxEventStatistic
import rogo.iot.module.flowcommon.box.event.FBoxEventTimerInterval
import rogo.iot.module.flowcommon.box.event.FBoxEventTouchID
import rogo.iot.module.flowcommon.box.event.FBoxEventVoiceRecognize
import rogo.iot.module.flowcommon.box.event.FBoxEventWeather
import rogo.iot.module.platform.ILogR
import java.util.Arrays

class ViewBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val TAG = "ViewBox"
    private var downX: Float = 0f
    private var downY: Float = 0f

    private lateinit var txtBoxEvtLabel: TextView
    private lateinit var txtBoxEvtDevice: TextView
    private lateinit var txtBoxEvtAttr: TextView
    private lateinit var lnEvtContent: LinearLayout
    private lateinit var lnEvtEmpty: LinearLayout

    private lateinit var txtBoxActLabel: TextView
    private lateinit var txtBoxActType: TextView
    private lateinit var txtBoxActAction: TextView

    private lateinit var txtBoxCdtLabel: TextView
    private lateinit var txtBoxCdtInput: TextView
    private lateinit var txtBoxCdtCondition: TextView
    var fBox: FBox? = null
        set(value) {
            field = value
            updateBlockContent()
        }
    var onBlockTouched: ((FBox) -> Unit)? = null
    var onBoxClickListener: OnBoxClickListener? = null

    init {
        setOnClickListener {
            onBoxClickListener?.onBoxClick(fBox)
        }
    }

    private fun updateBlockContent() {
        fBox?.let { boxVal ->
            removeAllViews()
            val layoutResId = when (boxVal) {
                is FBoxEventDevice,
                is FBoxEventMqtt,
                is FBoxEventWeather,
                is FBoxEventSchedule,
                is FBoxEventCameraStreaming,
                is FBoxEventFaceID,
                is FBoxEventHttpServerApi,
                is FBoxEventIORS232,
                is FBoxEventIORS485,
                is FBoxEventTouchID,
                is FBoxEventVoiceRecognize,
                is FBoxEventTimerInterval,
                is FBoxEventStatistic
                -> R.layout.layout_item_block_evt

                is FBoxActionConditionGeneral,
                is FBoxActionConditionTime,
                is FBoxActionConditionDeviceState
                -> R.layout.layout_item_block_cdt

                is FBoxActionAIGPT,
                is FBoxActionAIGemini,
                is FBoxActionCallHttp,
                is FBoxActionControlDevice,
                is FBoxActionCodeFunction,
                is FBoxActionFaceIDLearn,
                is FBoxActionFaceIDRecognize,
                is FBoxActionFaceIDRemove,
                is FBoxActionHandlerAnotherBox,
                is FBoxActionPublishMqtt,
                is FBoxActionSendWebSocket
                -> R.layout.layout_item_block_act

                else -> R.layout.layout_item_block_evt
            }
            LayoutInflater.from(context).inflate(layoutResId, this, true)

            when (boxVal) {
                is FBoxEventDevice,
                is FBoxEventMqtt,
                is FBoxEventWeather,
                is FBoxEventSchedule,
                is FBoxEventCameraStreaming,
                is FBoxEventFaceID,
                is FBoxEventHttpServerApi,
                is FBoxEventIORS232,
                is FBoxEventIORS485,
                is FBoxEventTouchID,
                is FBoxEventVoiceRecognize,
                is FBoxEventTimerInterval,
                is FBoxEventStatistic
                -> {
                    txtBoxEvtLabel = findViewById<TextView>(R.id.txt_box_evt_label)
                    txtBoxEvtDevice = findViewById<TextView>(R.id.txt_box_evt_device)
                    txtBoxEvtAttr = findViewById<TextView>(R.id.txt_box_evt_attr)
                    lnEvtContent = findViewById<LinearLayout>(R.id.ln_evt_content)
                    lnEvtEmpty = findViewById<LinearLayout>(R.id.ln_evt_empty)
                    when (boxVal) {
                        is FBoxEventDevice -> {
                            ILogR.D(TAG, "updateBlockContent: BoxEventInfo ", boxVal.devType, Arrays.toString(boxVal.attrTypes), boxVal.devType)
                            if (boxVal.devId == null &&
                                boxVal.attrTypes == null &&
                                boxVal.devType == 0
                            ) {
                                lnEvtContent.visibility = View.GONE
                                lnEvtEmpty.visibility = View.VISIBLE
                            } else {
                                lnEvtContent.visibility = View.VISIBLE
                                lnEvtEmpty.visibility = View.GONE
                            }
                        }
                    }
                }

                is FBoxActionConditionGeneral,
                is FBoxActionConditionTime,
                is FBoxActionConditionDeviceState
                -> {
                    txtBoxCdtLabel = findViewById<TextView>(R.id.txt_box_cdt_label)
                    txtBoxCdtInput = findViewById<TextView>(R.id.txt_box_cdt_input)
                    txtBoxCdtCondition = findViewById<TextView>(R.id.txt_box_cdt_condition)
                }

                is FBoxActionAIGPT,
                is FBoxActionAIGemini,
                is FBoxActionCallHttp,
                is FBoxActionControlDevice,
                is FBoxActionCodeFunction,
                is FBoxActionFaceIDLearn,
                is FBoxActionFaceIDRecognize,
                is FBoxActionFaceIDRemove,
                is FBoxActionHandlerAnotherBox,
                is FBoxActionPublishMqtt,
                is FBoxActionSendWebSocket
                -> {
                    txtBoxActLabel = findViewById<TextView>(R.id.txt_box_act_label)
                    txtBoxActType = findViewById<TextView>(R.id.txt_box_act_type)
                    txtBoxActAction = findViewById<TextView>(R.id.txt_box_act_action)
                }

                else -> {
                    txtBoxEvtLabel = findViewById<TextView>(R.id.txt_box_evt_label)
                    txtBoxEvtDevice = findViewById<TextView>(R.id.txt_box_evt_device)
                    txtBoxEvtAttr = findViewById<TextView>(R.id.txt_box_evt_attr)
                }
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val currentFBox = fBox ?: return super.onTouchEvent(event)
//        return when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                downX = event.x
//                downY = event.y
//                true
//            }
//
//            MotionEvent.ACTION_UP -> {
//                val upX = event.x
//                val upY = event.y
//                val clickThreshold = 5f
//
//                if (kotlin.math.abs(upX - downX) < clickThreshold &&
//                    kotlin.math.abs(upY - downY) < clickThreshold
//                ) {
//                    onBlockTouched?.invoke(currentFBox)
//                }
//                true
//            }
//
//            else -> return super.onTouchEvent(event)
//        }
        return false
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun getRightConnectionPoint(): Pair<Float, Float> {
        val centerX = x + width
        val centerY = y + height / 2f
        return Pair(centerX, centerY)
    }

    fun getLeftConnectionPint(): Pair<Float, Float> {
        val centerX = x
        val centerY = y + height / 2f
        return Pair(centerX, centerY)
    }

    interface OnBoxClickListener {
        fun onBoxClick(box: FBox?)
    }
}
