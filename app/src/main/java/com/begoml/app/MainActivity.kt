package com.begoml.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.begoml.app.mvi.LoginMviFeature

class MainActivity : AppCompatActivity() {

    private val viewModel = LoginMviFeature()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
