package com.example.inklink

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.inklink.api.UsersApi
import com.example.inklink.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firstNameEditText = findViewById(R.id.register_firstName)
        lastNameEditText = findViewById(R.id.register_lastName)
        emailEditText = findViewById(R.id.register_email)
        passwordEditText = findViewById(R.id.register_password)
        confirmPasswordEditText = findViewById(R.id.register_confirmPassword)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            if (!isValid()) return@setOnClickListener

            registerButton.isClickable = false
            registerButton.isEnabled = false

            val user = User(
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                email = emailEditText.text.toString(),
                password = passwordEditText.text.toString()
            )
            val helper = UsersApi(this)
            GlobalScope.launch(Dispatchers.Main) {
                val (responseUser, err) = helper.registerUser(user)

                if (err != null) {
                    showDialog(err.getString("message"))
                    return@launch
                }

                val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("userId", responseUser!!.id)
                editor.putString("email", responseUser.email)
                editor.putString("username", responseUser.userName)
                editor.putString("lastLoginDate", responseUser.lastLoginDate)

                editor.apply()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun isValid(): Boolean {
        if (firstNameEditText.text.toString().isBlank()) {
            showError(firstNameEditText, "First name cannot be blank")
            return false
        }

        if (lastNameEditText.text.toString().isBlank()) {
            showError(lastNameEditText, "Last name cannot be blank")
            return false
        }

        if (emailEditText.text.toString().isBlank()) {
            showError(emailEditText, "Email cannot be blank")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
            showError(emailEditText, "Invalid Email address")
            return false
        }

        if (passwordEditText.text.toString().length < 8) {
            showError(passwordEditText, "Password must be 8 characters long")
            return false
        }

        if (confirmPasswordEditText.text.toString() != passwordEditText.text.toString()) {
            showError(confirmPasswordEditText, "Passwords dont match")
            return false
        }

        return true
    }

    private fun showError(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this@RegisterActivity).apply {
            setTitle("Error")
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok", null)

            create()
            show()
        }
        registerButton.isClickable = true
        registerButton.isEnabled = true
    }
}