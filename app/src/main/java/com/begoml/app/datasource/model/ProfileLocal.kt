package com.begoml.app.datasource.model

data class ProfileLocal(
    val user: String,
    val imgUrl: String,
    val userInfo: String,
    val born: String,
    val education: String,
    val movies: List<MoviesLocal>
)
