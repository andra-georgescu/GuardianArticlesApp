package com.guardian.androidtest.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.guardian.androidtest.common.BaseViewModel
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.domain.toCategories
import com.guardian.androidtest.repo.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: ArticlesRepository
) : BaseViewModel<UIState>(UIState()) {

    private val _navigateToDetails: MutableLiveData<String> = MutableLiveData()
    val navigateToDetails: LiveData<String> get() = _navigateToDetails

    val articleCategories = Transformations.map(repository.articles) { it.toCategories() }

    init {
        fetchArticles()
    }

    fun onRefresh() {
        fetchArticles()
    }

    private fun fetchArticles() {
        setState { copy(refreshing = true, error = false) }
        viewModelScope.launch {
            try {
                repository.refreshArticles()
                setState { copy(refreshing = false, error = false) }
            } catch (e: IOException) {
                setState { copy(refreshing = false, error = true) }
            }
        }
    }

    fun handleArticleSelected(article: Article) {
        _navigateToDetails.value = article.id
    }
}

data class UIState(
    val refreshing: Boolean = false,
    val error: Boolean = false
)
