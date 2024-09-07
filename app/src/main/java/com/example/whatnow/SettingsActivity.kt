package com.example.whatnow

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var checkedCountry = pref.getString("country", "us")
        when (checkedCountry) {
            "us" -> binding.checkIconUs.isVisible = true
            "eg" -> binding.checkIconEg.isVisible = true
            "fr" -> binding.checkIconFr.isVisible = true
            "gb" -> binding.checkIconUk.isVisible = true
        }
        binding.egCv.setOnClickListener {
            putSP("eg")
            binding.checkIconEg.isVisible = true
            binding.checkIconFr.isVisible = false
            binding.checkIconUk.isVisible = false
            binding.checkIconUs.isVisible = false
        }
        binding.usCv.setOnClickListener {
            putSP("us")
            binding.checkIconUs.isVisible = true
            binding.checkIconFr.isVisible = false
            binding.checkIconUk.isVisible = false
            binding.checkIconEg.isVisible = false
        }
        binding.frCv.setOnClickListener {
            putSP("fr")
            binding.checkIconFr.isVisible = true
            binding.checkIconUs.isVisible = false
            binding.checkIconUk.isVisible = false
            binding.checkIconEg.isVisible = false

        }
        binding.ukCv.setOnClickListener {
            putSP("gb")
            binding.checkIconUk.isVisible = true
            binding.checkIconUs.isVisible = false
            binding.checkIconFr.isVisible = false
            binding.checkIconEg.isVisible = false
        }
    }

    fun putSP(country: String) {
        val pref = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit()
        pref.putString("country", country)
        pref.apply()
    }
}