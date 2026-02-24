package com.example.beeranking.utilis.loader

import android.content.Context
import android.widget.FrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator

class LoadingIndicator(val context: Context) {
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null
    val isLoading: Boolean
        get() = loadingDialog != null

    fun show() {
        val progressIndicator = CircularProgressIndicator(context).apply {
            isIndeterminate = true
            trackThickness = 16
            indicatorSize = 200
        }

        val container = FrameLayout(context).apply {
            addView(progressIndicator, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
            ))
            setPadding(0, 48, 0, 48)
        }

        loadingDialog = MaterialAlertDialogBuilder(context)
            .setView(container)
            .setCancelable(false)
            .create()

        loadingDialog?.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    fun hide() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}