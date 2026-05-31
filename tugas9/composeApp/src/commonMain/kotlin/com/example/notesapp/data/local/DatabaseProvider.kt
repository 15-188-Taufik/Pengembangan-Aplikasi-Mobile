package com.example.notesapp.data.local

import com.example.notesapp.db.NotesDatabase

object DatabaseProvider {
    private var database: NotesDatabase? = null

    fun getDatabase(databaseDriverFactory: DatabaseDriverFactory): NotesDatabase {
        return database ?: NotesDatabase(databaseDriverFactory.createDriver()).also {
            database = it
        }
    }
}
