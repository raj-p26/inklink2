package com.example.inklink

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.adapter.MyArticlesAdapter
import com.example.inklink.api.ArticlesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyArticlesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_articles, container, false)
        recyclerView = view.findViewById(R.id.my_articles_recyclerView)
        prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        GlobalScope.launch(Dispatchers.Main) {
            val userId = prefs.getString("userId", "nil")
            val helper = ArticlesApi(requireContext())
            val (articles, err) = helper.getArticlesOf(userId!!, "all")

            if (err != null) {
                showDialog(err.getString("message"))
            }

            recyclerView.adapter = MyArticlesAdapter(requireActivity(), articles!!)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requireActivity().recreate()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
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