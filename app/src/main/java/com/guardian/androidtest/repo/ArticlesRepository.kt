package com.guardian.androidtest.repo

import androidx.lifecycle.Transformations
import com.guardian.androidtest.db.ArticlesDatabase
import com.guardian.androidtest.db.toDbArticle
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.networking.ArticleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ArticlesRepository(
    private val articleService: ArticleService,
    private val articlesDatabase: ArticlesDatabase
) {

    val articles = Transformations.map(articlesDatabase.articleDao.getArticles()) { dbArticles ->
        dbArticles.map { it.toArticle() }
    }

    suspend fun refreshArticles() = withContext(Dispatchers.IO) {
        val articles = articleService.getArticles("brexit")
        val dbArticles = articles.map { it.toDbArticle() }
        articlesDatabase.articleDao.insertAll(dbArticles)
    }

    suspend fun getArticle(articleId: String): Article = withContext(Dispatchers.IO) {
        try {
            val article = articleService.getArticleDetails(articleId)
                .apply { isFavourite = isFavourite(id) }
            articlesDatabase.articleDao.updateArticle(article.toDbArticle())
            article
        } catch (e: IOException) {
            articlesDatabase.articleDao.getArticle(articleId).toArticle()
        }
    }

    suspend fun toggleFavourite(article: Article): Article = withContext(Dispatchers.IO) {
        val isFavourite = isFavourite(article.id)
        articlesDatabase.articleDao.updateFavourite(!isFavourite, article.id)
        article.copy(isFavourite = !isFavourite)
    }

    private suspend fun isFavourite(id: String) =
        articlesDatabase.articleDao.getFavourites().map { it.id }.contains(id)
}
