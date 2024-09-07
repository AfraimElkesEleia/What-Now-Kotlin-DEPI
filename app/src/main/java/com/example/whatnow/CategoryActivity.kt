package com.example.whatnow

//bff176c0fbdf435991f92999abf57929
//https://newsapi.org

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.whatnow.databinding.ActivityCategoryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCategoryBinding
    lateinit var category: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolBar = binding.toolbar
        setSupportActionBar(toolBar)
        supportActionBar!!.title = "Explore"

        binding.generalCv.setOnClickListener {
            category = "general"
            goToNews(category)
        }
        binding.sportsCv.setOnClickListener {
            category = "sports"
            goToNews(category)
        }
        binding.businessCv.setOnClickListener {
            category = "business"
            goToNews(category)
        }
        binding.technologyCv.setOnClickListener {
            category = "technology"
            goToNews(category)
        }
        binding.entertainmentCv.setOnClickListener {
            category = "entertainment"
            goToNews(category)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i: Intent
        when (item.itemId) {
            R.id.settings -> {
                i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
            }

            R.id.logout -> {
                lifecycleScope.launch {
                    val credentialManager: CredentialManager = CredentialManager.create(this@CategoryActivity)
                    credentialManager.clearCredentialState(request = ClearCredentialStateRequest())
                }
                lifecycleScope.launch {
                    Firebase.auth.signOut()
                    intentToMainActivity()
                }
                finish()
            }

            R.id.deletAccount -> {
                val deleteAlert = alertDialogDeleteAccount()
                deleteAlert.show()
            }
            R.id.favourites ->{
                val i = Intent(this,FavoritesActivity::class.java)
                startActivity(i)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun goToNews(category: String) {
        val i = Intent(this, NewsActivity::class.java)
        i.putExtra("category", category)
        startActivity(i)
    }

    private fun intentToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun alertDialogDeleteAccount(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.baseline_delete_forever_24).setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This is will permanently erase your account")
            .setPositiveButton("Yes") { _, _ ->
                val user = Firebase.auth.currentUser!!
                user.delete()
                    .addOnCompleteListener { task ->
                        Log.d("trace", task.toString())
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account was deleted", Toast.LENGTH_LONG)
                                .show()
                            val i = Intent(this, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()
                        } else {
                            Log.d("trace", "Task dialog failure")
                        }
                    }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        return builder.create()
    }
}
