package com.begoml.app.datasource

import android.content.Context
import androidx.annotation.RawRes
import com.begoml.app.R
import com.begoml.app.datasource.model.ProfileLocal
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class ApiService @Inject constructor(
    private val context: Context,
    private val gson: Gson
) {

    @Throws(IOException::class)
    private fun readJsonFromAssets(@RawRes resId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(resId)
        val builder = StringBuilder()
        inputStream.use { stream ->
            var jsonDataString: String?
            val bufferedReader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
            while (bufferedReader.readLine().also { jsonDataString = it } != null) {
                builder.append(jsonDataString)
            }
        }
        return String(builder)
    }

    fun getProfileInfo(): ProfileLocal {
        val historyDetailsLocal = readJsonFromAssets(R.raw.profile)
        return gson.fromJson(historyDetailsLocal, ProfileLocal::class.java)
    }
}
