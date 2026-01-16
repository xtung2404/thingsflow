package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoFlowBase
import com.example.thingsflow.ui.customview.LayoutZoomPan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import javax.inject.Inject

@HiltViewModel
class VMFlowScene
@Inject constructor(val repo: RepoFlowBase) : VMFlowBase(repo)
{
    private val TAG = "VMFlowScenario"

    //store uuid of the parent box
    private var rootBoxId: String?= null

    init {
        initScenario()
    }

    //initialize a new flow with a default FBoxEventDevice
    fun initScenario() {
        val currentBoxes = arrayListOf<FBox>()
        val fBoxEvent = FBoxEventDevice()
        currentBoxes.add(fBoxEvent)

        _boxes.value = ArrayList(currentBoxes)
    }

    fun setRootBoxId(id: String?) {
        rootBoxId = id
    }

    fun getRootBoxId(): String?= rootBoxId

    /**
     * Configures a box before adding it to the list of boxes
     * It calculate the next available 'id' and 'segId' based on existing boxes
     * and links FBoxAction boxes to their parent box.
     */
    fun configureNewBox(
        fBox: FBox,
        newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType?
    ) {
        viewModelScope.launch {
            val currentBoxes = _boxes.value ?: arrayListOf()
            _boxes.value = ArrayList(
                repo.generateBoxInfo(
                    rootBoxId = rootBoxId,
                    fBox = fBox,
                    newSegType = newSegType,
                    rootBoxes = currentBoxes
            ))
        }
    }

    fun createFlowScene(
        flowSceneId: String,
        devId: String,
        flowSceneLabel: String
    ) {
        viewModelScope.launch {
            repo.createFlowScene(flowSceneId, devId, flowSceneLabel)
        }
    }

    fun createSceneBoxes(
        flowSceneId: String,
        devId: String,
        boxes: ArrayList<FBox>
    ) {
        viewModelScope.launch {
            repo.createSceneBoxes(
                flowSceneId,
                devId,
                boxes
            )
        }
    }
}