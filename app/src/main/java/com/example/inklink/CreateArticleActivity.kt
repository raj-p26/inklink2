package com.example.inklink

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.inklink.api.ArticlesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

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

        if (intent.getStringExtra("title") != null)
            articleTitle.setText(intent.getStringExtra("title"))
        if (intent.getStringExtra("content") != null)
            articleContent.setText(intent.getStringExtra("content"))

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
            R.id.create_article_draft -> {
                if (articleTitle.text.toString().isBlank()) {
                    showMessage("Article Title cannot be blank")
                    return false
                }

                if (articleContent.text.toString().isBlank()) {
                    showMessage("Article Content cannot be blank")
                    return false
                }

                handleArticle("draft")
            }

            R.id.create_article_publish -> {
                if (articleTitle.text.toString().isBlank()) {
                    showMessage("Article Title cannot be blank")
                    return false
                }

                if (articleContent.text.toString().isBlank()) {
                    showMessage("Article Content cannot be blank")
                    return false
                }

                handleArticle("published")
            }

            R.id.create_article_discard -> {
                if (articleTitle.text.toString().isNotBlank() || articleContent.text.toString().isNotBlank()) {
                    AlertDialog.Builder(this@CreateArticleActivity).apply {
                        setTitle("Discard?")
                        setMessage("Are you sure you want to discard changes?")
                        setPositiveButton("Yes") { _, _ ->
                            setResult(Activity.RESULT_OK)
                            this@CreateArticleActivity.finishActivity(Activity.RESULT_OK)
                            return@setPositiveButton
                        }
                        setNegativeButton("No", null)

                        create()
                        show()
                    }
                }
                setResult(Activity.RESULT_OK)
                this@CreateArticleActivity.finish()
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (articleTitle.text.toString().isNotBlank() || articleContent.text.toString().isNotBlank()) {
            AlertDialog.Builder(this@CreateArticleActivity).apply {
                setTitle("Discard?")
                setMessage("Are you sure you want to discard changes?")
                setPositiveButton("Yes") { _, _ ->
                    setResult(Activity.RESULT_OK)
                    this@CreateArticleActivity.finish()
                    super.onBackPressed()
                }
                setNegativeButton("No", null)

                create()
                show()
            }
        } else {
            setResult(Activity.RESULT_OK)
            this@CreateArticleActivity.finish()
            super.onBackPressed()
        }
    }

    private fun handleArticle(status: String) {
        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val jsonObject = JSONObject().apply {
            put("title", articleTitle.text.toString())
            put("content", articleContent.text.toString())
            put("status", status)
        }

        GlobalScope.launch(Dispatchers.Main) {
            val helper = ArticlesApi(this@CreateArticleActivity)
            val result = if (intent.getStringExtra("articleId") != null) {
                jsonObject.put("id", intent.getStringExtra("articleId"))
                helper.updateArticleStatus(jsonObject)
            } else {
                jsonObject.put("user_id", prefs.getString("userId", null))
                helper.newArticle(jsonObject)
            }

            showDialogBox(result.getString("status"), result.getString("message"))
        }
    }

    private fun showDialogBox(status: String, message: String) {
        AlertDialog.Builder(this@CreateArticleActivity).apply {
            setTitle(status)
            setMessage(message)
            setCancelable(false)
            setPositiveButton("Ok") { _, _ ->
                setResult(Activity.RESULT_OK)
                this@CreateArticleActivity.finish()
            }

            create()
            show()
        }
    }

    private fun showMessage(message: String) {
        AlertDialog.Builder(this@CreateArticleActivity).apply {
            setTitle("Info")
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok", null)

            create()
            show()
        }
    }
}