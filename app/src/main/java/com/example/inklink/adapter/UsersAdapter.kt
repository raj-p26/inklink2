package com.example.inklink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.R
import com.example.inklink.models.User

internal class UsersAdapter(private val activity: FragmentActivity, private val users: ArrayList<User>) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val myView = inflater.inflate(R.layout.users_row, parent, false)

        return ViewHolder(myView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user = users[position]
        holder.setDetails()
    }

    override fun getItemCount(): Int = users.size

    internal inner class ViewHolder(private val myView: View) : RecyclerView.ViewHolder(myView) {
        var user = User()
        fun setDetails() {
            val usernameTextView = myView.findViewById<TextView>(R.id.users_row_username)
            usernameTextView.text = user.userName
            val emailTextView = myView.findViewById<TextView>(R.id.users_row_email)
            emailTextView.text = user.email
        }
    }
}