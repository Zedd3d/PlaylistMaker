package com.zeddikus.playlistmaker.data.mediatec.playlists.dto

data class PlaylistDto(
    val playlistId: Int,                                     // Идентификатор Плейлиста
    val playlistName: String?,                               // имя плейлиста
    val playlistDescription: String?,                        // описание плейлиста
    val playlistCover: String?,                              // имя файла в системе
    val tracksCount: Int,                                     // количество треков в плейлисте
    val addTime: Long = 0                                    // Время добавления
)