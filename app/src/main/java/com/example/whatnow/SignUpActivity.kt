package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.whatnow.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        val btnSignUp = binding.btnSignupScreen
        val progressBar = binding.progressBar
        val uppercase = Regex("[A-Z]")
        val specialChar = Regex("[!@#$%&_<>?{}]")
        val lowercase = Regex("[a-z]")
        val numbers = Regex("[0-9]")
        binding.emailSignUp.editText!!.doOnTextChanged { text, _, _, _ ->
            if (text!!.isBlank())
                binding.emailSignUp.error = "*Required"
            else
                binding.emailSignUp.error = null
        }
        binding.nameTextId.doOnTextChanged { text, _, _, _ ->
            if (text!!.isBlank())
                binding.nameSignUp.error = "*Required"
            else
                binding.nameSignUp.error = null
        }
        binding.passwordTextId.doOnTextChanged { text, _, _, _ ->
            if (text!!.isBlank())
                binding.passwordSignUp.error = "*Required"
            else if (text.length < 8)
                binding.passwordSignUp.error = "Password is too short"
            else if (!uppercase.containsMatchIn(text) || !specialChar.containsMatchIn(text) || !lowercase.containsMatchIn(
                    text
                ) || !numbers.containsMatchIn(text)
            ) {
                binding.passwordSignUp.error =
                    "Password should contain, lowercase, uppercase and special characters"
            }
            else
                binding.passwordSignUp.error = null
        }
        btnSignUp.setOnClickListener {
            val nameText = binding.nameSignUp.editText?.text.toString()
            val emailText = binding.emailSignUp.editText?.text.toString()
            val passwordText = binding.passwordSignUp.editText?.text.toString()
            //Log.d("trace", nameText)
            if (nameText.isBlank() || passwordText.isBlank() || emailText.isBlank()) {
                binding.nameSignUp.error = if (nameText.isBlank()) "*Required" else null
                binding.passwordSignUp.error = if (passwordText.isBlank()) "*Required" else null
                binding.emailSignUp.error = if (emailText.isBlank()) "*Required" else null
            } else if (!uppercase.containsMatchIn(passwordText) || !specialChar.containsMatchIn(passwordText) || !lowercase.containsMatchIn(
                   passwordText
                ) || !numbers.containsMatchIn(passwordText)
            ) {
                binding.passwordSignUp.error =
                    "Password should contain, lowercase, uppercase and special characters"
            }
            else {
                binding.nameSignUp.error = null
                binding.passwordSignUp.error = null
                binding.emailSignUp.error = null
                //Log.d("trace", "enter to signUp")
                progressBar.isVisible = true
                signUp(emailText.trim(), passwordText)
            }
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verifyEmail()
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                } else {
                    binding.progressBar.isVisible = false
                    val message = task.exception!!.message
                    if (message!!.contains("network"))Toast.makeText(this, "No internet connection try again!!", Toast.LENGTH_LONG).show()
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

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