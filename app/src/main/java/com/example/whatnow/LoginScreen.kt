package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.whatnow.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        val loginButton = binding.btnLoginScreen
        val forgetPass = binding.forgetPass
        binding.emailSignInText.doOnTextChanged{
            text,_,_,_ -> binding.emailSignIn.error = if(text!!.isBlank()) "*Required" else null
        }
        binding.passwordSignInText.doOnTextChanged{
                text,_,_,_ -> binding.passwordSignIn.error = if(text!!.isBlank())"*Required" else null
        }
        loginButton.setOnClickListener {
            val emailText = binding.emailSignIn.editText?.text.toString()
            val passwordText = binding.passwordSignIn.editText?.text.toString()
            if (emailText.isBlank() || passwordText.isBlank()) {
                binding.emailSignIn.error = if(emailText.isBlank())"*Required" else null
                binding.passwordSignIn.error = if(passwordText.isBlank())"*Required" else null
            } else {
                binding.emailSignIn.error = null
                binding.passwordSignIn.error = null
                binding.progressBar.isVisible = true
                login(emailText.trim(), passwordText)
            }
        }
        forgetPass.setOnClickListener {
            val i = Intent(this, ForgetPassActivity::class.java)
            startActivity(i)
        }

    }

    private fun login(email: String, password: String) {
        binding.progressBar.isVisible = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.isVisible = false
                    if (auth.currentUser!!.isEmailVerified) {
                        //Go to News Page
                        val i = Intent(this, CategoryActivity::class.java)
                        startActivity(i)
                        finishAffinity()
                    } else {
                        binding.progressBar.isVisible = false
                        verifyEmail()
                        Toast.makeText(this, "Check your email first", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.progressBar.isVisible = false
                    // If sign in fails, display a message to the user.
                    val message = task.exception!!.message
                    when {
                        message!!.contains("incorrect") -> Toast.makeText(
                            this,
                            "Email or Password is incorrect !!",
                            Toast.LENGTH_LONG
                        ).show()
                        message.contains("network")-> Toast.makeText(this, "No internet connection try again!!", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun verifyEmail() {
        Toast.makeText(this, "Check Your Email", Toast.LENGTH_LONG).show()
        auth.currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar.isVisible = false
                }
            }
    }
}
