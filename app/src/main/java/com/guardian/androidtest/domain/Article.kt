package com.guardian.androidtest.domain

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Article(
    val id: String,
    val thumbnail: String,
    val sectionId: String,
    val sectionName: String,
    val published: Date,
    val title: String,
    val body: String,
    val url: String,
    var isFavourite: Boolean? = null
) : Serializable {
    fun displayDate(): String = SimpleDateFormat("dd/MM/yyyy").format(published)
}

fun List<Article>.toCategories(): List<Category> {

    fun Date.getWeekOfYear(): Int =
        Calendar.getInstance().apply { time = this@getWeekOfYear }.get(Calendar.WEEK_OF_YEAR)

    val currentWeek = Date().getWeekOfYear()
    val lastWeek = if (currentWeek - 1 > 0) currentWeek - 1 else 52
    val groupedArticles = groupBy {
        when {
            it.id in favourites() -> Category.CategoryType.FAVOURITES
            it.published.getWeekOfYear() == currentWeek -> Category.CategoryType.THIS_WEEK
            it.published.getWeekOfYear() == lastWeek -> Category.CategoryType.LAST_WEEK
            else -> Category.CategoryType.OLDER
        }
    }

    val categories = mutableListOf<Category>()

    Category.CategoryType.values().forEach { type ->
        groupedArticles[type]?.let { articles ->
            categories.add(
                Category(
                    type,
                    articles.sortedByDescending { it.published })
            )
        }
    }

    return categories
}

fun List<Article>.favourites() = filter { it.isFavourite == true }.map { it.id }
