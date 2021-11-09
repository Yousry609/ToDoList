package com.turki.todolist.model.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.turki.todolist.model.NoteDAO


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDAO(): NoteDAO

    companion object {


        @Volatile
        var instance: NoteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): NoteDatabase {
            return instance?: synchronized(Any()){
                instance?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
            }

        }

    }
}

