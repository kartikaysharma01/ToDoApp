package com.example.todoapp.helper

import android.content.Context
import android.content.Intent
import com.example.todoapp.LoginActivity
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
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
        // [END auth_fui_signout]
    }
}