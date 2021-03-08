package com.virtual.librarylink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        fragmentInit()
        loadProfile()
    }

    private fun fragmentInit() {
        val libraryFragment:Fragment = LibraryFragment()
        val crowdFragment:Fragment = CrowdFragment()
        val studentInfoFragment:Fragment = StudentInfoFragment()

       setCurrentFragment(libraryFragment)
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.library_work->setCurrentFragment(libraryFragment)
                R.id.crowd->setCurrentFragment(crowdFragment)
                R.id.student_info->setCurrentFragment(studentInfoFragment)
            }
            true
    }
    }

    fun loadProfile() {
        val user = auth.currentUser
        val userReference = database.collection("users").document(user?.uid.toString())

        userReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                } else {
                    Toast.makeText(this, "Unable to find the information", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_container,fragment)
            commit()
        }

    }