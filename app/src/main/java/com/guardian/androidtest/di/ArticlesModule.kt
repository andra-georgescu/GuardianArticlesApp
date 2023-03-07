package com.guardian.androidtest.di

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import com.guardian.androidtest.db.ArticlesDatabase
import com.guardian.androidtest.networking.ArticleService
import com.guardian.androidtest.networking.ArticleServiceImpl
import com.guardian.androidtest.networking.GuardianAPI
import com.guardian.androidtest.repo.ArticlesRepository
import com.guardian.androidtest.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ArticlesModule {

    companion object {
        private const val BASE_URL = "https://content.guardianapis.com"
        private const val HEADER_API_KEY = "api-key"
    }

    @Singleton
    @Provides
    fun provideArticleRepository(
        articleService: ArticleService,
        articlesDatabase: ArticlesDatabase
    ): ArticlesRepository =
        ArticlesRepository(articleService, articlesDatabase)

    @Singleton
    @Provides
    fun provideArticleService(guardianApi: GuardianAPI): ArticleService =
        ArticleServiceImpl(guardianApi)

    @Singleton
    @Provides
    fun provideArticlesDatabase(@ApplicationContext context: Context): ArticlesDatabase =
        Room.databaseBuilder(
            context,
            ArticlesDatabase::class.java,
            "articles"
        ).build()

    @Singleton
    @Provides
    fun provideGuardianApi(moshi: Moshi, okHttpClient: OkHttpClient): GuardianAPI =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(GuardianAPI::class.java)

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    @Provides
    fun provideOkHttpClient(
        @AuthInterceptor authInterceptor: Interceptor,
        @LoggingInterceptor loggingInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(authInterceptor)
        addInterceptor(loggingInterceptor)
    }.build()

    @LoggingInterceptor
    @Provides
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @AuthInterceptor
    @Provides
    fun provideAuthInterceptor(resources: Resources): Interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val hb = original.headers.newBuilder().apply {
                add(HEADER_API_KEY, resources.getString(R.string.guardian_api_key))
            }
            return chain.proceed(original.newBuilder().headers(hb.build()).build())
        }
    }

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor
