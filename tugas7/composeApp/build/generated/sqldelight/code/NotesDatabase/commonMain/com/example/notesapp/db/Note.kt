package com.example.notesapp.db

import kotlin.Long
import kotlin.String

public data class Note(
  public val id: Long,
  public val title: String,
  public val content: String,
  public val created_at: Long,
  public val updated_at: Long,
)
