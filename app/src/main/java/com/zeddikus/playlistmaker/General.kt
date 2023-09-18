package com.zeddikus.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue

object General {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}