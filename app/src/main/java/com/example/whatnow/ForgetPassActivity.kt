package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.whatnow.databinding.ActivityForgetPassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPassActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityForgetPassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.emailText.editText!!.doOnTextChanged{text,_,_,_ -> binding.emailText.error = if(text!!.isBlank()) "*Required" else null}
        val resetBtn = binding.resetBtn
        resetBtn.setOnClickListener {
            val email = binding.emailText.editText!!.text.toString()
            if (email.isBlank()) {
                binding.emailText.error = "*Required"
            }
            else {
                binding.emailText.error = null
                resetPass(email.trim())
            }
        }
    }

    private fun resetPass(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, LoginScreen::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val message = task.exception!!.message
                    Log.d("trace", message!!)
                }
            }
    }
}
