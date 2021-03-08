package com.virtual.librarylink

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.ArrayList

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private val years = arrayOf(1,2,3,4)
    var year = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        auth = FirebaseAuth.getInstance()

        back_to_signin_button.setOnClickListener {
            val intent: Intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        session_start.onItemSelectedListener = this

        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,years)

        session_start.adapter = arrayAdapter

        registerUser()
    }

    private fun registerUser() {
        register_button.setOnClickListener {
            when {
                TextUtils.isEmpty(firstname.text.toString()) -> {
                    firstname.error = "Please enter First Name"
                }
                TextUtils.isEmpty(lastname.text.toString()) -> {
                    lastname.error = "Please enter Last Name"
                }
                TextUtils.isEmpty(collage_id.text.toString()) -> {
                    collage_id.error = "Please enter Collage ID"
                }
                TextUtils.isEmpty(email.text.toString()) -> {
                    email.error = "Please enter email"
                }
                TextUtils.isEmpty(phone.text.toString()) -> {
                    phone.error = "Please enter Phone Number"
                }
            }
            auth.createUserWithEmailAndPassword(email.text.toString(), pswd.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = auth.currentUser
                        createUser()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Registration Unsuccessful, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun createUser(){

        val user = hashMapOf(
            "first_name" to firstname.text.toString(),
            "last_name" to lastname.text.toString(),
            "clg_id" to collage_id.text.toString(),
            "email" to email.text.toString(),
            "phone" to phone.text.toString(),
            "branch" to branch.text.toString(),
            "year" to year,
            "inLibrary" to false,
            "booksIssued" to arrayListOf<String>()
        )
        database.collection("users")
            .document(auth.currentUser?.uid.toString())
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Registration Unsuccessful, please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        year = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"Year not Selected",Toast.LENGTH_LONG).show()
    }
}