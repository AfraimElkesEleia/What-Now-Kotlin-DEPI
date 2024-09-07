package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.whatnow.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleIdOption: GetGoogleIdOption
    private lateinit var credentialManager: CredentialManager
    val coroutineScope = lifecycleScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnSignUp = binding.btnSignUp
        val btnLogIn = binding.btnLogin
        val googleFAB = binding.FABG
        credentialManager = CredentialManager.create(this)
        googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .setAutoSelectEnabled(false)
            .build()
      //check while user is typing
        googleFAB.setOnClickListener {
            binding.progressCircular.isVisible = true
            signWithGoogle()
        }
        btnSignUp.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }
        btnLogIn.setOnClickListener {
            val i = Intent(this, LoginScreen::class.java)
            startActivity(i)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null  && currentUser.isEmailVerified) {
                        //Log.d("trace", currentUser.displayName.toString())
                        val i = Intent(this, CategoryActivity::class.java)
                        startActivity(i)
                        finish()
            }
        }

        fun signWithGoogle() {
            val request: GetCredentialRequest =
                GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
            coroutineScope.launch {
                signWithGoogleS(request)
            }
        }

        suspend fun signWithGoogleS(request: GetCredentialRequest) {
            try {
                val result = credentialManager.getCredential(context = this, request = request)
                binding.progressCircular.isVisible = false
                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential).await()
                val i = Intent(this, CategoryActivity::class.java)
                startActivity(i)
                Toast.makeText(this, "You are signed in !!", Toast.LENGTH_LONG).show()
            } catch (e: GetCredentialException) {
                //Log.d("trace","Error : ${e.message.toString() }")
                if(e.message!!.contains("available"))
                    Toast.makeText(this,"You dont have account Google in your device or check your internet",Toast.LENGTH_LONG).show()

            }catch (e: FirebaseNetworkException){
                Toast.makeText(this,"check your internet connection",Toast.LENGTH_LONG).show()
            }

        }
}
