package com.heptacreation.sumamente.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.heptacreation.sumamente.R

class LinkAccountDialogFragment : DialogFragment() {

    interface LinkAccountDialogListener {
        fun onAcceptLink()
        fun onNotNow()
        fun onRemindMeLater()
    }

    private var listener: LinkAccountDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as? LinkAccountDialogListener ?: context as? LinkAccountDialogListener
        if (listener == null) {
            throw ClassCastException("$context must implement LinkAccountDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.dialog_link_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDialogTitle: TextView = view.findViewById(R.id.tvDialogTitle)
        val tvDialogMessage: TextView = view.findViewById(R.id.tvDialogMessage)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnNotNow: Button = view.findViewById(R.id.btnNotNow)
        val btnRemindLater: Button = view.findViewById(R.id.btnRemindLater)


        val titleIds = arrayOf(
            R.string.link_prompt_title_tranquility,
            R.string.link_prompt_title_positive,
            R.string.link_prompt_title_minimalist,
            R.string.link_prompt_title_concise
        )

        val bodyIds = arrayOf(
            R.string.link_prompt_body_tranquility,
            R.string.link_prompt_body_positive,
            R.string.link_prompt_body_minimalist,
            R.string.link_prompt_body_concise
        )

        val randomIndex = titleIds.indices.random()

        tvDialogTitle.text = getString(titleIds[randomIndex])
        tvDialogMessage.text = getString(bodyIds[randomIndex])


        btnAccept.setOnClickListener {
            listener?.onAcceptLink()
            dismiss()
        }

        btnNotNow.setOnClickListener {
            listener?.onNotNow()
            dismiss()
        }

        btnRemindLater.setOnClickListener {
            listener?.onRemindMeLater()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    companion object {

        fun newInstance(): LinkAccountDialogFragment {
            return LinkAccountDialogFragment()
        }
    }
}