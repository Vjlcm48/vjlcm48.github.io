package com.heptacreation.sumamente.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.MessagesStateManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil

class MessagesActivity : BaseActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_messages)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })

        btnBack = findViewById(R.id.btn_back)
        tvTitle = findViewById(R.id.tv_title)
        tvEmpty = findViewById(R.id.tv_empty)
        recycler = findViewById(R.id.recycler_messages)

        btnBack.setOnClickListener { finish() }

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MessagesAdapter { item ->

            MessagesStateManager.markAsRead(this, item.id)
            refreshList()

            InternalMessageDialog.show(
                activity = this,
                titleRes = item.titleRes,
                bodyRes = item.bodyRes,
                onNotNow = {
                    MessagesStateManager.markIgnoredNow(this, item.id)
                    refreshList()
                },

                onGo = {
                    if (item.id == MessagesStateManager.MSG_AMBASSADOR) {
                        startActivity(Intent(this, EmbajadorActivity::class.java))
                        finish()
                    } else if (item.id.startsWith(MessagesStateManager.MSG_REFERRAL_VALIDATED)) {
                        startActivity(Intent(this, EmbajadorActivity::class.java))
                        finish()
                    } else {
                        InternalMessageDialog.showUnderConstruction(this)
                    }
                }
            )
        }
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        MessagesStateManager.ensureActivationByThresholds(this)
        refreshList()
    }

    private fun refreshList() {
        adapter.submitList(MessagesStateManager.getActiveMessages(this))
        val hasItems = adapter.itemCount > 0
        tvEmpty.visibility = if (hasItems) View.GONE else View.VISIBLE
        recycler.visibility = if (hasItems) View.VISIBLE else View.GONE
    }


    private class MessagesAdapter(
        private val onItemClicked: (MessagesStateManager.MessageItem) -> Unit
    ) : ListAdapter<MessagesStateManager.MessageItem, MessagesViewHolder>(DIFF) {

        companion object {
            private val DIFF = object : DiffUtil.ItemCallback<MessagesStateManager.MessageItem>() {
                override fun areItemsTheSame(
                    oldItem: MessagesStateManager.MessageItem,
                    newItem: MessagesStateManager.MessageItem
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: MessagesStateManager.MessageItem,
                    newItem: MessagesStateManager.MessageItem
                ): Boolean =
                    oldItem.titleRes == newItem.titleRes &&
                            oldItem.bodyRes == newItem.bodyRes &&
                            oldItem.unread == newItem.unread
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MessagesViewHolder {
            val inflater = android.view.LayoutInflater.from(parent.context)
            val v = inflater.inflate(R.layout.item_message_title, parent, false)
            return MessagesViewHolder(v)
        }

        override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(item, onItemClicked)
        }
    }


    private class MessagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tv_message_title)
        private val redDot: View = view.findViewById(R.id.red_dot_small)
        private val container: View = view.findViewById(R.id.item_container)

        fun bind(item: MessagesStateManager.MessageItem, onItemClicked: (MessagesStateManager.MessageItem) -> Unit) {
            title.setText(item.titleRes)
            redDot.visibility = if (item.unread) View.VISIBLE else View.GONE
            container.setOnClickListener { onItemClicked(item) }
        }
    }
}
