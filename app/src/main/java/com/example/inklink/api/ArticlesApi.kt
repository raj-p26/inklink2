package com.example.inklink.api

import android.content.Context
import android.util.Log
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

    suspend fun newArticle(obj: JSONObject): JSONObject {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.POST, "$URL/articles/new", obj, {
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

    suspend fun updateArticleStatus(article: JSONObject): JSONObject {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.PUT, "$URL/articles/update_status", article, {
                    continuation.resume(it)
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject().put("message", "Could not connect to the server")
                    continuation.resume(errObject)
                })

            requestQueue.add(request)
        }
    }

    suspend fun deleteArticle(id: String, userId: String): JSONObject {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.DELETE, "$URL/articles/delete/$id?user_id=$userId", null, {
                    continuation.resume(it)
                }, {
                    Log.d("it-dbg", it.toString())
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null) {
                        Log.d("it-dbg", String(it.networkResponse.data))
                        JSONObject(String(it.networkResponse.data))
                    } else
                        JSONObject().put("message", "Could not connect to the server")
                    continuation.resume(errObject)
                })

            requestQueue.add(request)
        }
    }

}