package com.virtual.librarylink

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_crowd.*


class CrowdFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    var current = 0
    var max = 0
    var remain = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        getCrowdInfo()
        var isInLibrary = false
        remain = max - current


        val userReference = database.collection("users").document(auth.currentUser?.uid.toString())
        userReference.get().addOnSuccessListener {
            if (it.getBoolean("inLibrary") == true) {
                add_slot.isVisible = false
                finish_session.isVisible = true
            } else {
                add_slot.isVisible = true
                finish_session.isVisible = false
            }
            add_slot.setOnClickListener {
                //checking if slot is available
                if (current < max) {
                    //update the info on server
                    current++
                    remain--
                    updateData(true)

                    //update the info on clint
                    getCrowdInfo()

                    // toggle button
                    add_slot.isVisible = false
                    finish_session.isVisible = true
                }
            }
            finish_session.setOnClickListener {
                //update the info on server
                current--
                remain++
                updateData(false)

                //update the info on clint
                getCrowdInfo()

                // toggle button
                add_slot.isVisible = true
                finish_session.isVisible = false
            }
        }
    }

    private fun updateData(flag: Boolean) {
        val crowdInfo = database.collection("crowd").document("general_info")
        val userRef = database.collection("users").document(auth.currentUser?.uid.toString())

        userRef.update("inLibrary", flag)
        crowdInfo.update("CURRENT", current)
        crowdInfo.update("REMAINING", remain)
    }

    @SuppressLint("SetTextI18n")
    private fun getCrowdInfo() {
        val crowdInfo = database.collection("crowd").document("general_info")

        crowdInfo.get().addOnSuccessListener {
            //retrieving data from the server
            currentLibraryCount.text = it.getLong("CURRENT").toString()
            max_slots.text = it.getLong("MAX").toString()
            remaining_slots.text = it.getLong("REMAINING").toString()
            //updating the variables
            current = currentLibraryCount.text.toString().toInt()
            max = max_slots.text.toString().toInt()
            remain = remaining_slots.text.toString().toInt()
        }
            .addOnFailureListener {
                Toast.makeText(activity, "Unable to fetch data", Toast.LENGTH_LONG).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crowd, container, false)
    }
}

