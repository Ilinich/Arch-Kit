package com.begoml.app.tools

import android.graphics.drawable.Drawable
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.begoml.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener


fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.apply {
        divider.setDrawable(this)
        addItemDecoration(divider)
    }
}

fun ImageView.loadImage(
    imageUrl: String,
    @DrawableRes placeholderImage: Int = R.drawable.ic_launcher_background,
    @DrawableRes errorImage: Int = R.drawable.ic_launcher_background
) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(placeholderImage)
        .error(errorImage)
        .fitCenter()
        .into(this)
}
