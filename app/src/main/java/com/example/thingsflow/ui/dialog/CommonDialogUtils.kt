package com.example.thingsflow.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogLoadingWithAnimationBinding

fun Context.showDialogLoadingWithAnimation(
    @StringRes titleRes: Int,
    @StringRes messRes: Int? = null,
    messStr: String,
    @RawRes lottieAnim: Int?= null,
    lifecycle: Lifecycle,
    isAnimLoop: Boolean = false
): Dialog {
    var isShow = false
    val dialog: Dialog
    val view: View =
        LayoutInflater.from(this).inflate(R.layout.dialog_loading_with_animation, null)
    val builder = AlertDialog.Builder(this)
        .setView(view)
        .setCancelable(false)


    dialog = builder.create()
    dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.getWindow()?.setGravity(Gravity.BOTTOM)

    val binding = DialogLoadingWithAnimationBinding.bind(view)
    binding.txtTitle.setText(titleRes)

    if (messRes!=null)
        binding.txtMessage.setText(messRes)
    else
        binding.txtMessage.text = messStr

//    binding.animationView.setAnimation(lottieAnim)
//    val repeatCount = if (isAnimLoop) -1 else 0
//    if (repeatCount != binding.animationView.repeatCount) {
//        binding.animationView.repeatCount = repeatCount
//    }

    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                if (dialog.isShowing) {
                    isShow = true
                }
                dialog.dismiss()
            }

            Lifecycle.Event.ON_RESUME -> {
                if (isShow) {
                    dialog.show()
                }
            }
            else -> {

            }
        }
    }

    lifecycle.addObserver(observer)

    if (!dialog.isShowing) {
        dialog.show()
        isShow = true
    }

    if (!dialog.isShowing) {
        dialog.show()
        isShow = true
    }

    dialog.setOnDismissListener {
        isShow = false
    }
    return dialog
}