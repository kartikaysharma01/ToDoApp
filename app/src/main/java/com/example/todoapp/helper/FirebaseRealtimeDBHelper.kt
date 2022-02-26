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

    var database: DatabaseReference
    val allItemsMutableLiveData = MutableLiveData<DataSnapshot?>()
    private var TAG = FirebaseRealtimeDBHelper::class.java.simpleName

    init {
        // check database connection
        connectToRealtimeDatabase()

        database = Firebase.database(
            "https://todo-app-groww-assignment-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).reference.child("todos")
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
                // todo; add a snack, unable to connect to database . please check ur internet conection
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
                Log.d(TAG, "Failed to initialize user key, error = ${it.message}")
                // todo; add a snack, unable to intialise userkey in database
            }
    }

    private fun connectToRealtimeDatabase() {
        val connectedRef = Firebase.database("https://todo-app-groww-assignment-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(".info/connected")
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