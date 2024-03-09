package com.example.inklink

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.example.inklink.api.UsersApi
import com.example.inklink.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpdateProfileFragment : Fragment() {
    private lateinit var updateFirstName: EditText
    private lateinit var updateLastName: EditText
    private lateinit var updateUsername: EditText
    private lateinit var updateEmail: EditText
    private lateinit var updatePassword: EditText
    private lateinit var updateAbout: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_profile, container, false)
        updateFirstName = view.findViewById(R.id.update_firstName)
        updateLastName = view.findViewById(R.id.update_lastName)
        updateUsername = view.findViewById(R.id.update_username)
        updateEmail = view.findViewById(R.id.update_email)
        updatePassword = view.findViewById(R.id.update_password)
        updateAbout = view.findViewById(R.id.update_about)
        updateProfileButton = view.findViewById(R.id.update_profile_button)
        prefs = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        updateProfileButton.isClickable = false
        updateProfileButton.isEnabled = false

        getAndSetData()

        return view
    }

    private fun getAndSetData() {
        GlobalScope.launch(Dispatchers.Main) {
            val helper = UsersApi(requireContext())
            val (user, err) = helper.getUserById(prefs.getString("userId", null))

            if (err != null) {
                showDialog(err.getString("status"), err.getString("message"))
                return@launch
            }

            this@UpdateProfileFragment.user = user!!
            updateFirstName.setText(user.firstName)
            updateFirstName.doAfterTextChanged { handleChange(it.toString(), user.firstName!!) }
            updateLastName.setText(user.lastName)
            updateLastName.doAfterTextChanged { handleChange(it.toString(), user.lastName!!) }
            updateUsername.setText(user.userName)
            updateUsername.doAfterTextChanged { handleChange(it.toString(), user.userName!!) }
            updateEmail.setText(user.email)
            updateEmail.doAfterTextChanged { handleChange(it.toString(), user.email!!) }
            updateAbout.setText(user.about)
            updateAbout.doAfterTextChanged { handleChange(it.toString(), user.about!!) }
            updatePassword.doAfterTextChanged {
                if (it.toString().length < 8) {
                    updateProfileButton.isClickable = false
                    updateProfileButton.isEnabled = false
                } else {
                    updateProfileButton.isClickable = true
                    updateProfileButton.isEnabled = true
                }
            }
        }

        updateProfileButton.setOnClickListener {
            if (updatePassword.text.toString().isNotBlank() &&
                    updatePassword.text.toString().length < 8) {
                Toast.makeText(requireContext(), "Password should be atleast 8 chars long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestObject = JSONObject().apply {
                put("id", prefs.getString("userId", null))
                put("first_name", updateFirstName.text.toString())
                put("last_name", updateLastName.text.toString())
                put("username", updateUsername.text.toString())
                if (user.email != updateEmail.text.toString())
                    put("email", updateEmail.text.toString())
                if (updatePassword.text.toString().isNotBlank())
                    put("password", updatePassword.text.toString())
                put("about", updateAbout.text.toString())
            }

            makeRequest(requestObject)
        }
    }

    private fun makeRequest(requestObject: JSONObject) {
        GlobalScope.launch(Dispatchers.Main) {
            val helper = UsersApi(requireContext())
            val (_, response) = helper.updateUser(requestObject)
            showDialog(response.getString("status"), response.getString("message"))

        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            setNeutralButton("Ok") { _, _ ->
                if (title != "failed") {
                    fragmentManager
                        ?.beginTransaction()
                        ?.attach(HomeFragment())
                        ?.detach(this@UpdateProfileFragment)
                        ?.commit()
                }
            }

            create()
            show()
        }
    }

    private fun handleChange(changedValue: String, originalValue: String) {
        if (changedValue == originalValue) {
            updateProfileButton.isClickable = false
            updateProfileButton.isEnabled = false
        } else {
            updateProfileButton.isClickable = true
            updateProfileButton.isEnabled = true
        }
    }
}