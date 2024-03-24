package com.zeddikus.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,                                     // Идентификатор Плейлиста
    val playlistName: String?,                               // имя плейлиста
    val playlistDescription: String?,                        // описание плейлиста
    val playlistCover: String?,                              // имя файла в системе
    val tracksCount: Int,                                     // количество треков в плейлисте
    val playlistTimeMillis: Long,                             // количество времени в плейлисте
    val addTime: Long = 0                                    // Время добавления
)

@Entity(tableName = "playlist_list_table")
data class TrackInPlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val tableStringId: Int,                  // Идентификатор строке в таблице треков плейлиста
    val playlistID: Int,                        // сслыка на плейлист
    val trackId: String,                        // ссылка на трек
    val trackName: String?,                                  // Название композиции
    val artistName: String?,                                 // Имя исполнителя
    val trackTimeMillis: Long?,                              // Продолжительность трека
    val artworkUrl100: String?,                              // Ссылка на изображение обложки
    val primaryGenreName: String?,                           // Название жанра
    val collectionName: String?,                             // Название альбома
    val releaseDate: String?,                                // Дата выхода
    val country: String?,                                    // страна
    val previewUrl: String?,                                 // URL предпросмотра
    val addTime: Long = 0
)
