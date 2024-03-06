package com.example.inklink.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.inklink.models.User
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UsersApi(context: Context) {
    private var requestQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        const val URL = "http://192.168.113.215:4000"
    }

    suspend fun getAllUsers(): Pair<ArrayList<User>?, JSONObject?> {
        return suspendCoroutine { continuation ->
            val request = JsonObjectRequest(Request.Method.GET, "$URL/users/", null, {
                val users = extractJsonObj(it)
                continuation.resume(Pair(users, null))
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

    private fun extractJsonObj(it: JSONObject): ArrayList<User> {
        val users = ArrayList<User>()
        val array = it.getJSONArray("users")
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val user = User(
                id = obj.getString("id"),
                userName = obj.getString("username"),
                email = obj.getString("email")
            )

            users.add(user)
        }

        return users
    }

    suspend fun registerUser(user: User): Triple<User?, JSONObject?, String?> {
        val jsonObject = JSONObject()
        jsonObject.put("first_name", user.firstName)
        jsonObject.put("last_name", user.lastName)
        jsonObject.put("username", user.userName)
        jsonObject.put("email", user.email)
        jsonObject.put("password", user.password)

        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.POST, "$URL/users/signup", jsonObject, {
                    val responseUser = it.getJSONObject("user")
                    val token = it.getString("token")

                    user.firstName = responseUser.getString("first_name")
                    user.lastName = responseUser.getString("last_name")
                    user.email = responseUser.getString("email")
                    user.id = responseUser.getString("id")
                    continuation.resume(Triple(user, null, token))
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject().put("message", "Could not connect to the server")

                    continuation.resume(Triple(null, errObject, null))
                })
            requestQueue.add(request)
        }
    }

    suspend fun getUserByCredentials(email: String, password: String): Triple<User?, JSONObject?, String?> {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.POST, "$URL/users/login", jsonObject, {
                    val response = it.getJSONObject("user")
                    val token = it.getString("token")
                    val user = User(
                        id = response.getString("id"),
                        userName = response.getString("username"),
                        email = response.getString("email")
                    )
                    continuation.resume(Triple(user, null, token))
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject().put("message", "Could not connect to the server")
                    continuation.resume(Triple(null, errObject, null))
                })
            requestQueue.add(request)
        }
    }
}