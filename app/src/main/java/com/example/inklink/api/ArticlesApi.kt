package com.example.inklink.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.inklink.models.Article
import com.example.inklink.models.User
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ArticlesApi(context: Context) {
    private val requestQueue = Volley.newRequestQueue(context)

    companion object {
        const val URL = "http://192.168.113.215:4000"
    }

    suspend fun getAllArticles(type: String): Pair<ArrayList<Article>?, JSONObject?> {
        return suspendCoroutine { continuation ->
            val request = JsonObjectRequest(Request.Method.GET, "$URL/articles/$type", null, {
                val articles = extractArticles(it)
                continuation.resume(Pair(articles, null))
            }, {
                val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                    JSONObject(String(it.networkResponse.data))
                else
                    JSONObject().put("message", "Could not connect to the server")

                continuation.resume(Pair(null, errObject))
            })

            requestQueue.add(request)
        }
    }

    private fun extractArticles(it: JSONObject): ArrayList<Article> {
        val articles = ArrayList<Article>()
        val array = it.getJSONArray("articles")
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val article = Article(
                id = obj.getString("id"),
                authorName = obj.getString("author"),
                title = obj.getString("title"),
                content = obj.getString("content"),
                reportCount = obj.getInt("report_count"),
                status = obj.getString("status"),
                creationDate = obj.getString("creation_date")
            )

            articles.add(article)
        }

        return articles
    }
}