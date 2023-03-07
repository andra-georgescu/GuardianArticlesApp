package com.guardian.androidtest.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.guardian.androidtest.MainDispatcherRule
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.repo.ArticlesRepository
import com.guardian.androidtest.ui.details.ArticleDetailsState
import com.guardian.androidtest.ui.details.ArticleDetailsViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class ArticleDetailsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private val repository = mock<ArticlesRepository>()

    @Mock
    lateinit var stateObserver: Observer<ArticleDetailsState>

    private lateinit var stateCaptor: ArgumentCaptor<ArticleDetailsState>

    private lateinit var viewModel: ArticleDetailsViewModel

    @Before
    fun setUp() {
        viewModel = ArticleDetailsViewModel(repository)
        stateCaptor = ArgumentCaptor.forClass(ArticleDetailsState::class.java)
        viewModel.state.observeForever(stateObserver)
    }

    @After
    fun tearDown() {
        viewModel.state.removeObserver(stateObserver)
    }

    @Test
    fun loadingArticleSuccessfully() = runTest {
        val article: Article = mock()
        whenever(repository.getArticle("id")).thenReturn(article)

        viewModel.withArticleId("id")

        stateCaptor.run {
            verify(stateObserver, times(3)).onChanged(capture())
            assertEquals(ArticleDetailsState(), allValues[0])
            assertEquals(ArticleDetailsState(loading = true), allValues[1])
            assertEquals(ArticleDetailsState(article), allValues[2])
        }
    }

    @Test
    fun loadingArticleUnsuccessfully() = runTest {
        whenever(repository.getArticle("id")).doAnswer { throw IOException() }

        viewModel.withArticleId("id")

        stateCaptor.run {
            verify(stateObserver, times(3)).onChanged(capture())
            assertEquals(ArticleDetailsState(), allValues[0])
            assertEquals(ArticleDetailsState(loading = true), allValues[1])
            assertEquals(ArticleDetailsState(error = true), allValues[2])
        }
    }

    @Test
    fun togglingFavouriteSuccessfully() = runTest {
        val article1: Article = mock()
        val article2: Article = mock()
        whenever(repository.getArticle("id")).thenReturn(article1)
        whenever(repository.toggleFavourite(any())).thenReturn(article2)

        viewModel.withArticleId("id")
        viewModel.onFavouriteClicked()

        stateCaptor.run {
            verify(stateObserver, times(4)).onChanged(capture())
            assertEquals(ArticleDetailsState(article2), allValues[3])
        }
    }

    @Test
    fun togglingFavouriteUnsuccessfully() = runTest {
        val article: Article = mock()
        whenever(repository.getArticle("id")).thenReturn(article)
        whenever(repository.toggleFavourite(any())).doAnswer { throw IOException() }

        viewModel.withArticleId("id")
        viewModel.onFavouriteClicked()

        stateCaptor.run {
            verify(stateObserver, times(4)).onChanged(capture())
            assertEquals(ArticleDetailsState(article = article, error = true), allValues[3])
        }
    }
}