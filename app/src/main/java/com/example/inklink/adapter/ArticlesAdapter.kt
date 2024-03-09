package com.example.inklink.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.R
import com.example.inklink.ViewArticleActivity
import com.example.inklink.models.Article

internal class ArticlesAdapter(private val activity: FragmentActivity, private val articles: ArrayList<Article>) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val view = inflater.inflate(R.layout.articles_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.article = articles[position]
        holder.setDetails()
    }

    override fun getItemCount(): Int = articles.size

    internal inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        var article = Article()

        fun setDetails() {
            val articleTitle = view.findViewById<TextView>(R.id.articles_row_title)
            articleTitle.text = article.title
            val articleContent = view.findViewById<TextView>(R.id.articles_row_content)
            articleContent.text = article.content

            val cardView = view.findViewById<CardView>(R.id.articles_row_cardView)
            cardView.setOnClickListener {
                val intent = Intent(activity.applicationContext, ViewArticleActivity::class.java)
                intent.putExtra("title", article.title)
                intent.putExtra("content", article.content)
                intent.putExtra("user_id", article.userId)
                intent.putExtra("article_status", article.status)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                activity.startActivity(intent)
            }
        }
    }
}