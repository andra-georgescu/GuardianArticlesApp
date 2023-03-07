package com.guardian.androidtest.networking.model

import com.guardian.androidtest.domain.Article
import java.util.*

data class ApiArticle(
    val id: String,
    val sectionId: String,
    val sectionName: String,
    val webPublicationDate: Date,
    val webTitle: String,
    val webUrl: String,
    val apiUrl: String,
    val fields: ApiArticleFields?
) {
    data class ApiArticleFields(
        val headline: String?,
        val body: String?,
        val thumbnail: String?
    )

    fun toArticle() = Article(
        id,
        fields?.thumbnail ?: "",
        sectionId,
        sectionName,
        webPublicationDate,
        fields?.headline ?: "",
        fields?.body ?: "",
        apiUrl
    )
}
