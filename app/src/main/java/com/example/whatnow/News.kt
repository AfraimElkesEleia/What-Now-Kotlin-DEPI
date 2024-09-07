package com.example.whatnow

import com.google.gson.annotations.SerializedName

data class News(val articles:ArrayList<Article>)

data class Article(val title:String,
                   val url:String,
                   @SerializedName("urlToImage")
                   val image:String,
                   val source:Source ,var checked:Boolean = false)


data class Source(val name:String = "")

data class Favorite(val title:String = "",
                    val url:String = "",
                    val image:String = "",
                    val source:Source? = null, var checked:Boolean = true)
