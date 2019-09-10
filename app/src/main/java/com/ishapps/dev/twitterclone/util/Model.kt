package com.ishapps.dev.twitterclone.util

data class User(val email:String?= "", val username: String?= "", val imageUrl:String?= "", val followHashTags:ArrayList<String>?= arrayListOf(), val followUsers:ArrayList<String> ?= arrayListOf())