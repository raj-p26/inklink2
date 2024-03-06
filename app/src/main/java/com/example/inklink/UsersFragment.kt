package com.example.inklink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.adapter.UsersAdapter
import com.example.inklink.api.UsersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerView = view.findViewById(R.id.users_recycler_view)

        GlobalScope.launch(Dispatchers.Main) {
            val helper = UsersApi(context!!)
            val (users, err) = helper.getAllUsers()

            if (err != null && users == null) {
                showDialog(err.getString("message"))
                return@launch
            }

            recyclerView.adapter = UsersAdapter(requireActivity(), users!!)
            recyclerView.layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(context!!).apply {
            setTitle("Error")
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok") { _, _ ->
                activity!!.finish()
            }

            create()
            show()
        }
    }
}