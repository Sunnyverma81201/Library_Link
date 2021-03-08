package com.virtual.librarylink

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_library.*
import java.util.*
import kotlin.collections.ArrayList

class bookAdapter(private val context: LibraryFragment, private val dataSource: ArrayList<Book>) : BaseAdapter() {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private val inflater: LayoutInflater = context.layoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.book, parent, false)
        val bookNameView = rowView.findViewById(R.id.book_name) as TextView
        val bookCodeView = rowView.findViewById(R.id.book_code) as TextView
        val bookAuthorView = rowView.findViewById(R.id.book_author) as TextView
        val bookReturnButton = rowView.findViewById(R.id.return_book_button) as Button

        val book = getItem(position) as Book

        auth = FirebaseAuth.getInstance()

        val userReference = database.collection("users").document(auth.currentUser?.uid.toString())
        val bookReference = database.collection("books").document(book.bookCode)
        bookNameView.text = book.bookName
        bookAuthorView.text = book.bookAuthor
        bookCodeView.text = book.bookNumber.toString()

        bookReturnButton.setOnClickListener {
            var bookCode = book.bookCode

            userReference.get()
                .addOnSuccessListener {
                    var books = it.get("booksIssued") as ArrayList<String>
                    if (books.size > 1) {
                        books.remove(bookCode)
                        userReference.update("booksIssued", books)
                    }
                    else{
                        userReference.update("booksIssued", arrayListOf<String>())
                    }
                    bookReference.update("currentIssuer", "")
                    bookReference.update("isIssued", false)
                }
        }
        return rowView
    }

}