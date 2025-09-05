package com.heptacreation.sumamente.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.heptacreation.sumamente.R

object InternalMessageDialog {

    fun show(
        activity: BaseActivity,
        titleRes: Int,
        bodyRes: Int,
        onNotNow: () -> Unit,
        onGo: () -> Unit
    ): Dialog {
        val ctx: Context = activity
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_link_unlink_account, null, false)

        val tvTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvDialogMessage)
        val btnCancel = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnAction = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAction)

        btnCancel.setText(R.string.dialog_btn_not_now)
        btnAction.setText(R.string.dialog_btn_go)

        tvTitle.setText(titleRes)
        tvMessage.setText(bodyRes)

        val dialog = AlertDialog.Builder(ctx)
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancel.setOnClickListener {
            onNotNow()
            dialog.dismiss()
        }
        btnAction.setOnClickListener {
            onGo()
        }

        dialog.show()
        return dialog
    }

    fun showUnderConstruction(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(R.string.dialog_under_construction)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
