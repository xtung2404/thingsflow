package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigOutputJsonBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterJsonField
import com.example.thingsflow.ui.dialog.DialogAddAndEditJsonField
import rogo.iot.module.flowcommon.define.FJsonField

class OverlayConfigOutputJson(
    context: Context,
    container: ViewGroup,
    private val onOutputConfigured: (Array<FJsonField>) -> Unit,
    private val onClose: () -> Unit
) : OverlayBase<LayoutOverlayConfigOutputJsonBinding>(
    context,
    container,
    LayoutOverlayConfigOutputJsonBinding::inflate
) {
    private var fieldList = arrayListOf<FJsonField>()

    // Adapter gốc quản lý toàn bộ cấu trúc cây
    private val adapterJsonField: AdapterJsonField by lazy {
        AdapterJsonField(
            onMenuClick = { parentField, returnToChild ->
                // Mở dialog khi nhấn menu ở bất kỳ node nào (cha, con, cháu...)
                dialogAddAndEditJsonField.setCallback { key, type ->
                    val newField = FJsonField().apply {
                        this.label = key
                        this.type = type
                        this.jsonPath = parentField.jsonPath + "." + "${key}"
                        this.fields = arrayOf()
                    }

                    // 1. Dữ liệu thấm vào Reference của node cha
                    returnToChild(newField)

                    // 2. Ép Root Adapter quét lại cây để tính toán lại chiều cao
                    // notifyDataSetChanged() ở root là cách duy nhất để node con nở ra
                    adapterJsonField.notifyDataSetChanged()
                    dialogAddAndEditJsonField.dismiss()
                }
                dialogAddAndEditJsonField.show()
            },
            onNotifyParent = {
                // Tín hiệu Bubble up từ các node sâu gửi về
                adapterJsonField.notifyDataSetChanged()
            }
        )
    }

    private val dialogAddAndEditJsonField by lazy {
        DialogAddAndEditJsonField(context) { key, type ->
            fieldList.add(
                FJsonField().apply {
                    this.label = key
                    this.type = type
                    this.jsonPath = "." + "${key}"
                    this.fields = arrayOf()
                }
            )
            adapterJsonField.submitList(fieldList.toList())
            adapterJsonField.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(binding: LayoutOverlayConfigOutputJsonBinding) {

    }

    override fun initUI() {
        super.initUI()
        binding.rvJson.apply {
            // Không set layoutManager ở XML nữa, set ở đây cho chắc chắn
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = adapterJsonField
        }

        adapterJsonField.submitList(fieldList.toList())

        binding.btnBack.setOnClickListener { onClose.invoke() }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnSave.setOnClickListener {
                onOutputConfigured.invoke(fieldList.toTypedArray())
            }

            btnMenu.setOnClickListener {
                dialogAddAndEditJsonField.setCallback { key, type ->
                    fieldList.add(
                        FJsonField().apply {
                            this.label = key
                            this.type = type
                            this.jsonPath = key
                            this.fields = arrayOf()
                        })
                    adapterJsonField.submitList(fieldList.toList())
                    adapterJsonField.notifyDataSetChanged()
                    dialogAddAndEditJsonField.dismiss()
                }
                dialogAddAndEditJsonField.show()
            }
        }
    }

    override fun show() {
        super.show()
        fieldList = arrayListOf()
        adapterJsonField.submitList(fieldList)
    }

}