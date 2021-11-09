package com.turki.todolist.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Int,
    var noteTitle: String,
    var noteCategory: String,
    var noteDescription: String,
    var noteCreationDate: String,
    var noteDueDate: String,
    var noteIsDone: Boolean
) : Parcelable