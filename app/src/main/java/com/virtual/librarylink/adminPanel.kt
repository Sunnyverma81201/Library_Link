package com.virtual.librarylink

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_panel.*
import kotlinx.android.synthetic.main.fragment_crowd.*
import java.util.*

class adminPanel : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        auth = FirebaseAuth.getInstance()


        signOutAdmin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            this.finish()
        }

        addBook.setOnClickListener {
            addBookToDatabase()
        }

        database.collection("crowd").document("general_info").get()
            .addOnSuccessListener {
                current_strength.text = it.getLong("CURRENT").toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Unable to Fetch Content", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onPause() {
        super.onPause()
        auth.signOut()
        finish()
        startActivity(Intent(this,SignInActivity::class.java))
    }

    private fun addBookToDatabase() {
        val databaseReference = database.collection("books")
        var book_id = bookID.text.toString().toUpperCase(Locale.ROOT)
        var book = hashMapOf(
            "book_name" to bookName.text.toString(),
            "author" to bookAuthor.text.toString(),
            "number" to bookNumber.text.toString().toLong(),
            "isIssued" to false,
            "currentIssuer" to ""
        )

        databaseReference.document(book_id).set(book)
            .addOnSuccessListener {
                Toast.makeText(this, "Book added Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Unable to Add the book try again after sometime",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}