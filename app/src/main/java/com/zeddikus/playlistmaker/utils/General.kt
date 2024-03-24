package com.zeddikus.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object General {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun convertURLtoBigSizeCover(artworkUrl100: String): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    fun declinationTracksCount(tracksCount: Int): String {
        var partOfPrice = tracksCount % 100
        if (partOfPrice.compareTo(14) > 0) {
            partOfPrice = partOfPrice % 10
        }
        return when (partOfPrice) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }

    }

    fun declinationMinutes(tracksCount: Int): String {
        var partOfPrice = tracksCount % 100
        if (partOfPrice.compareTo(14) > 0) {
            partOfPrice = partOfPrice % 10
        }
        return when (partOfPrice) {
            1 -> "минута"
            2, 3, 4 -> "минуты"
            else -> "минут"
        }

    }

}