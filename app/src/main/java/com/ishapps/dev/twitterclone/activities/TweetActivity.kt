package com.ishapps.dev.twitterclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.util.*

import kotlinx.android.synthetic.main.activity_tweet.*

class TweetActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageUri:Uri?=null
    private var userId:String?=null
    private var userName:String?=null
    private var tweetId:DocumentReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)
        if(intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME)){
            userId = intent.getStringExtra(PARAM_USER_ID)
            userName = intent.getStringExtra(PARAM_USER_NAME)
        }else{
            Toast.makeText(this@TweetActivity,"Error creating new tweet ...",Toast.LENGTH_SHORT).show()
        }
        linear_pb_container_tweet.setOnTouchListener { v, event ->  true}
    }

    fun addImage(view: View){
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,445)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 445 && resultCode == Activity.RESULT_OK){

            var image = data?.data
            image?.let {
                imageUri = it
                imvTweet.loadUrl(imageUri.toString(),R.drawable.logo)
            }
        }
    }



    fun sendTweet(view:View){
        linear_pb_container_tweet.visibility = View.VISIBLE
        val text = etTweetText.text.toString()
        tweetId = firebaseDB.collection(DATA_TWEETS).document()
        Log.i("uploadImage",imageUri.toString())
        var filePath = firebaseStorage.child(DATA_TWEET_IMAGES).child(userId!!)
        filePath.putFile(imageUri!!).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener {
                imageUri = it
                val hashtags = getHashTags(text)
                val tweet= Tweet(tweetId?.id, arrayListOf(userId!!),userName,text,imageUri?.toString(),System.currentTimeMillis(),hashtags)
                Log.i("uploadImage image uri",imageUri.toString())
                Log.i("uploadImage tweet id",tweetId.toString())
                Log.i("uploadImage tweet",tweet.toString())
                tweetId!!.set(tweet).addOnCompleteListener {  finish()}.addOnFailureListener {
                        e->e.printStackTrace()
                    linear_pb_container_tweet.visibility = View.GONE
                    Toast.makeText(this@TweetActivity,"failed to post tweet ...",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                linear_pb_container_tweet.visibility = View.GONE
                Toast.makeText(this@TweetActivity," failed to send the tweet ...",Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener{
            linear_pb_container_tweet.visibility = View.GONE
            Toast.makeText(this@TweetActivity,"failed to send the tweet...",Toast.LENGTH_SHORT).show()
        }


    }

    fun getHashTags(source:String):ArrayList<String>{
        val hashtags = arrayListOf<String>()
        var hashtag:String?=null
        var text = source
        while(text.contains("#")){
            var hashIndex = text.indexOf("#")
            text = text.substring(hashIndex+1)

            var nextHash = text.indexOf("#")
            var nextSpace = text.indexOf(" ")

            if(nextHash == -1 && nextSpace == -1){
                hashtag = text.substring(0)
            }else if(nextHash != -1 && nextHash < nextSpace){
                hashtag = text.substring(0,nextHash)
                text = text.substring(nextHash+1)
            }else{
                hashtag = text.substring(0,nextSpace)
                text = text.substring(nextSpace+1)
            }

            if(!hashtag.isNullOrEmpty()){
                hashtags.add(hashtag)
            }
        }
        return hashtags
    }

    companion object{
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_NAME = "UserName"
        fun newIntent(context: Context,userId:String?, userName:String?):Intent {

            var intent  = Intent(context,TweetActivity::class.java)
            intent.putExtra(PARAM_USER_ID,userId)
            intent.putExtra(PARAM_USER_NAME,userName)
            return intent
        }
    }

}
