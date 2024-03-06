package com.example.inklink

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Patterns
import com.example.inklink.api.UsersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.login_button)
        emailEditText = findViewById(R.id.login_email_editText)
        passwordEditText = findViewById(R.id.login_password_editText)

        loginButton.setOnClickListener {
            if (!isValid()) return@setOnClickListener

            GlobalScope.launch(Dispatchers.Main) {
                val handler = UsersApi(applicationContext)
                val (user, err, token) = handler.getUserByCredentials(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )

                if (err != null) {
                    showDialog(err.getString("message"))
                    return@launch
                }

                val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("userId", user!!.id)
                editor.putString("email", user.email)
                editor.putString("username", user.userName)
                editor.putString("userToken", token!!)

                editor.apply()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun isValid(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
            showError(emailEditText, "Invalid Email Address")
            return false
        }

        if (passwordEditText.text.toString().isBlank()) {
            showError(passwordEditText, "Password cannot be blank")
            return false
        }

        if (passwordEditText.text.toString().length < 8) {
            showError(passwordEditText, "Password must be 8 characters long")
            return false
        }
        return true
    }

    private fun showError(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setNeutralButton("Ok", null)
        builder.setCancelable(false)

        builder.create().show()
    }
}