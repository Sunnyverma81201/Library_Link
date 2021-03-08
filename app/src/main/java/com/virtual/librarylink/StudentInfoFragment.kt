package com.virtual.librarylink

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_student_info.*


class StudentInfoFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        getUserInfo()

    }

    override fun onStart() {
        super.onStart()
        logout_button.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this.context,SignInActivity::class.java))
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getUserInfo() {
        val userReference = database.collection("users").document(auth.currentUser?.uid.toString())
        userReference.get().addOnSuccessListener(OnSuccessListener {
            if (it != null){
                user_name.text = it.getString("first_name").toString()+" "+it.getString("last_name").toString()
                user_clg_id.text = it.getString("clg_id").toString()
                user_email.text = it.getString("email").toString()
                user_session.text = "Year: " + it.getString("year").toString()
                user_branch.text = it.getString("branch").toString()
                user_contact.text = it.getString("phone").toString()
            }
        })
            .addOnFailureListener(OnFailureListener {
                auth.signOut()
                Toast.makeText(activity,"Unable to fetch USERDATA try SingIn in again",Toast.LENGTH_LONG).show()
                startActivity(Intent(activity,SignInActivity::class.java))
            })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_info, container, false)
    }
}