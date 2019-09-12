package com.ishapps.dev.twitterclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.util.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseDatabase = FirebaseFirestore.getInstance()
    val firebaseStorage = FirebaseStorage.getInstance().reference
    var imageUrl:String ?= null
    companion object{
        fun newIntent(context: Context) = Intent(context,ProfileActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null){
            finish()
        }

        linear_pb_container_profile.setOnTouchListener { v, event -> true }
        photoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,345)
        }
        populateInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 345){
            storeImage(data?.data)
        }
    }

    fun storeImage(imageUri:Uri?){
        imageUri?.let{
            Toast.makeText(this@ProfileActivity,"uploading ... ",Toast.LENGTH_SHORT).show()
            linear_pb_container_profile.visibility  = View.VISIBLE
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            var filePath:StorageReference =  firebaseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageUri).addOnSuccessListener {
                filePath.downloadUrl
                    .addOnSuccessListener {
                        val url:String = it.toString()
                        firebaseDatabase.collection(DATA_USERS).document(userId).update(DATA_USER_IMAGE_URL,url)
                            .addOnSuccessListener {
                                imageUrl = url
                                photoIV.loadUrl(imageUrl,R.drawable.logo)
                                linear_pb_container_profile.visibility = View.GONE
                            }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this@ProfileActivity,"upload image failed ...",Toast.LENGTH_SHORT).show()
                        linear_pb_container_profile.visibility = View.GONE
                    }

            }.addOnFailureListener{
                Toast.makeText(this@ProfileActivity,"upload image failed ...",Toast.LENGTH_SHORT).show()
                linear_pb_container_profile.visibility = View.GONE
            }
        }
    }



    fun populateInfo(){
        linear_pb_container_profile.visibility = View.VISIBLE
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        firebaseDatabase.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                Log.i("ProfileActivity","user  : "+user)
                usernameET.setText(user?.username,TextView.BufferType.EDITABLE)
                emailET.setText(user?.email,TextView.BufferType.EDITABLE)
                linear_pb_container_profile.visibility = View.GONE
                imageUrl = user?.imageUrl
                imageUrl?.let{
                    photoIV.loadUrl(user?.imageUrl,R.drawable.logo)
                }
            }
            .addOnFailureListener {
                e->e.printStackTrace()
                finish()
            }
    }

    fun onApply(view: View){
        linear_pb_container_profile.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userName = usernameET.text.toString()
        val email = emailET.text.toString()
        var userMap = HashMap<String,Any>()
        userMap[DATA_USER_USERNAME] = userName
        userMap[DATA_USER_EMAIL] = email
        firebaseDatabase.collection(DATA_USERS).document(uid!!).update(userMap).addOnSuccessListener {
            Toast.makeText(this@ProfileActivity,"update successful",Toast.LENGTH_SHORT).show()
            finish()

        }
            .addOnFailureListener {
                e->e.printStackTrace()
                linear_pb_container_profile.visibility = View.GONE
                Toast.makeText(this@ProfileActivity,"update failed",Toast.LENGTH_SHORT).show()
            }
    }

    fun onSignOut(view: View){
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}
