package com.example.inklink

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var menu: Menu
    private lateinit var headerUsername: TextView
    private lateinit var headerEmail: TextView
    private lateinit var headerLastLoginTime: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.mainActivity_toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
        headerView = navigationView.getHeaderView(0)
        headerUsername = headerView.findViewById(R.id.nav_header_user_name)
        headerEmail = headerView.findViewById(R.id.nav_header_user_email)
        headerLastLoginTime = headerView.findViewById(R.id.header_last_login_time)

        menu = navigationView.menu

        prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav_str, R.string.close_nav_str
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        updateNavigationMenu()
        updateHeader()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_layout, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())

            R.id.nav_articles -> replaceFragment(ArticlesFragment())

            R.id.nav_users -> replaceFragment(UsersFragment())

            R.integer.my_articles_opt_int -> replaceFragment(MyArticlesFragment())

            R.integer.profile_opt_int -> replaceFragment(UpdateProfileFragment())

            R.integer.logIn_opt_int -> {
                intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, 1)
            }

            R.integer.signUp_opt_int -> {
                intent = Intent(this, RegisterActivity::class.java)
                startActivityForResult(intent, 1)
            }

            R.integer.logout_opt_int -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Logout?")
                    setMessage("Are you sure you want to logout?")
                    setPositiveButton("Yes") { _, _ ->
                        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        prefs.edit()
                            .clear()
                            .apply()
                        intent = Intent(this@MainActivity, this@MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    setNegativeButton("No", null)
                    create()
                    show()
                }
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        recreate()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_layout, HomeFragment())

        navigationView.setCheckedItem(R.id.nav_home)

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

    private fun updateNavigationMenu() {
        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        if (!prefs.contains("userId")) {
            menu
                .add(R.id.nav_user_group, R.integer.logIn_opt_int, 1, "Login")
                .setIcon(R.drawable.ic_login)
            menu
                .add(R.id.nav_user_group, R.integer.signUp_opt_int, 2, "Sign Up")
                .setIcon(R.drawable.ic_sign_up)
        } else {
            menu
                .add(R.id.nav_user_group, R.integer.my_articles_opt_int, 1, "My Articles")
                .setIcon(R.drawable.ic_my_articles)
            menu
                .add(R.id.nav_user_group, R.integer.profile_opt_int, 2, "Profile")
                .setIcon(R.drawable.ic_update_account)
            menu
                .add(R.id.nav_user_group, R.integer.logout_opt_int, 3, "Log Out")
                .setIcon(R.drawable.ic_logout)
        }

        menu.setGroupCheckable(R.id.nav_user_group, true, false)
    }

    private fun updateHeader() {
        headerUsername.text = prefs.getString("username", "Guest")
        headerEmail.text = prefs.getString("email", "guest@inklink.com")

        if (prefs.contains("userId")) {
            val text = String.format(
                resources.getString(R.string.header_str_last_login_time),
                prefs.getString("lastLoginDate", null)
            )
            headerLastLoginTime.text = text
        } else {
            headerLastLoginTime.visibility = View.GONE
        }
    }
}