package com.guardian.androidtest.ui

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.guardian.androidtest.db.ArticleDao
import com.guardian.androidtest.db.ArticlesDatabase
import com.guardian.androidtest.db.DbArticle
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class ArticlesDatabaseTest {

    private lateinit var articleDao: ArticleDao
    private lateinit var db: ArticlesDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ArticlesDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        articleDao = db.articleDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetArticle() {
        runBlocking {
            val id = "id1"
            val article = DbArticle(id, "", "", "", Date(), "", "", "")
            articleDao.insertAll(listOf(article))
            val storedArticle = articleDao.getArticle(id)
            assertEquals(article, storedArticle)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateArticle() {
        runBlocking {
            val id = "id1"
            val title = "title1"
            val article = DbArticle(id, "", "", "", Date(), "", "", "")
            articleDao.insertAll(listOf(article))
            articleDao.updateArticle(article.copy(title = title))
            val updatedArticle = articleDao.getArticle(id)
            assertEquals(title, updatedArticle.title)
        }
    }

    @Test
    @Throws(Exception::class)
    fun toggleFavourite() {
        runBlocking {
            val id = "id1"
            val article = DbArticle(id, "", "", "", Date(), "", "", "")
            articleDao.insertAll(listOf(article))
            articleDao.updateFavourite(true, id)
            val updatedArticle = articleDao.getArticle(id)
            assertEquals(true, updatedArticle.isFavourite)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getFavourites() {
        runBlocking {
            val id1 = "id1"
            val id2 = "id2"
            val article1 = DbArticle(id1, "", "", "", Date(), "", "", "", true)
            val article2 = DbArticle(id2, "", "", "", Date(), "", "", "")
            articleDao.insertAll(listOf(article1, article2))
            val favourites = articleDao.getFavourites()
            assertEquals(1, favourites.size)
            assertEquals(id1, favourites[0].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun ignoreConflicts() {
        runBlocking {
            val id = "id"
            val title1 = "title1"
            val title2 = "title2"
            val article1 = DbArticle(id, "", "", "", Date(), title1, "", "")
            val article2 = DbArticle(id, "", "", "", Date(), title2, "", "")
            articleDao.insertAll(listOf(article1, article2))
            val insertedArticle = articleDao.getArticle(id)
            assertEquals(title1, insertedArticle.title)
        }
    }
}