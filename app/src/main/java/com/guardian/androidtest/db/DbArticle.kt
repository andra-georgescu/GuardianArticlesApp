package com.guardian.androidtest.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guardian.androidtest.domain.Article
import java.io.Serializable
import java.util.*

@Entity(tableName = "articles")
data class DbArticle(
    @PrimaryKey
    val id: String,
    val thumbnail: String,
    val sectionId: String,
    val sectionName: String,
    val published: Date,
    val title: String,
    val body: String,
    val url: String,
    var isFavourite: Boolean? = null
) : Serializable {
    fun toArticle() = Article(
        id,
        thumbnail,
        sectionId,
        sectionName,
        published,
        title,
        body,
        url,
        isFavourite
    )
}

fun Article.toDbArticle() = DbArticle(
    id,
    thumbnail,
    sectionId,
    sectionName,
    published,
    title,
    body,
    url,
    isFavourite
)