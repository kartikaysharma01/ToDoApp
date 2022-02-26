package com.example.todoapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityLoginBinding
import com.example.todoapp.utils.hideStatusBar
import com.example.todoapp.utils.snack
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        this.onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
        hideStatusBar()
        initView()
    }

    private fun initView() {
        binding.btnSignIn.setOnClickListener {
            createSignInIntent()
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (result.idpResponse == null)
                snack(binding.root, getString(R.string.login_cancel_back_press), false)
            else
                snack(binding.root, getString(R.string.login_failed, result.idpResponse!!.error?.errorCode), false)
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

}