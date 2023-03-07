package com.guardian.androidtest.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArticleDao {
    @Query("select * from articles")
    fun getArticles(): LiveData<List<DbArticle>>

    @Query("select * from articles WHERE isFavourite=1")
    suspend fun getFavourites(): List<DbArticle>

    @Query("select * from articles WHERE id=:id")
    suspend fun getArticle(id: String): DbArticle

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(articles: List<DbArticle>)

    @Update
    suspend fun updateArticle(article: DbArticle)

    @Query("UPDATE articles SET isFavourite=:isFavourite WHERE id=:id")
    suspend fun updateFavourite(isFavourite: Boolean, id: String)
}