package com.begoml.app.presentation.profile.model

data class ProfileUi(
    val user: String,
    val imgUrl: String,
    val userInfo: String,
    val born: String,
    val education: String,
    val movies: List<MoviesUi>
)
