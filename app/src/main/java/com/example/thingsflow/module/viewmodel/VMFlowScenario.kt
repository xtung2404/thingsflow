package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thingsflow.module.repository.RepoFlowScenario
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.platform.ILogR
import javax.inject.Inject

@HiltViewModel
class VMFlowScenario
@Inject constructor(val repo: RepoFlowScenario) :ViewModel()
{
    private val TAG = "VMFlowScenario"
    //store boxes of flow scenario
    private val _boxes = MutableLiveData<ArrayList<FBox>>(arrayListOf<FBox>())
    val boxes: LiveData<ArrayList<FBox>> = _boxes

    //store uuid of the parent box
    private var rootBoxId: String?= null

    init {
        initScenario()
    }

    //initialize a new flow with a default FBoxEventDevice
    private fun initScenario() {
        val currentBoxes = _boxes.value ?: arrayListOf()
        if (currentBoxes.isEmpty()) {
            val fBoxEvent = FBoxEventDevice()
            currentBoxes.add(fBoxEvent)

            _boxes.value = ArrayList(currentBoxes)
        }
    }

    fun setRootBoxId(id: String?) {
        rootBoxId = id
    }

    /**
     * Configures a box before adding it to the list of boxes
     * It calculate the next available 'id' and 'segId' based on existing boxes
     * and links FBoxAction boxes to their parent box.
     */
    fun configBox(fBox: FBox, newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType?) {
        val currentBoxes = _boxes.value ?: arrayListOf()
        var highestBoxId: Int = 0
        var highestSegId: Int = 0
        // Iterate through existing boxes to find the maximum 'id' and 'segId'
        currentBoxes.forEach { currentBox ->
            val id = currentBox.id.toInt()
            if (id > highestBoxId) {
                highestBoxId = id
            }
            if (currentBox is FBoxAction) {
                val segId: Int = currentBox.segId.toInt()
                if (segId > highestSegId) {
                    highestSegId = segId
                }
            }
        }

        when (fBox) {
            is FBoxEvent -> {
                // set ID for box event
                fBox.id = (highestBoxId + 1).toString()
            }

            is FBoxAction -> {
                //set the id of next segment for the first event box
                if (currentBoxes.size == 1) {
                    if (currentBoxes[0] is FBoxEvent) {
                        (currentBoxes[0] as FBoxEvent).targetSegId = (highestSegId + 1).toString()
                    }
                }
                // set ID for the current box action
                fBox.id = (highestBoxId + 1).toString()
                // set segmentID for the current box action
                fBox.segId = (highestSegId + 1).toString()
                // set the id of the parent box for the current box
                fBox.rootId = rootBoxId
                fBox.positiveSegId = ""
                fBox.negativeSegId = ""
                val rootBox = currentBoxes.find { it.id == rootBoxId }
                // determine if the current box belongs to positive segment or negative segment of the parent box
                if (rootBox != null && rootBox is FBoxAction) {
                    when (newSegType) {
                        LayoutZoomPan.OnBoxActionListener.NewSegType.DEFAULT,
                        LayoutZoomPan.OnBoxActionListener.NewSegType.POSITIVE -> {
                            rootBox.positiveSegId = fBox.segId
                        }

                        LayoutZoomPan.OnBoxActionListener.NewSegType.NEGATIVE -> {
                            rootBox.negativeSegId = fBox.segId
                        }

                        else -> {

                        }
                    }
                }
            }
        }
        currentBoxes.add(fBox)
        _boxes.value = currentBoxes
        _boxes.value?.forEach {
            ILogR.D(TAG, "configBox:boxInfo", Gson().toJson(it))
        }
    }

}