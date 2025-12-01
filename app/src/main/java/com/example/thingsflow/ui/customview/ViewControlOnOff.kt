package com.example.thingsflow.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.example.thingsflow.R
import com.example.thingsflow.utils.getDeviceTypeLabel
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGPT
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGemini
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionCodeFunction
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionTime
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDLearn
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRecognize
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRemove
import rogo.iot.module.flowcommon.box.action.FBoxActionHandlerAnotherBox
import rogo.iot.module.flowcommon.box.action.FBoxActionPublishMqtt
import rogo.iot.module.flowcommon.box.action.FBoxActionSendWebSocket
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
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.platform.define.IoTCmdConst
import java.util.Arrays

class ViewControlOnOff @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val TAG = "ViewControlOnOff"
    private var btnOn: AppCompatButton
    private var btnOff: AppCompatButton


    init {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_control_on_off, this, true)

        btnOn = findViewById<AppCompatButton>(R.id.btn_on)
        btnOff = findViewById<AppCompatButton>(R.id.btn_off)
    }

    fun initView(
        onOnOffSelected: (IntArray) -> Unit
    ) {
        btnOn.setOnClickListener {
            ILogR.D(TAG, "initView: on")
            onOnOffSelected.invoke(
                intArrayOf(
                    IoTAttribute.ACT_ONOFF,
                    IoTCmdConst.POWER_ON
                )
            )
        }

        btnOff.setOnClickListener {
            ILogR.D(TAG, "initView: off")
            onOnOffSelected.invoke(
                intArrayOf(
                    IoTAttribute.ACT_ONOFF,
                    IoTCmdConst.POWER_OFF
                )
            )
        }
    }

    fun updateButtonState(actionValue: IntArray?) {
        // Nếu không có hành động nào (chưa được chọn), reset về trạng thái mặc định
        if (actionValue == null || actionValue.size < 2) {
            btnOn.setBackgroundResource(R.drawable.bg_gray) // Thay bằng background mặc định của bạn
            btnOff.setBackgroundResource(R.drawable.bg_gray) // Thay bằng background mặc định của bạn
            return
        }

        // Kiểm tra giá trị hành động để đặt background
        when (actionValue[1]) {
            IoTCmdConst.POWER_ON -> {
                // Cập nhật background cho trạng thái BẬT
                btnOn.setBackgroundResource(R.drawable.bg_light_gray_stroke_blue) // Thay bằng background khi được chọn
                btnOff.setBackgroundResource(R.drawable.bg_gray)
            }
            IoTCmdConst.POWER_OFF -> {
                // Cập nhật background cho trạng thái TẮT
                btnOn.setBackgroundResource(R.drawable.bg_gray)
                btnOff.setBackgroundResource(R.drawable.bg_light_gray_stroke_blue) // Thay bằng background khi được chọn
            }
        }
    }


}
