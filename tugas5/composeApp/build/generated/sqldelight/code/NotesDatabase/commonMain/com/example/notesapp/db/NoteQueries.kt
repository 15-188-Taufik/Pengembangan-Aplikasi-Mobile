package com.example.notesapp.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class NoteQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    title: String,
    content: String,
    created_at: Long,
    updated_at: Long,
  ) -> T): Query<T> = Query(-1_698_323_834, arrayOf("Note"), driver, "Note.sq", "selectAll",
      "SELECT * FROM Note ORDER BY updated_at DESC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!
    )
  }

  public fun selectAll(): Query<Note> = selectAll { id, title, content, created_at, updated_at ->
    Note(
      id,
      title,
      content,
      created_at,
      updated_at
    )
  }

  public fun <T : Any> selectById(id: Long, mapper: (
    id: Long,
    title: String,
    content: String,
    created_at: Long,
    updated_at: Long,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!
    )
  }

  public fun selectById(id: Long): Query<Note> = selectById(id) { id_, title, content, created_at,
      updated_at ->
    Note(
      id_,
      title,
      content,
      created_at,
      updated_at
    )
  }

  public fun <T : Any> search(query: String, mapper: (
    id: Long,
    title: String,
    content: String,
    created_at: Long,
    updated_at: Long,
  ) -> T): Query<T> = SearchQuery(query) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!
    )
  }

  public fun search(query: String): Query<Note> = search(query) { id, title, content, created_at,
      updated_at ->
    Note(
      id,
      title,
      content,
      created_at,
      updated_at
    )
  }

  public fun count(): Query<Long> = Query(1_509_884_176, arrayOf("Note"), driver, "Note.sq",
      "count", "SELECT COUNT(*) FROM Note") { cursor ->
    cursor.getLong(0)!!
  }

  public fun insert(
    title: String,
    content: String,
    created_at: Long,
    updated_at: Long,
  ) {
    driver.execute(-267_447_592, """
        |INSERT INTO Note(title, content, created_at, updated_at)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindString(0, title)
          bindString(1, content)
          bindLong(2, created_at)
          bindLong(3, updated_at)
        }
    notifyQueries(-267_447_592) { emit ->
      emit("Note")
    }
  }

  public fun update(
    title: String,
    content: String,
    updated_at: Long,
    id: Long,
  ) {
    driver.execute(77_498_600, """
        |UPDATE Note
        |SET title = ?, content = ?, updated_at = ?
        |WHERE id = ?
        """.trimMargin(), 4) {
          bindString(0, title)
          bindString(1, content)
          bindLong(2, updated_at)
          bindLong(3, id)
        }
    notifyQueries(77_498_600) { emit ->
      emit("Note")
    }
  }

  public fun delete(id: Long) {
    driver.execute(-419_113_526, """DELETE FROM Note WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-419_113_526) { emit ->
      emit("Note")
    }
  }

  public fun deleteAll() {
    driver.execute(-341_057_673, """DELETE FROM Note""", 0)
    notifyQueries(-341_057_673) { emit ->
      emit("Note")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Note", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Note", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_108_390_003, """SELECT * FROM Note WHERE id = ?""", mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "Note.sq:selectById"
  }

  private inner class SearchQuery<out T : Any>(
    public val query: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Note", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Note", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(10_008_007, """
    |SELECT * FROM Note
    |WHERE title  LIKE '%' || ? || '%'
    |   OR content LIKE '%' || ? || '%'
    |ORDER BY updated_at DESC
    """.trimMargin(), mapper, 2) {
      bindString(0, query)
      bindString(1, query)
    }

    override fun toString(): String = "Note.sq:search"
  }
}
