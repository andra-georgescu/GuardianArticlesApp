package com.guardian.androidtest.ui.list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.guardian.androidtest.R
import com.guardian.androidtest.databinding.ActivityArticleListBinding
import com.guardian.androidtest.ui.details.ArticleDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

const val EXTRA_ARTICLE_ID = "com.guardian.ARTICLE_ID"

@AndroidEntryPoint
class ArticlesActivity : AppCompatActivity() {

    private val viewModel: ArticlesViewModel by viewModels()
    lateinit var view: ActivityArticleListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = DataBindingUtil.setContentView(this, R.layout.activity_article_list)

        setSupportActionBar(view.toolbar)

        view.swipeRefresh.setOnRefreshListener { viewModel.onRefresh() }

        val adapter = CategoryAdapter { viewModel.handleArticleSelected(it) }
        view.categories.layoutManager = LinearLayoutManager(this)
        view.categories.adapter = adapter

        viewModel.state.observe(this) { state ->
            view.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) showErrorToast()
        }

        viewModel.articleCategories.observe(this) { categories ->
            adapter.submitList(categories)
        }

        viewModel.navigateToDetails.observe(this) { articleId ->
            val intent = Intent(this, ArticleDetailsActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE_ID, articleId)
            }
            startActivity(intent)
        }
    }

    private fun showErrorToast() = Toast.makeText(baseContext, R.string.error, LENGTH_LONG).show()
}
