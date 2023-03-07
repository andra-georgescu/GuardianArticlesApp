package com.guardian.androidtest.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.guardian.androidtest.MainDispatcherRule
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.repo.ArticlesRepository
import com.guardian.androidtest.ui.list.ArticlesViewModel
import com.guardian.androidtest.ui.list.UIState
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class ArticlesViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private val repository = mock<ArticlesRepository>()

    @Mock
    lateinit var navigationObserver: Observer<String>

    @Mock
    lateinit var stateObserver: Observer<UIState>

    private lateinit var stateCaptor: ArgumentCaptor<UIState>

    private lateinit var viewModel: ArticlesViewModel

    @Before
    fun setUp() {
        whenever(repository.articles).thenReturn(mock())
        viewModel = ArticlesViewModel(repository)
        stateCaptor = ArgumentCaptor.forClass(UIState::class.java)
        viewModel.navigateToDetails.observeForever(navigationObserver)
        viewModel.state.observeForever(stateObserver)
    }

    @After
    fun tearDown() {
        viewModel.navigateToDetails.removeObserver(navigationObserver)
        viewModel.state.removeObserver(stateObserver)
    }

    @Test
    fun navigationLiveDataIsUpdatedCorrectly() {
        val article = mock<Article>()
        whenever(article.id).thenReturn("id")

        viewModel.handleArticleSelected(article)
        verify(navigationObserver).onChanged("id")
    }

    @Test
    fun refreshingSuccessfullyUpdatesTheStateCorrectly() = runTest {
        viewModel.onRefresh()
        stateCaptor.run {
            verify(stateObserver, times(3)).onChanged(capture())
            Assert.assertEquals(UIState(), allValues[0])
            Assert.assertEquals(UIState(refreshing = true), allValues[1])
            Assert.assertEquals(UIState(), allValues[2])
        }
    }

    @Test
    fun refreshingUnsuccessfullyUpdatesTheStateCorrectly() = runTest {
        whenever(repository.refreshArticles()).doAnswer { throw IOException() }
        viewModel.onRefresh()
        stateCaptor.run {
            verify(stateObserver, times(3)).onChanged(capture())
            Assert.assertEquals(UIState(), allValues[0])
            Assert.assertEquals(UIState(refreshing = true), allValues[1])
            Assert.assertEquals(UIState(error = true), allValues[2])
        }
    }
}
