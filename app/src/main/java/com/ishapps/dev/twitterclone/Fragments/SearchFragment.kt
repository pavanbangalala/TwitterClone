package com.ishapps.dev.twitterclone.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.adapters.TweetListAdapter
import com.ishapps.dev.twitterclone.listeners.TweetListener
import com.ishapps.dev.twitterclone.util.DATA_TWEETS
import com.ishapps.dev.twitterclone.util.DATA_TWEET_HASHTAGS
import com.ishapps.dev.twitterclone.util.Tweet
import com.ishapps.dev.twitterclone.util.User
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : BaseFragment() {

    private var currentHashTag = ""
    private var tweetsAdaper:TweetListAdapter ?= null
    private var currentUser:User ?= null
    private var firebaseDB = FirebaseFirestore.getInstance()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private var listener  = object :TweetListener{
        override fun onLayoutClick(tweet: Tweet?) {

        }

        override fun onLike(tweet: Tweet) {

        }

        override fun onRetweet(tweet: Tweet) {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tweetsAdaper = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdaper?.setListener(listener)
        rvSearchItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdaper
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        }

        swiperefreshSearch.setOnRefreshListener {
            swiperefreshSearch.isRefreshing = false
            updateList()
        }
    }

    fun newHashTag(term: String) {
        Log.i("SearchFragment term",term)
        currentHashTag = term
        imvFollowHashtag.visibility = View.VISIBLE
        updateList()
    }

    fun updateList(){
        Log.i("SearchFragment term","updating the list")
        rvSearchItems.visibility = View.GONE
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS,currentHashTag).get()
            .addOnSuccessListener { list->
                rvSearchItems.visibility = View.VISIBLE
                var tweets = arrayListOf<Tweet>()
                for(document in list.documents){
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let {
                        tweets.add(tweet)
                    }
                }
                if(!tweets.isNullOrEmpty()) {
                    var sortedTweets = tweets.sortedWith(compareByDescending { it.timeStamp })
                    tweetsAdaper?.updateTweets(sortedTweets)
                }else{
                    Toast.makeText(context,"No results for this search",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                e->e.printStackTrace()
            }
    }


}
