package com.example.sumamente.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.sumamente.R

class LinkUnlinkAccountDialogFragment : DialogFragment() {

    interface Listener {
        fun onDialogAction()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Listener ?: context as? Listener
        if (listener == null) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_link_unlink_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = view.findViewById<TextView>(R.id.tvDialogMessage)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnAction = view.findViewById<Button>(R.id.btnAction)


        tvDialogTitle.text = arguments?.getString("title") ?: ""
        tvDialogMessage.text = arguments?.getString("message") ?: ""
        btnAction.text = arguments?.getString("actionText") ?: ""

        btnCancel.setOnClickListener { dismiss() }
        btnAction.setOnClickListener {
            listener?.onDialogAction()
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
        fun newInstance(title: String, message: String, actionText: String): LinkUnlinkAccountDialogFragment {
            val frag = LinkUnlinkAccountDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("message", message)
            args.putString("actionText", actionText)
            frag.arguments = args
            return frag
        }
    }
}
