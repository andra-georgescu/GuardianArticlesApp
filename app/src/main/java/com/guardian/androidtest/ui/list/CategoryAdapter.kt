package com.guardian.androidtest.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.guardian.androidtest.domain.Article
import com.guardian.androidtest.domain.Category
import com.guardian.androidtest.databinding.ListItemArticleCategoryBinding

class CategoryAdapter(private val onArticleClicked: (Article) -> Unit) :
    ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListItemArticleCategoryBinding.inflate(
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
        private val view: ListItemArticleCategoryBinding,
        private val onClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(view.root) {

        fun bind(category: Category) {
            view.title.setText(category.type.titleRes)

            val adapter = ArticleAdapter(onClick).also { it.submitList(category.articles) }
            view.articles.layoutManager = LinearLayoutManager(view.root.context)
            view.articles.adapter = adapter
        }
    }
}

private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {

    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}