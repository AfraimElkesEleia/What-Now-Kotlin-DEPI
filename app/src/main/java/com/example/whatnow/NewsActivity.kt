package  com.example.whatnow

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityNewsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//bff176c0fbdf435991f92999abf57929

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    lateinit var category : String
    lateinit var auth:FirebaseAuth
    lateinit var country :String
    lateinit var url : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        Log.d("testUID",auth.uid.toString())
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        val pref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        country = pref.getString("country","us")!!
        category = intent.getStringExtra("category")!!
        url = "/v2/top-headlines?country=$country&category=$category&apiKey=4d5e10c67fd647b38aa71d5f78bd39b8"
        loadNews()
        binding.swipeRefresh.setOnRefreshListener { loadNews() }

    }
    private fun loadNews(){
        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val c = retrofit.create(NewsCallable::class.java)
        c.getNews(url).enqueue(object : Callback<News>{
            override fun onResponse(p0: Call<News>, responce: Response<News>) {
                val news = responce.body()
                val articles = news?.articles!!
                articles.removeAll {
                    it.title == "[Removed]"
                }
                showNews(articles)
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false
                Log.d("trace","$articles + $country")

            }

            override fun onFailure(p0: Call<News>, p1: Throwable) {
                Log.d("trace", "Error ${p1.message}")
                binding.progressBar.isVisible = false
                if (p1.message!!.contains("Unable")) {
                    Snackbar.make(
                        binding.root,
                        "No internet connection try again!!",
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                    ).setAction("Retry") {
                        loadNews()
                    }.show()
                }
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }
    private fun showNews(articles: ArrayList<Article>){
        val adapter = NewsAdapter(this,articles,country,auth)
        binding.newsList.adapter =  adapter
    }


}