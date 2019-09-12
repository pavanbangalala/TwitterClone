package com.ishapps.dev.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ishapps.dev.twitterclone.Fragments.HomeFragment
import com.ishapps.dev.twitterclone.Fragments.MyActivityFragment
import com.ishapps.dev.twitterclone.Fragments.SearchFragment
import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.util.DATA_USERS
import com.ishapps.dev.twitterclone.util.User
import com.ishapps.dev.twitterclone.util.loadUrl
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val myActivityFragment = MyActivityFragment()
    private var sectionsPagerAdapter:SectionPagerAdapter?= null
    var firebaseDatabase = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sectionsPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        viewpager.adapter = sectionsPagerAdapter
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager))
        tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {

            }

        })
        fab.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            firebaseDatabase.collection(DATA_USERS).document(userId!!).get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                startActivity( TweetActivity.newIntent(this@HomeActivity,userId,user?.username))
            }

        }
        linear_pb_container_home.setOnTouchListener { v, event -> true }
    }

    override fun onResume() {
        super.onResume()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null){
            startActivity(LoginActivity.newIntent(this@HomeActivity))
            finish()
        }else{
            firebaseDatabase.collection(DATA_USERS).document(userId).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    logo.loadUrl(user?.imageUrl,R.drawable.logo)
                }.addOnFailureListener{
                   Toast.makeText(this@HomeActivity,"could not load profile image",Toast.LENGTH_SHORT).show()
                }
        }
    }

    inner class SectionPagerAdapter(fm:FragmentManager):FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            return when(position){
                0-> homeFragment
                1->searchFragment
                2->myActivityFragment
                else->homeFragment
            }
        }

        override fun getCount() = 3

    }

    companion object{
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    fun onClickSignOut(view: View){
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    fun gotoProfileActivity(view: View){
        startActivity(ProfileActivity.newIntent(this@HomeActivity))

    }
}
