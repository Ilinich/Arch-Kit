package com.begoml.app.tools

import android.content.Context
import android.os.Parcelable
import android.util.Patterns
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.begoml.app.R
import com.bumptech.glide.Glide

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

fun View.hideSoftKeyboard() {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(applicationWindowToken, 0)
    } catch (ignored: Exception) {
    }
}

fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
    children.forEach { child -> child.restoreHierarchyState(childViewStates) }
}

fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
    val childViewStates = SparseArray<Parcelable>()
    children.forEach { child -> child.saveHierarchyState(childViewStates) }
    return childViewStates
}

fun NavController.navigateSafe(
    @IdRes destination: Int?,
    action: NavController.() -> Unit
) {
    if (this.currentDestination?.id == destination) {
        action()
    }
}
