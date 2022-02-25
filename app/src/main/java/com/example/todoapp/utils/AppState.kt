package com.example.todoapp.utils

import android.content.Context

class AppState private constructor(context: Context) {

    enum class Key {
        USER_TOKEN_STR,
        EMAIL_STR,
        NAME_STR,
        IS_LOGGED_IN,
    }
}