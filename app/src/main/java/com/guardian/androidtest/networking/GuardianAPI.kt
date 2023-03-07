package com.guardian.androidtest.networking

import com.guardian.androidtest.networking.model.ApiArticle
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GuardianAPI {
    @GET("search?show-fields=headline,thumbnail&page-size=50")
    suspend fun searchArticles(@Query("q") searchTerm: String): ApiArticleListResponse

    @GET
    suspend fun getArticle(
        @Url articleUrl: String,
        @Query("show-fields") fields: String
    ): ApiArticleDetailsResponse
}

data class ApiArticleDetailsResponse(val response: ApiArticleDetails)

data class ApiArticleDetails(val content: ApiArticle)

data class ApiArticleListResponse(val response: ApiArticleList)

data class ApiArticleList(val results: List<ApiArticle>)
