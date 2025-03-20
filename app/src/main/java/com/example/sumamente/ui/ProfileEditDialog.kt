package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.example.sumamente.R

class ProfileEditDialog(context: Context) : Dialog(context) {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_profile_edit)

        window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val btnEditUsername = findViewById<Button>(R.id.btn_edit_username)
        val btnEditFlag = findViewById<Button>(R.id.btn_edit_flag)

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                dismiss()
            }
        }

        btnEditUsername.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(context, UsernameSelectionActivity::class.java)
                context.startActivity(intent)
                dismiss()
            }
        }

        btnEditFlag.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(context, FlagSelectionActivity::class.java)
                context.startActivity(intent)
                dismiss()
            }
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

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}
