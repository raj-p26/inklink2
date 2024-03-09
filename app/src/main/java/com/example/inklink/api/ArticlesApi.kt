package com.example.inklink.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.inklink.models.Article
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ArticlesApi(context: Context) {
    private val requestQueue = Volley.newRequestQueue(context)

    companion object {
        const val URL = "http://192.168.185.216:4000"
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
                userId = obj.getString("user_id"),
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

    suspend fun newArticle(article: Article): JSONObject {
        if (article.userId == null) {
            return (JSONObject().apply {
                put("status", "failed")
                put("message", "How the hell are you able to create an article?")
            })
        }
        val jsonObject = JSONObject()
        jsonObject.put("title", article.title)
        jsonObject.put("content", article.content)
        jsonObject.put("status", article.status)
        jsonObject.put("user_id", article.userId)

        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.POST, "$URL/articles/new", jsonObject, {
                    continuation.resume(it)
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject()
                            .put("status", "failed")
                            .put("message", "Could not connect to the server")

                    continuation.resume(errObject)
                })

            requestQueue.add(request)
        }
    }

    suspend fun getArticlesOf(userId: String, type: String): Pair<ArrayList<Article>?, JSONObject?> {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.GET, "$URL/articles/$userId/$type", null, {
                    val articles = extractArticles(it)
                    continuation.resume(Pair(articles, null))
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject()
                            .put("status", "failed")
                            .put("message", "Could not connect to the server")

                    continuation.resume(Pair(null, errObject))
                })

            requestQueue.add(request)
        }
    }

    suspend fun getLatestArticlesOf(userId: String): Pair<ArrayList<Article>?, JSONObject?> {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.GET, "${UsersApi.URL}/users/$userId/latest", null, {
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

}