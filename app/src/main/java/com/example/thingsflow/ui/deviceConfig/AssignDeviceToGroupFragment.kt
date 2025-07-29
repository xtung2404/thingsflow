package com.example.thingsflow.ui.deviceConfig

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentAssignDeviceToGroupBinding
import com.example.thingsflow.module.viewmodel.GroupViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.GroupSpinnerAdapter
import com.example.thingsflow.ui.dialog.DialogCreateGroup
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

@AndroidEntryPoint
class AssignDeviceToGroupFragment : BaseFragment<FragmentAssignDeviceToGroupBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_assign_device_to_group

    private val groupViewModel by activityViewModels<GroupViewModel>()
    private lateinit var groupSpinnerAdapter: GroupSpinnerAdapter
    private val dialogCreateGroup: DialogCreateGroup by lazy {
        DialogCreateGroup(
            requireContext(),
            requireActivity()
        )
    }
    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
            val availableGroups = arrayListOf<IoTGroup?>()
            availableGroups.add(null)
            availableGroups.addAll(groupViewModel.getAll())
            groupSpinnerAdapter = GroupSpinnerAdapter(
                requireContext(),
                availableGroups.toList()
            )
            spinnerGroup.adapter = groupSpinnerAdapter
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnCreate.setOnClickListener {
                dialogCreateGroup.show()
            }

            btnContinue.setOnClickListener {
                val selectedGroup = (spinnerGroup.selectedItem as IoTGroup?)
                selectedGroup?.let {
                    val groupBundle = bundleOf("group" to it.uuid)
                    findNavController().navigate(R.id.setDeviceLabelFragment, groupBundle)
                }
            }

            btnContinueNotAssign.setOnClickListener {
                val groupBundle = bundleOf("group" to null)
                findNavController().navigate(R.id.setDeviceLabelFragment, groupBundle)
            }
        }
    }
}