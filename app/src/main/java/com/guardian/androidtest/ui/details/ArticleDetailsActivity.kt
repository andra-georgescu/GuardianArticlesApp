package com.guardian.androidtest.ui.details

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.guardian.androidtest.R
import com.guardian.androidtest.databinding.ActivityArticleDetailsBinding
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.ui.list.EXTRA_ARTICLE_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleDetailsActivity : AppCompatActivity() {

    private val viewModel: ArticleDetailsViewModel by viewModels()
    private lateinit var view: ActivityArticleDetailsBinding
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = DataBindingUtil.setContentView(this, R.layout.activity_article_details)

        setSupportActionBar(view.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        intent.getStringExtra(EXTRA_ARTICLE_ID)?.let { viewModel.withArticleId(it) }

        viewModel.state.observe(this) { state ->
            state.article?.let { updateViewWithArticle(it) }
            view.loadingIndicator.visibility = if (state.loading) View.VISIBLE else View.GONE
            if (state.error) showErrorToast()
        }
    }

    private fun updateViewWithArticle(article: Article) {
        view.body.text = Html.fromHtml(article.body)
        view.title.text = article.title
        Glide.with(baseContext).load(article.thumbnail).into(view.image)
        val favouriteIcon =
            if (article.isFavourite == true) R.drawable.ic_favourite_filled
            else R.drawable.ic_favourite_border
        menu?.findItem(R.id.action_favorite)?.setIcon(favouriteIcon)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> {
            viewModel.onFavouriteClicked()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showErrorToast() = Toast.makeText(baseContext, R.string.error, LENGTH_LONG).show()
}