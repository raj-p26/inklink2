package com.example.inklink.models

data class User(
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var userName: String? = firstName,
    var email: String? = null,
    var password: String? = null,
    var about: String? = null,
    var registrationDate: String? = null,
    var lastLoginDate: String? = null
) {
    override fun toString(): String {
        return """
        User {
            id: ${id},
            firstName: ${firstName},
            lastName: ${lastName},
            userName: ${userName},
            email: ${email},
            password: ${password},
            about: ${about},
            registrationDate: ${registrationDate},
            lastLoginDate: ${lastLoginDate},
        }
        """
    }
}