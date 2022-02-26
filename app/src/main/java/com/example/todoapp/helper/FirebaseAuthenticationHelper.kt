package com.example.todoapp.helper

import android.content.Context
import android.content.Intent
import com.example.todoapp.views.LoginActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthenticationHelper {

    fun isLoggedIn() : Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    fun getCurrentUserUid() : String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser!!.uid
    }

    fun signOut(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
    }
}