package com.guardian.androidtest.networking

import com.guardian.androidtest.domain.Article

interface ArticleService {

    suspend fun getArticles(searchTerm: String): List<Article>

    suspend fun getArticleDetails(articleId: String): Article
}