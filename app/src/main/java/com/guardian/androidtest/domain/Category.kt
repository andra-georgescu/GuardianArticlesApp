package com.guardian.androidtest.domain

import androidx.annotation.StringRes
import com.guardian.androidtest.R

data class Category(val type: CategoryType, val articles: List<Article>) {

    enum class CategoryType(@StringRes val titleRes: Int) {
        FAVOURITES(R.string.favourites),
        THIS_WEEK(R.string.this_week),
        LAST_WEEK(R.string.last_week),
        OLDER(R.string.older)
    }
}