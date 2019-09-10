package com.ishapps.dev.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.ishapps.dev.twitterclone.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        Log.i("LoginActivity listener","checking for user id")
        val user = it.currentUser?.uid
        user?.let{
            startActivity(HomeActivity.newIntent(this@LoginActivity))
            finish()
        }
    }

    fun setTextChangeListener(et:EditText, til:TextInputLayout){
        et.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //FirebaseApp.initializeApp(applicationContext)
        setTextChangeListener(etEmail,emailTIL)
        setTextChangeListener(etPassword,passwordTIL)
    }


    fun onClickLogin(view: View){

        var proceed = true
        if(etEmail.text.isNullOrEmpty()){
            emailTIL.error = "email is required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }

        if(etPassword.text.isNullOrEmpty()){
            passwordTIL.error = "password is required"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }

        if(proceed){
            linear_pb_container.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(etEmail.text.toString(),etPassword.text.toString())
                .addOnCompleteListener {
                    if(!it.isSuccessful){
                        linear_pb_container.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,"login error : "+it.exception!!.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {e->
                    linear_pb_container.visibility = View.GONE
                    e.printStackTrace()
                }
        }
    }

    fun onClickSignUp(view: View){
        startActivity(SignUpActivity.newIntent(this@LoginActivity))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)

    }
}
