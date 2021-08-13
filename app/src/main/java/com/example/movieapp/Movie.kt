package com.example.movieapp

import androidx.annotation.DrawableRes

data class Movie(
    val title: String,
    @DrawableRes val imageResourceId: Int,
    @DrawableRes val bgResourceId: Int,
    val chips: List<String>
)