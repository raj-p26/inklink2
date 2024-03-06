package com.example.inklink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.R
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
        }
    }
}