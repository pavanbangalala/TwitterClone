package com.ishapps.dev.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ishapps.dev.twitterclone.R
import com.ishapps.dev.twitterclone.util.DATA_USERS
import com.ishapps.dev.twitterclone.util.User

import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseFirestore.getInstance()
    val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        var  user = it.currentUser?.uid
        user?.let{
            linear_pb_container_signup.visibility = View.GONE
            Toast.makeText(this@SignUpActivity,"Registration succesful",Toast.LENGTH_SHORT).show()
            startActivity(HomeActivity.newIntent(this@SignUpActivity))
            finish()
        }
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setTextChangeListener(etUserName,usernameTIL)
        setTextChangeListener(etEmail,emailTIL)
        setTextChangeListener(etPassword,passwordTIL)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }


    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onClickLogin(view: View){
        startActivity(newIntent(this@SignUpActivity))
        finish()
    }

    fun onClickSignUp(view: View){
        var proceed = true

        if(etUserName.text.isNullOrEmpty()){
            usernameTIL.error = "invalid user name"
            usernameTIL.isErrorEnabled = true
            proceed = false
        }

        if(etEmail.text.isNullOrEmpty()){
            emailTIL.error = "invalid email address"
            emailTIL.isErrorEnabled = true
            proceed = false
        }

        if(etPassword.text.isNullOrEmpty()){
            passwordTIL.error = "invalid password"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }

        if(proceed){
            linear_pb_container_signup.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(etEmail.text.toString(),etPassword.text.toString()).addOnCompleteListener {
                if(!it.isSuccessful){

                    Toast.makeText(this@SignUpActivity,"sign up error: "+it.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                }else{
                    val username = etUserName.text.toString()
                    val email = etEmail.text.toString()
                    val user = User(email,username,"", arrayListOf(), arrayListOf())
                    firebaseDatabase.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)

                }
                linear_pb_container_signup.visibility = View.GONE

            }.addOnFailureListener {
                linear_pb_container_signup.visibility = View.GONE
                it.printStackTrace()
            }
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
}
