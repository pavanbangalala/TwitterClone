package com.ishapps.dev.twitterclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.listeners.TweetListener
import com.ishapps.dev.twitterclone.util.Tweet
import com.ishapps.dev.twitterclone.util.getDate
import com.ishapps.dev.twitterclone.util.loadUrl

class TweetListAdapter(val userId:String,val tweets:ArrayList<Tweet>):RecyclerView.Adapter<TweetListAdapter.TweetViewHolder>() {
    private var tweetListener:TweetListener?= null

    fun setListener(listener:TweetListener){
        this.tweetListener = listener
    }

    fun updateTweets(tweetsList:List<Tweet>){
        tweets.clear()
        tweets.addAll(tweetsList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  TweetViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_tweeet,parent,false)
    )

    override fun getItemCount() = tweets.size

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.bind(userId,tweets[position],tweetListener)
    }

    class TweetViewHolder(var view: View):RecyclerView.ViewHolder(view){

        private val layout = view.findViewById<ViewGroup>(R.id.tweet_layout)
        private val username = view.findViewById<TextView>(R.id.tvUserName)
        private val text = view.findViewById<TextView>(R.id.tvTweet)
        private val image = view.findViewById<ImageView>(R.id.imvTweet)
        private val date = view.findViewById<TextView>(R.id.tvDate)
        private val like = view.findViewById<ImageView>(R.id.imvTweetLike)
        private val likeCount = view.findViewById<TextView>(R.id.tvTweetLikeCount)
        private val retweet = view.findViewById<ImageView>(R.id.imvTweetRetweet)
        private val retweetCount = view.findViewById<TextView>(R.id.tvTweetRetweetCount)



        fun bind(userId:String,tweet:Tweet,listener: TweetListener?){
            username.text  = tweet.username
            text.text = tweet.text
            if(tweet.imageUrl.isNullOrEmpty()){
                image.visibility = View.GONE
            }else{
                image.visibility = View.VISIBLE
                image.loadUrl(tweet.imageUrl)
            }
            date.text = getDate(tweet.timeStamp!!)
            likeCount.text = tweet.likes?.size.toString()
            retweetCount.text = ((tweet.userIds.size)-1).toString()

            layout.setOnClickListener{listener?.onLayoutClick(tweet)}
            like.setOnClickListener { listener?.onLike(tweet) }
            retweet.setOnClickListener { listener?.onRetweet(tweet) }

            if(tweet.likes?.contains(userId) == true){
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like))
            }else{
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like_inactive))
            }

            if(!tweet.retweets.isNullOrEmpty()){
                if(tweet.retweets?.get(0) == userId){
                    retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.original))
                    retweet.isClickable = false
                }else if(tweet.retweets?.contains(userId) == true){
                    retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.retweet))
                }
                else{
                    retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.retweet_inactive))
                }
            }else{
                retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.retweet_inactive))
            }


        }
    }
}