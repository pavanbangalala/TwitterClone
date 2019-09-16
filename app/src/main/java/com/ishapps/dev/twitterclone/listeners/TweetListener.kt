package com.ishapps.dev.twitterclone.listeners

import com.ishapps.dev.twitterclone.util.Tweet

interface TweetListener {

    fun onLayoutClick(tweet: Tweet?)
    fun onLike(tweet:Tweet)
    fun onRetweet(tweet:Tweet)
}