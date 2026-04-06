package com.heptacreation.sumamente.ui

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
    val getProData: () -> Int,
    val totalLevelsPrincipiante: Int = 70,
    val totalLevelsAvanzado: Int = 70,
    val totalLevelsPro: Int = 70
)