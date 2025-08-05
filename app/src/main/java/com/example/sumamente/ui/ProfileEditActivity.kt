package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import com.example.sumamente.R
import com.example.sumamente.ui.utils.DataSyncManager
import android.app.Dialog
import android.os.Handler



class ProfileEditActivity : BaseActivity(), LinkUnlinkAccountDialogFragment.Listener, ProgressConflictDialogFragment.Listener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnLinkUnlink: AppCompatButton
    private var isLinking: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val btnEditUsername = findViewById<AppCompatButton>(R.id.btn_edit_username)
        val btnEditFlag = findViewById<AppCompatButton>(R.id.btn_edit_flag)
        btnLinkUnlink = findViewById(R.id.btn_link_unlink)
        val title = findViewById<View>(R.id.title_profile_edit)

        updateLinkButtonState()

        startEntranceAnimation(title, btnEditUsername, btnEditFlag, btnLinkUnlink, closeButton)

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }

        btnEditUsername.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, UsernameSelectionActivity::class.java)
                startActivity(intent)
            }
        }

        btnEditFlag.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, FlagSelectionActivity::class.java)
                startActivity(intent)
            }
        }

        btnLinkUnlink.setOnClickListener {
            applyBounceEffect(it) {
                val isLinked = sharedPreferences.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)
                isLinking = !isLinked
                showLinkUnlinkDialog(isLinking)
            }
        }
    }

    private fun updateLinkButtonState() {
        val isLinked = sharedPreferences.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)
        btnLinkUnlink.text = if (isLinked) {
            getString(R.string.action_unlink_account)
        } else {
            getString(R.string.dialog_link_accept)
        }
    }

    private fun showLinkUnlinkDialog(isLinking: Boolean) {
        val title: String
        val message: String
        val actionText: String

        if (isLinking) {
            title = getString(R.string.dialog_link_title)
            message = getString(R.string.link_account_confirm_message)
            actionText = getString(R.string.dialog_link_accept)
        } else {
            title = getString(R.string.dialog_unlink_title)
            message = getString(R.string.unlink_account_confirm_message)
            actionText = getString(R.string.action_unlink_account)
        }

        val dialog = LinkUnlinkAccountDialogFragment.newInstance(title, message, actionText)
        dialog.show(supportFragmentManager, "LinkUnlinkAccountDialog")
    }

    private fun startEntranceAnimation(vararg views: View) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((100 * index).toLong())
                .start()
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }
        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
        }
        animatorSet.start()
    }

    override fun onDialogAction() {
        if (isLinking) {
            FirebaseAuthManager.startGoogleSignIn(
                this,
                getString(R.string.default_web_client_id)
            ) { success, message ->
                if (success) {
                    sharedPreferences.edit { putBoolean(SettingsActivity.ACCOUNT_LINKED, true) }
                    updateLinkButtonState()

                    DataSyncManager.getLocalAndCloudProgress(this) { localProgress, cloudProgress ->
                        if (localProgress != null && cloudProgress != null && localProgress != cloudProgress) {

                            ProgressConflictDialogFragment.newInstance()
                                .show(supportFragmentManager, "ProgressConflictDialog")
                        } else {

                            DataSyncManager.syncDataToCloud(this) { ok, err ->
                                showResultDialog(restoring = false)
                                updateLinkButtonState()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, message ?: getString(R.string.account_linked_error), Toast.LENGTH_SHORT).show()
                    updateLinkButtonState()
                }
            }
        }

    }

    private fun showResultDialog(restoring: Boolean) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_restore_success)
        val tvResult = dialog.findViewById<TextView>(R.id.tvRestoreSuccess)
        tvResult.text = if (restoring)
            getString(R.string.restore_success_title)
        else
            getString(R.string.update_success_title)
        dialog.show()

        Handler(mainLooper).postDelayed({
            dialog.dismiss()
        }, 2000)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FirebaseAuthManager.handleSignInResult(this, requestCode, data)
    }

    override fun onRestoreCloud() {
        DataSyncManager.syncDataFromCloud(this) { ok, _ ->
            showResultDialog(restoring = true)
        }
    }

    override fun onKeepLocal() {
        DataSyncManager.syncDataToCloud(this) { ok, _ ->
            showResultDialog(restoring = false)
        }
    }

}
