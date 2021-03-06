package com.example.todoapp.helper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.models.StoredTodo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseRealtimeDBHelper {

    private var database: DatabaseReference
    private val allItemsMutableLiveData = MutableLiveData<DataSnapshot?>()
    private var TAG = FirebaseRealtimeDBHelper::class.java.simpleName
    private var DB_URL = "https://todo-app-groww-assignment-default-rtdb.asia-southeast1.firebasedatabase.app/"

    init {
        // check database connection
        connectToRealtimeDatabase()

        database = Firebase.database(DB_URL).reference.child("todos")
    }

    fun fetchData(uid: String): MutableLiveData<DataSnapshot?> {
        var returnList: DataSnapshot?
        initUserKey(uid)
        database.child(uid).orderByChild("updatedAt").get()
            .addOnSuccessListener {
                returnList = it
                allItemsMutableLiveData.postValue(returnList)
            }
            .addOnFailureListener {
                Log.e(TAG, "unable to connect to database, ERROR MSG = ${it.message}")
            }
        return allItemsMutableLiveData
    }

    /**
     * Change status of existing To-Do item
     */
    fun changeItemStatus(uid: String, itemData: StoredTodo, status: Boolean) {
        database.child(uid).child(itemData.id!!).child("status").setValue(status)
        fetchData(uid)
    }

    /**
     * add new To-Do item to database
     */
    fun addItem(uid: String, itemData: StoredTodo): String {
        val newEntryRef = database.child(uid).push()
        itemData.id = newEntryRef.key!!
        newEntryRef.setValue(itemData)
        return itemData.id!!
    }

    /**
     * update existing to-do item
     */
    fun editItem(uid: String, itemData: StoredTodo) {
        database.child(uid).child(itemData.id!!).setValue(itemData)
    }

    /**
     * delete existing to-do item
     */
    fun deleteItem(uid: String, itemData: StoredTodo) {
        database.child(uid).child(itemData.id!!).removeValue()
        fetchData(uid)
    }

    /**
     * if uid does not exist in database,
     * create one with temp initialization
     */
    private fun initUserKey(uid : String) {
        database.child(uid).get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    database.child(uid).setValue(null)
                }
            }.addOnFailureListener {
                Log.e(TAG, "Failed to initialize user key, error = ${it.message}")
            }
    }

    private fun connectToRealtimeDatabase() {
        val connectedRef = Firebase.database(DB_URL).getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d(TAG, "Database connected")
                } else {
                    Log.d(TAG, "Database not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Listener was cancelled, error = ${error.message}")
            }
        })
    }
}