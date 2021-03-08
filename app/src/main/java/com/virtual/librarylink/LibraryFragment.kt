package com.virtual.librarylink

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_library.*
import java.util.*
import kotlin.collections.ArrayList


class LibraryFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore

    var bookList: ArrayList<Book> = ArrayList()

    override fun onPause() {
        super.onPause()
        bookList = ArrayList()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)

        loadListViewData()
        val adapter = bookAdapter(this, bookList)
        issuedBooks.adapter = adapter
    }
    override fun onStart() {
        super.onStart()
        issue_book_button.setOnClickListener {
            var bookCode = issue_book_code.text.toString().toUpperCase(Locale.ROOT)
            if (bookCode != "") {
                var books: ArrayList<String>? = arrayListOf<String>()
                database.collection("users").document(auth.currentUser?.uid.toString()).get()
                    .addOnSuccessListener {
                        if (it.get("booksIssued") != null) {
                            books = it.get("booksIssued") as ArrayList<String>
                        } else {
                            books?.add(bookCode)
                        }
                        if (books!!.contains(bookCode)) {
                            Toast.makeText(
                                activity,
                                "Book Already Issued By You",
                                Toast.LENGTH_LONG
                           )
                                .show()
                        } else {
                            tryIssueBook(bookCode)
                            val ft: FragmentTransaction =
                                requireFragmentManager().beginTransaction()
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false)
                            }
                            ft.detach(this).attach(this).commit()
                        }
                    }
            } else{
                Toast.makeText(activity, "Input Book ID First", Toast.LENGTH_SHORT).show()
            }
        }

        loadListViewData()

        refresh_button.setOnClickListener{
            val adapter = bookAdapter(this, bookList)
            issuedBooks.adapter = adapter
        }

    }

    private fun loadListViewData(){
        val userReference =
            database.collection("users").document(auth.currentUser?.uid.toString())
        val booksReference = database.collection("books")


        bookList = ArrayList()

        userReference.get().addOnSuccessListener { it ->
            booksReference.whereEqualTo("currentIssuer", it.getString("clg_id"))
                .get().addOnSuccessListener {
                    for (book in it) {
                        var b = book.data
                        bookList.add(
                            Book(
                                book.id,
                                b["book_name"].toString(),
                                b["author"].toString(),
                                b["number"] as Long
                            )
                        )
                    }
                }
        }

    }
    private fun tryIssueBook(bookCode: String) {
        // database references
        val userReference = database.collection("users").document(auth.currentUser?.uid.toString())
        val bookReference = database.collection("books").document(bookCode.toUpperCase(Locale.ROOT))

        //updating book data
        bookReference.get().addOnSuccessListener {
            if (it.getBoolean("isIssued") == false) {
                userReference.get().addOnSuccessListener {
                    var books = it.get("booksIssued") as ArrayList<String>
                    bookReference.update("currentIssuer", it.get("clg_id").toString())
                    bookReference.update("isIssued", true)
                    books.add(bookCode)
                    userReference.update("booksIssued", books)
                    Toast.makeText(activity, "Book Issued", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity, "Book Already Issued", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_library, container, false)

    }

}