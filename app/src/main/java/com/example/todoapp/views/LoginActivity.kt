package com.example.todoapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        hideStatusBar()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )
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
        } else {
            if (result.idpResponse == null)
                snack(binding.root, getString(R.string.login_cancel_back_press), false)
            else
                snack(binding.root, getString(R.string.login_failed, result.idpResponse!!.error?.errorCode), false)
            // todo: Restart login activity
            Log.i("logged in user details: ", "login failed")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

//    private fun themeAndLogo() {
//        val providers = emptyList<AuthUI.IdpConfig>()
//
//        // [START auth_fui_theme_logo]
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setLogo(R.drawable.my_great_logo) // Set logo drawable
//            .setTheme(R.style.MySuperAppTheme) // Set theme
//            .build()
//        signInLauncher.launch(signInIntent)
//        // [END auth_fui_theme_logo]
//    }

}