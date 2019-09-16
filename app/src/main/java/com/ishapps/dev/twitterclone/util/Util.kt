package com.ishapps.dev.twitterclone.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.request.RequestOptions
import com.ishapps.dev.twitterclone.R
import java.text.DateFormat
import java.util.*

fun getProgressDrawable(context: Context):CircularProgressDrawable{
    return  CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadUrl(url:String?,errorDrawable:Int = R.drawable.empty){
    val requestOptions = RequestOptions().placeholder(getProgressDrawable(context)).error(errorDrawable)
    Glide.with(this).setDefaultRequestOptions(requestOptions).load(url).into(this)
}

fun getDate(timestamp:Long):String{
    timestamp?.let {
        val dateFormat:DateFormat = DateFormat.getDateInstance()
        return dateFormat.format(Date(timestamp))
    }

    return "unknown"

}