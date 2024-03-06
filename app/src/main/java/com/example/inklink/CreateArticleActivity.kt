package com.example.inklink

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.inklink.api.ArticlesApi
import com.example.inklink.models.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateArticleActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var articleTitle: EditText
    private lateinit var articleContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)
        toolbar = findViewById(R.id.createArticle_toolbar)
        articleTitle = findViewById(R.id.new_article_title)
        articleContent = findViewById(R.id.new_article_content)

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.create_article_opt_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_article_draft -> createArticle("draft")
            R.id.create_article_publish -> createArticle("published")
            R.id.create_article_discard -> {
                Log.d("TODO", "TODO discard")
            }
        }

        return true
    }

    private fun createArticle(status: String) {
        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val article = Article()
        article.userId = prefs.getString("userId", null)
        article.title = articleTitle.text.toString()
        article.content = articleContent.text.toString()
        article.status = status
        GlobalScope.launch(Dispatchers.Main) {
            val helper = ArticlesApi(this@CreateArticleActivity)
            val result = helper.newArticle(article)

            showDialogBox(result.getString("status"), result.getString("message"))
            if (result.getString("status") != "failed")
                setResult(Activity.RESULT_OK)
            finish()
            }
    }

    private fun showDialogBox(status: String, message: String) {
        AlertDialog.Builder(this@CreateArticleActivity).apply {
            setTitle(status)
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok") { _, _ ->
                this@CreateArticleActivity.finish()
            }

            create()
            show()
        }
    }
}