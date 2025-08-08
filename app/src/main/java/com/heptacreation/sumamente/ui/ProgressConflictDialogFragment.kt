package com.heptacreation.sumamente.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.DialogFragment
import com.heptacreation.sumamente.R

class ProgressConflictDialogFragment : DialogFragment() {

    interface Listener {
        fun onRestoreCloud()
        fun onKeepLocal()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Listener ?: context as? Listener
        if (listener == null) {
            throw ClassCastException("$context must implement ProgressConflictDialogFragment.Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_conflict_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.btnRestoreCloud).setOnClickListener {
            listener?.onRestoreCloud()
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.btnKeepLocal).setOnClickListener {
            listener?.onKeepLocal()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(): ProgressConflictDialogFragment = ProgressConflictDialogFragment()
    }
}
