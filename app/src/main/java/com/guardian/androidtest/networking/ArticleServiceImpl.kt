package com.guardian.androidtest.networking

import com.guardian.androidtest.domain.Article

class ArticleServiceImpl(private val guardianAPI: GuardianAPI) : ArticleService {

    override suspend fun getArticles(searchTerm: String): List<Article> {
        val apiResponse = guardianAPI.searchArticles(searchTerm)
        return apiResponse.response.results.map { it.toArticle() }
    }

    override suspend fun getArticleDetails(articleId: String): Article {
        val apiResponse = guardianAPI.getArticle(articleId, "main,body,headline,thumbnail")
        return apiResponse.response.content.toArticle()
    }
}