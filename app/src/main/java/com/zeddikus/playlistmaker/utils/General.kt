package com.zeddikus.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object General {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun convertURLtoBigSizeCover(artworkUrl100: String): String {
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }
}