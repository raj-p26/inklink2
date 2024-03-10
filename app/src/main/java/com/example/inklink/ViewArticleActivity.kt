package com.example.inklink

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.inklink.api.ArticlesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            Menu.FIRST -> {
                val editIntent = Intent((this as Activity), CreateArticleActivity::class.java)
                editIntent.putExtra("articleId", intent.getStringExtra("article_id"))
                editIntent.putExtra("title", intent.getStringExtra("title"))
                editIntent.putExtra("content", intent.getStringExtra("content"))

                startActivityForResult(editIntent, Activity.RESULT_OK)
            }
            Menu.FIRST+1 -> {
                AlertDialog.Builder(this@ViewArticleActivity).apply {
                    setTitle("Delete article?")
                    setMessage("Are you sure you want to delete this article?")
                    setPositiveButton("Yes") { _, _ -> deleteArticle() }
                    setNegativeButton("No", null)

                    create()
                    show()
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        recreate()
        Log.d("recreate-dbg", "$requestCode --- $resultCode")
        Log.d("recreate-dbg", "Activity <ViewArticle> recreated")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
        super.onBackPressed()
    }

    private fun deleteArticle() {
        GlobalScope.launch(Dispatchers.Main) {
            val id = intent.getStringExtra("article_id")
            val userId = prefs.getString("userId", null)
            val helper = ArticlesApi(this@ViewArticleActivity)
            val response = helper.deleteArticle(id!!, userId!!)

            showDialog(response.getString("status"), response.getString("message"))
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this@ViewArticleActivity).apply {
            setTitle(title)
            setMessage(message)
            setNeutralButton("Ok") { _, _ ->
                setResult(Activity.RESULT_OK)
                this@ViewArticleActivity.finish()
            }
            create()
            show()
        }
    }
}