package com.example.inklink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.adapter.ArticlesAdapter
import com.example.inklink.api.ArticlesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewUserActivity : AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_user)
        firstNameEditText = findViewById(R.id.viewUser_first_name)
        lastNameEditText = findViewById(R.id.viewUser_last_name)
        usernameEditText = findViewById(R.id.viewUser_username)
        emailEditText = findViewById(R.id.viewUser_email)
        aboutEditText = findViewById(R.id.viewUser_about)
        recyclerView = findViewById(R.id.viewUserArticles_recycler_view)

        setData()

        GlobalScope.launch(Dispatchers.Main) {
            val helper = ArticlesApi(this@ViewUserActivity)
            val (articles, err) = helper.getLatestArticlesOf(intent.getStringExtra("userId")!!)

            if (err != null) {
                showDialog(err.getString("message"))
                return@launch
            }

            recyclerView.adapter = ArticlesAdapter(this@ViewUserActivity, articles!!)
            recyclerView.layoutManager = LinearLayoutManager(this@ViewUserActivity)
        }
    }

    private fun setData() {
        firstNameEditText.setText(intent.getStringExtra("firstName"))
        firstNameEditText.isEnabled = false
        lastNameEditText.setText(intent.getStringExtra("lastName"))
        lastNameEditText.isEnabled = false
        usernameEditText.setText(intent.getStringExtra("username"))
        usernameEditText.isEnabled = false
        emailEditText.setText(intent.getStringExtra("email"))
        emailEditText.isEnabled = false
        aboutEditText.setText(intent.getStringExtra("about"))
        aboutEditText.isEnabled = false
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this@ViewUserActivity).apply {
            setTitle("Error")
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok") { _, _ ->
                this@ViewUserActivity.finish()
            }

            create()
            show()
        }
    }
}