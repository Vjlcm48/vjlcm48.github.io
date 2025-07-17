package com.example.sumamente.ui

import android.text.SpannableString
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class GameProgressItem(
    @param:ColorRes val borderColorRes: Int,
    @param:DrawableRes val totalRowBackgroundRes: Int,
    @param:StringRes val gameNameRes: Int? = null,
    val gameNameSpannable: SpannableString? = null,
    @param:ColorRes val gameNameTextColorRes: Int,
    val getPrincipianteData: () -> Int,
    val getAvanzadoData: () -> Int,
    val getProData: () -> Int
)