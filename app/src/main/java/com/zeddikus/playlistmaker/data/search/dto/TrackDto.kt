package com.zeddikus.playlistmaker.data.search.dto

data class TrackDto(val trackName: String?,                          // Название композиции 1
            val artistName: String?,                                 // Имя исполнителя
            val trackTimeMillis: Long?,                              // Продолжительность трека
            val artworkUrl100: String?,                              // Ссылка на изображение обложки
            val trackId: String?,                                    // Идентификатор трека
            val primaryGenreName: String?,                           // Название жанра
            val collectionName: String?,                             // Название альбома
            val releaseDate: String?,                                // Дата выхода
            val country: String?,                                    // страна
            val previewUrl: String?                                  // Адрес предварительного прослушивания трека
)