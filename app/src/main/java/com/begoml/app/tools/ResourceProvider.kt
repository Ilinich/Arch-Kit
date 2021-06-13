package com.begoml.app.tools

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import javax.inject.Inject

interface ResourceProvider {

    val packageName: String

    fun getString(@StringRes res: Int): String

    fun getString(@StringRes res: Int, vararg args: Any): String

    fun getStringArray(@ArrayRes res: Int): List<String>

    fun getColor(@ColorRes res: Int): Int
}

class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {

    override val packageName: String
        get() = context.packageName

    override fun getString(res: Int) = context.resources.getString(res)

    override fun getString(res: Int, vararg args: Any) = context.resources.getString(res, *args)

    override fun getStringArray(res: Int): List<String> = context.resources.getStringArray(res).asList()

    override fun getColor(res: Int) = ContextCompat.getColor(context, res)
}
