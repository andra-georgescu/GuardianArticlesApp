package com.guardian.androidtest.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.databinding.ListItemArticleBinding

class ArticleAdapter(private val onArticleClicked: (Article) -> Unit) :
    ListAdapter<Article, ArticleAdapter.ViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view, onArticleClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val view: ListItemArticleBinding,
        private val onClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(view.root) {

        fun bind(article: Article) {
            view.article = article
            view.root.setOnClickListener { onClick(article) }
            Glide.with(view.root.context).load(article.thumbnail).into(view.thumbnail)
        }
    }
}

private class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {

    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
