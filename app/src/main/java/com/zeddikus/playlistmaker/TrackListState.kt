package com.zeddikus.playlistmaker

import android.view.View

enum class TrackListState {
    GONE,
    ERROR_NETWORK,
    ERROR_EMPTY,
    OK,
    SEARCH_IN_PROGRESS;

    fun getProgressBarVisible(): Int{
        return when (this) {
            SEARCH_IN_PROGRESS -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun getTrackListVisible(): Int{
        return when (this) {
            OK -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun getPlaceholderVisible(): Int{
        return when (this) {
            ERROR_NETWORK -> View.VISIBLE
            ERROR_EMPTY -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun getUpdateButtonVisible(): Int{
        return when (this) {
            ERROR_NETWORK -> View.VISIBLE
            else -> View.GONE
        }
    }

}