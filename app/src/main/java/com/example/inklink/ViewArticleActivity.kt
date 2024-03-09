package com.example.inklink

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class ViewArticleActivity : AppCompatActivity() {
    private lateinit var viewArticleTitle: TextView
    private lateinit var viewArticleContent: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_article)
        viewArticleTitle = findViewById(R.id.view_article_title)
        viewArticleContent = findViewById(R.id.view_article_content)
        toolbar = findViewById(R.id.view_article_toolBar)
        prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        viewArticleTitle.text = intent.getStringExtra("title")
        viewArticleContent.text = intent.getStringExtra("content")
        toolbar.title = intent.getStringExtra("title")

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val userId = prefs.getString("userId", null)
        val articleStatus = intent.getStringExtra("article_status")

        if (userId == null)
            return true

        if (userId == intent.getStringExtra("user_id") && articleStatus == "published")
            return true

        menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "Edit")
        menu.add(Menu.NONE, Menu.FIRST + 1, Menu.FIRST + 1, "Delete")

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST -> Toast.makeText(this, "Editing article", Toast.LENGTH_SHORT).show()
            Menu.FIRST+1 -> Toast.makeText(this, "Deleting article", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}