package com.example.inklink.api

import android.content.Context
import android.util.Log
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
        const val URL = "http://192.168.185.216:4000"
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
                email = obj.getString("email"),
                lastLoginDate = obj.getString("last_login_date")
            )

            users.add(user)
        }

        return users
    }

    suspend fun registerUser(user: User): Pair<User?, JSONObject?> {
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

                    user.userName = responseUser.getString("username")
                    user.email = responseUser.getString("email")
                    user.id = responseUser.getString("id")
                    user.lastLoginDate = responseUser.getString("last_login_date")
                    continuation.resume(Pair(user, null))
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

    suspend fun getUserByCredentials(email: String, password: String): Pair<User?, JSONObject?> {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.POST, "$URL/users/login", jsonObject, {
                    val response = it.getJSONObject("user")
                    val user = User(
                        id = response.getString("id"),
                        userName = response.getString("username"),
                        email = response.getString("email"),
                        lastLoginDate = response.getString("last_login_date")
                    )
                    continuation.resume(Pair(user, null))
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

    suspend fun getUserById(id: String?): Pair<User?, JSONObject?> {
        if (id == null) {
            val errObject = JSONObject()
            errObject.put("status", "failed")
            errObject.put("message", "ID is null, How?")

            return Pair(null, errObject)
        }

        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.GET, "$URL/users/$id", null, {
                    val user = getUserFromJson(it.getJSONObject("user"))
                    continuation.resume(Pair(user, null))
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

    suspend fun updateUser(jsonObject: JSONObject): Pair<Boolean, JSONObject> {
        return suspendCoroutine { continuation ->
            val request =
                JsonObjectRequest(Request.Method.PUT, "$URL/users/update", jsonObject, {
                    continuation.resume(Pair(true, it))
                }, {
                    val errObject = if (it.networkResponse != null && it.networkResponse.data != null)
                        JSONObject(String(it.networkResponse.data))
                    else
                        JSONObject().put("message", "Could not connect to the server")
                    continuation.resume(Pair(false, errObject))
                })

            requestQueue.add(request)
        }
    }

    private fun getUserFromJson(jsonObject: JSONObject): User {
        val user = User()
        user.id = jsonObject.getString("id")
        user.firstName = jsonObject.getString("first_name")
        user.lastName = jsonObject.getString("last_name")
        user.userName = jsonObject.getString("username")
        user.email = jsonObject.getString("email")
        user.about = jsonObject.getString("about")
        user.registrationDate = jsonObject.getString("registration_date")
        user.lastLoginDate = jsonObject.getString("last_login_date")
        Log.d("user-dbg", user.toString())

        return user
    }
}