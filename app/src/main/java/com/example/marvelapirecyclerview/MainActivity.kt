package com.example.marvelapirecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var characterAdapter: characterRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvCharacters: RecyclerView = findViewById(R.id.characterView)
        characterAdapter = characterRecyclerViewAdapter(mutableListOf())
        rvCharacters.adapter = characterAdapter
        rvCharacters.layoutManager = LinearLayoutManager(this)
        // makes it more even as decoration
        rvCharacters.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

        getRandomUser()
    }
    private fun getRandomUser () {
        var client = AsyncHttpClient()
        val timestamp = System.currentTimeMillis()
        val privateKey = "b293dbe9ded41539baf44fd39f4c0fc5b9ee36f0"
        val publicKey = "069f1177d9bf5d414262395785c7f3a2"
        val hash = stringToMd5("$timestamp$privateKey$publicKey")
        // first url I tried "https://gateway.marvel.com/v1/public/characters?ts=$timestamp&apikey=$publicKey&hash=$hash"
        // returns 20 values
        val url =
            "https://gateway.marvel.com/v1/public/characters?ts=$timestamp&apikey=$publicKey&hash=$hash" + "&limit=100&offset=100"
        client[url, object : JsonHttpResponseHandler() {
            // Request results
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String,
                throwable: Throwable?
            ) {
                Log.d("TAG", response)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i("Tag", json.toString())
                var resultsJSON = json.jsonObject.getString("attributionText")

                val dataObject = json.jsonObject.getJSONObject("data")
                val resultsArray = dataObject.getJSONArray("results")

                for (i in 0 until resultsArray.length()) {
                    val characterObject = resultsArray.getJSONObject(i)
                    val name = characterObject.getString("name")
                    val description = if (characterObject.getString("description").isNotEmpty())
                        characterObject.getString("description")
                    else
                        "No description available"



                    //Log.i("CharacterInfo", "Name: $name, Description: $description, Thumbnail: $thumbnailUrl")
                    val characters = parseJsonResponse(json)
                    characterAdapter.setCharacterList(characters)
                }

            }
        }]
    } // getRandomUser

    private fun parseJsonResponse(json: JsonHttpResponseHandler.JSON): List<Character> {
        val characters = mutableListOf<Character>()

        val dataObject = json.jsonObject.getJSONObject("data")
        val resultsArray = dataObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val characterObject = resultsArray.getJSONObject(i)
            val name = characterObject.getString("name")
            val description = if (characterObject.getString("description").isNotEmpty())
                characterObject.getString("description")
            else
                "No description available"

            // Constructing the thumbnail URL
            val thumbnailObject = characterObject.getJSONObject("thumbnail")
            val thumbnailPath = thumbnailObject.getString("path")
            val thumbnailExtension = thumbnailObject.getString("extension")
            val thumbnailUrl = "$thumbnailPath.$thumbnailExtension"
            Log.d("URLCHECKING:", thumbnailUrl)
            characters.add(Character(name, description, thumbnailUrl))
        }
        return characters
    }
    fun stringToMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(input.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashText = no.toString(16)
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }
        return hashText
    }
}
