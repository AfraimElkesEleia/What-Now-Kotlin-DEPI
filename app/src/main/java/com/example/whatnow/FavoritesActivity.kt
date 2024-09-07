package com.example.whatnow

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.whatnow.databinding.ActivityFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class FavoritesActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavoritesBinding
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var array: ArrayList<Favorite> = ArrayList()
    lateinit var rv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = Firebase.firestore
        auth = Firebase.auth
        rv = binding.favouritesList
        val adapter = FavAdapter(this, array, auth)
        rv.adapter = adapter
        getDataChangeListener()

    }

    fun getDataChangeListener() {
        db.collection(auth.currentUser!!.uid.toString()).addSnapshotListener { value, error ->
            Log.d("trace", auth.currentUser!!.uid)
            if (error != null) {
                Log.d("trace", error.message.toString())
            } else {
                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d("trace", "New Doc: ${dc.document.data}")
                            array.add(dc.document.toObject(Favorite::class.java))
                            Log.d("trace", dc.document.toObject(Favorite::class.java).toString())
                            Log.d("trace", array.toString())
                            rv.adapter = FavAdapter(this, array, auth)
                        }

                        else -> {
                            Log.d("trace", "Removed Doc: ${dc.document.data}")
                            array.remove(dc.document.toObject(Favorite::class.java))
                            rv.adapter = FavAdapter(this, array, auth)
                        }
                    }
                }
            }
        }
    }

   /* private fun getDate(array: ArrayList<Favorite>) {
        db.collection(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("trace", "${document.id} => ${document.data}")
                    array.add(
                        Favorite(
                            title = document.data["title"] as String,
                            url = document.data["url"] as String,
                            image = document.data["image"] as String,
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d("trace", "Error getting documents: ", exception)
            }
    }*/
}