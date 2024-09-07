package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.FavItemBinding
import com.example.whatnow.databinding.ListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavAdapter(
    val a: Activity,
    val favList: ArrayList<Favorite>,
    val currentUser: FirebaseAuth
) :
    RecyclerView.Adapter<FavAdapter.MH>() {
    val db = Firebase.firestore

    class MH(val binding: FavItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MH {
        val view = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MH(view)
    }

    override fun getItemCount(): Int = favList.size

    override fun onBindViewHolder(holder: MH, position: Int) {
        holder.binding.titleTv.text = favList[position].title
        holder.binding.sourceTv.text = favList[position].source!!.name
        Glide.with(holder.binding.articleImage.context).load(favList[position].image)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)
        val url = favList[position].url
        holder.binding.articleContainer.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }
        holder.binding.shareFab.setOnClickListener {
            ShareCompat.IntentBuilder(a).setType("text/plain")
                .setChooserTitle("Share article with: ").setText(url).startChooser()
        }
        holder.binding.favouriteFab.setOnClickListener {

            db.collection(currentUser.uid.toString()).document(favList[position].title).delete()
                .addOnSuccessListener {
                    Toast.makeText(a, "Removed from your favorites", Toast.LENGTH_SHORT).show()
                }

        }
    }


}