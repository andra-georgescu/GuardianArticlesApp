package com.guardian.androidtest.domain

import com.guardian.androidtest.domain.Category.CategoryType
import org.junit.Test
import java.util.*

class CategoryMappingTest {

    @Test
    fun articlesAreMappedCorrectlyToCategories() {
        val favArticle = Article("favId", "", "", "", Date(), "", "", "", true)
        val thisWeekArticle = Article("id1", "", "", "", Date(), "", "", "")
        val lastWeekArticle =
            Article("id2", "", "", "", getPastDate(7), "", "", "")
        val olderArticle = Article("id3", "", "", "", getPastDate(30), "", "", "")
        val articles = listOf(favArticle, thisWeekArticle, lastWeekArticle, olderArticle)

        val categories = articles.toCategories()

        assert(categories.first { it.type == CategoryType.FAVOURITES }.articles == listOf(favArticle))
        assert(
            categories.first { it.type == CategoryType.THIS_WEEK }.articles == listOf(
                thisWeekArticle
            )
        )
        assert(
            categories.first { it.type == CategoryType.LAST_WEEK }.articles == listOf(
                lastWeekArticle
            )
        )
        assert(categories.first { it.type == CategoryType.OLDER }.articles == listOf(olderArticle))
    }

    @Test
    fun categoriesAreOrderedAsShouldBeDisplayed() {
        val favArticle = Article("favId", "", "", "", Date(), "", "", "", true)
        val thisWeekArticle = Article("id1", "", "", "", Date(), "", "", "")
        val lastWeekArticle =
            Article("id2", "", "", "", getPastDate(7), "", "", "")
        val olderArticle = Article("id3", "", "", "", getPastDate(30), "", "", "")
        val articles = listOf(favArticle, thisWeekArticle, lastWeekArticle, olderArticle)

        val categories = articles.toCategories()

        assert(categories[0].type == CategoryType.FAVOURITES)
        assert(categories[1].type == CategoryType.THIS_WEEK)
        assert(categories[2].type == CategoryType.LAST_WEEK)
        assert(categories[3].type == CategoryType.OLDER)
    }

    @Test
    fun emptyCategoriesDontGetMapped() {
        val favArticle = Article("favId", "", "", "", Date(), "", "", "", true)
        val olderArticle = Article("id3", "", "", "", getPastDate(30), "", "", "")
        val articles = listOf(favArticle, olderArticle)

        val categories = articles.toCategories()

        assert(categories.size == 2)
        assert(categories[0].type == CategoryType.FAVOURITES)
        assert(categories[1].type == CategoryType.OLDER)
    }

    @Test
    fun articlesAreSortedByDateDescending() {
        val oldArticle = Article("id1", "", "", "", getPastDate(29), "", "", "")
        val olderArticle =
            Article("id2", "", "", "", getPastDate(30), "", "", "")
        val oldestArticle = Article("id3", "", "", "", getPastDate(31), "", "", "")
        val articles = listOf(olderArticle, oldArticle, oldestArticle)
        val sortedArticles = listOf(oldArticle, olderArticle, oldestArticle)

        val categories = articles.toCategories()

        assert(categories.first { it.type == CategoryType.OLDER }.articles == sortedArticles)
    }

    private fun getPastDate(daysAgo: Int): Date = Calendar.getInstance().let {
        it.add(Calendar.DAY_OF_MONTH, -1 * daysAgo)
        it.time
    }
}