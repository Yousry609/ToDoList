package com.turki.todolist.model

import androidx.room.*
import com.turki.todolist.model.entity.Note

@Dao
interface NoteDAO {

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun addNote(note:Note)

    @Update
    suspend fun updateNote(note:Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("select * from note_table order by noteId desc, noteCategory desc")
    suspend fun getNotes():List<Note>

}