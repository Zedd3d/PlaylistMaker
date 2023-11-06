package com.zeddikus.playlistmaker.domain.models

data class Track(
    val trackName: String,                                  // Название композиции 1
    val artistName: String,                                 // Имя исполнителя
    val trackTimeMillis: Long,                              // Продолжительность трека (миллисекунды)
    val trackTime: String,                                  // Продолжительность трека (строка)
    val artworkUrl100: String,                              // Ссылка на изображение обложки
    val trackId: String,                                    // Идентификатор трека
    val primaryGenreName: String,                           // Название жанра
    val collectionName: String,                             // Название альбома
    val releaseDate: String?,                               // Дата выхода
    val country: String,                                    // страна
    val previewUrl: String                                  // Адрес предварительного прослушивания трека
)
