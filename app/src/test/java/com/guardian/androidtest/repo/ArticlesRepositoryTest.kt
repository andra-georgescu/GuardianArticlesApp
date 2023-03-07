package com.guardian.androidtest.repo

import com.guardian.androidtest.db.ArticleDao
import com.guardian.androidtest.db.ArticlesDatabase
import com.guardian.androidtest.db.toDbArticle
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.networking.ArticleService
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import java.io.IOException
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ArticlesRepositoryTest {

    @Mock
    lateinit var mockService: ArticleService

    @Mock
    lateinit var mockDatabase: ArticlesDatabase

    @Mock
    lateinit var mockDao: ArticleDao

    lateinit var repository: ArticlesRepository

    @Before
    fun setUp() {
        whenever(mockDatabase.articleDao).thenReturn(mockDao)
        whenever(mockDao.getArticles()).thenReturn(mock())
        repository = ArticlesRepository(mockService, mockDatabase)
    }

    @Test
    fun repoFetchesAndStoresArticles() {
        val article = Article("id", "", "", "", Date(), "", "", "", false)
        val articles = listOf(article)
        val dbArticles = listOf(article.toDbArticle())

        mockService.stub { onBlocking { getArticles(any()) }.doReturn(articles) }

        runBlocking {
            repository.refreshArticles()
            verify(mockDao).insertAll(eq(dbArticles))
        }
    }

    @Test
    fun gettingAnArticleReturnsApiDataWhenNetworkCallSuccessful() {
        val article = Article("id", "", "", "", Date(), "", "", "", false)

        mockService.stub { onBlocking { getArticleDetails(any()) }.doReturn(article) }
        mockDao.stub { onBlocking { getFavourites() }.doReturn(listOf()) }

        runBlocking {
            val res = repository.getArticle(article.id)
            assertEquals(article, res)
        }
    }

    @Test
    fun gettingAnArticleReturnsRoomDataWhenNetworkCallFailed() {
        val article = Article("id", "", "", "", Date(), "", "", "", false)

        mockService.stub {
            onBlocking { getArticleDetails(any()) }.doAnswer { throw IOException() }
        }
        mockDao.stub { onBlocking { getArticle(article.id) }.doReturn(article.toDbArticle()) }

        runBlocking {
            val res = repository.getArticle(article.id)
            assertEquals(article, res)
        }
    }

    @Test
    fun gettingAFavouriteArticleMarksItAsFavourite() {
        val article = Article("id", "", "", "", Date(), "", "", "")

        mockService.stub { onBlocking { getArticleDetails(any()) }.doReturn(article) }
        mockDao.stub { onBlocking { getFavourites() }.doReturn(listOf(article.toDbArticle())) }

        runBlocking {
            val details = repository.getArticle(article.id)
            assertEquals(true, details.isFavourite)
        }
    }

    @Test
    fun togglingAFavouriteArticleMarksItNotFavourite() {
        val article = Article("id", "", "", "", Date(), "", "", "")

        mockDao.stub { onBlocking { getFavourites() }.doReturn(listOf(article.toDbArticle())) }

        runBlocking {
            val fav = repository.toggleFavourite(article)
            assertEquals(false, fav.isFavourite)
            verify(mockDao).updateFavourite(false, article.id)
        }
    }

    @Test
    fun togglingANotFavouriteArticleMarksItFavourite() {
        val article = Article("id", "", "", "", Date(), "", "", "")

        mockDao.stub { onBlocking { getFavourites() }.doReturn(listOf()) }

        runBlocking {
            val fav = repository.toggleFavourite(article)
            assertEquals(true, fav.isFavourite)
            verify(mockDao).updateFavourite(true, article.id)
        }
    }
}