package com.ishapps.dev.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.ishapps.dev.twitterclone.Fragments.HomeFragment
import com.ishapps.dev.twitterclone.Fragments.ProfileFragment
import com.ishapps.dev.twitterclone.Fragments.SearchFragment
import com.ishapps.dev.twitterclone.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()
    private var sectionsPagerAdapter:SectionPagerAdapter?= null


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
    }

    override fun onResume() {
        super.onResume()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null){
            startActivity(LoginActivity.newIntent(this@HomeActivity))
            finish()
        }
    }

    inner class SectionPagerAdapter(fm:FragmentManager):FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            return when(position){
                0-> homeFragment
                1->searchFragment
                2->profileFragment
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
}
