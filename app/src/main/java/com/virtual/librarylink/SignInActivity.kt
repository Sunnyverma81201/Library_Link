package com.virtual.librarylink

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signin.*
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        //for transparent statusbar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        auth = FirebaseAuth.getInstance()

        signup_button.setOnClickListener{
            val intent: Intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        val currentuser = auth.currentUser
        if (currentuser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        forgot_password.setOnClickListener {
            auth.sendPasswordResetEmail(user_id.text.toString())
            Toast.makeText(this,"Password reset link sent on Above email",Toast.LENGTH_LONG).show()
        }

        signInUser()

    }
    private fun signInUser(){
        login_button.setOnClickListener {
            when {
                TextUtils.isEmpty(user_id.text.toString()) -> {
                    user_id.error = "Please enter email"
                }
                TextUtils.isEmpty(password.text.toString()) -> {
                    password.error = "Please enter password"
                }
            }
            if (user_id.text.toString() != "sunny.verma81201@gmail.com"){
                auth.signInWithEmailAndPassword(user_id.text.toString(), password.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed, try again.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            } else{
                auth.signInWithEmailAndPassword(user_id.text.toString(), password.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, adminPanel::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed, try again.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }


        }
    }
}