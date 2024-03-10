package com.example.inklink

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inklink.adapter.ArticlesAdapter
import com.example.inklink.api.ArticlesApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        floatingActionButton = view.findViewById(R.id.new_article_btn)
        recyclerView = view.findViewById(R.id.home_recycler_view)
        prefs = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        if (!prefs.contains("userId"))
            floatingActionButton.hide()

        GlobalScope.launch(Dispatchers.Main) {
            val helper = ArticlesApi(requireActivity())
            val (articles, err) = helper.getAllArticles("latest")

            if (err != null && articles == null) {
                showDialog(err.getString("message"))
                return@launch
            }

            recyclerView.adapter = ArticlesAdapter(requireActivity(), articles!!)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(requireActivity(), CreateArticleActivity::class.java)

            startActivityForResult(intent, 45)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requireActivity().recreate()
        Log.d("recreate-dbg", "$requestCode --- $resultCode")
        Log.d("recreate-dbg", "Activity <HomeFragment> recreated")
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