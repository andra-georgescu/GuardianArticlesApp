package com.guardian.androidtest.ui.details

import androidx.lifecycle.viewModelScope
import com.guardian.androidtest.common.BaseViewModel
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.repo.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val repository: ArticlesRepository
) : BaseViewModel<ArticleDetailsState>(ArticleDetailsState()) {

    fun withArticleId(articleId: String) {
        viewModelScope.launch {
            setState { copy(loading = true, error = false) }
            try {
                val article = repository.getArticle(articleId)
                setState { copy(loading = false, article = article) }
            } catch (e: IOException) {
                setState { copy(loading = false, error = true) }
            }
        }
    }

    fun onFavouriteClicked() {
        val article = state.value?.article ?: return
        viewModelScope.launch {
            try {
                val updatedArticle = repository.toggleFavourite(article)
                setState { copy(article = updatedArticle, error = false) }
            } catch (e: IOException) {
                setState { copy(error = true) }
            }
        }
    }
}

data class ArticleDetailsState(
    val article: Article? = null,
    val loading: Boolean = false,
    val error: Boolean = false
)

