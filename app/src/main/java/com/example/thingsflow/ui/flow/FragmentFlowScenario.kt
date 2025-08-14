package com.example.thingsflow.ui.flow

import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.customview.ViewBox
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.define.IoTDeviceType

@AndroidEntryPoint
class FragmentFlowScenario : FragmentBase<FragmentFlowScenarioBinding>(), ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario

    private val TAG = "FragmentFlowScenario"

    override fun initView() {
        super.initView()
        binding.apply {
            // Khởi tạo một danh sách FBox mới
            val boxes = mutableListOf<FBox>()

            val fBoxEvent = FBoxEventDevice()
            fBoxEvent.devId = "123"
            fBoxEvent.devType = IoTDeviceType.SWITCH
            fBoxEvent.targetSegId = 1
            boxes.add(fBoxEvent)

            val fActionBox = FBoxActionControlDevice()
            fActionBox.devId = "1222"
            fActionBox.segId = 1
            boxes.add(fActionBox)

            val fActionBox1 = FBoxActionControlDevice()
            fActionBox1.devId = "1223"
            fActionBox1.segId = 1
            fActionBox1.positiveSegId = 2
            fActionBox1.negativeSegId = 3
            boxes.add(fActionBox1)

            val fActionBox2 = FBoxActionControlDevice()
            fActionBox2.devId = "12223"
            fActionBox2.segId = 2
            fActionBox2.positiveSegId = 6
            fActionBox2.negativeSegId = 7
            boxes.add(fActionBox2)

            val fActionBox3 = FBoxActionControlDevice()
            fActionBox3.devId = "12224"
            fActionBox3.segId = 3
            fActionBox3.negativeSegId = 4
            fActionBox3.positiveSegId = 5
            boxes.add(fActionBox3)

            val fActionBox4 = FBoxActionControlDevice()
            fActionBox4.devId = "12224"
            fActionBox4.segId = 4
            boxes.add(fActionBox4)

            val fActionBox5 = FBoxActionControlDevice()
            fActionBox5.devId = "12224"
            fActionBox5.segId = 5
            boxes.add(fActionBox5)

            val fActionBox6 = FBoxActionControlDevice()
            fActionBox6.devId = "12224"
            fActionBox6.segId = 6
            boxes.add(fActionBox6)

            val fActionBox7 = FBoxActionControlDevice()
            fActionBox7.devId = "12224"
            fActionBox7.segId = 7
            boxes.add(fActionBox7)
            // Gán danh sách cho boxList của LayoutZoomPan
            boxLayout.boxList = ArrayList(boxes)

            binding.boxLayout.onBoxClickListener = this@FragmentFlowScenario
        }
    }

    override fun initAction() {
        super.initAction()
    }

    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick")
    }
}