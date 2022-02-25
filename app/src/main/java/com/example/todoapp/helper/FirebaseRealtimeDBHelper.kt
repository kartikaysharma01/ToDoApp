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

    init {
        // check database connection
        connectToRealtimeDatabase()

        database = Firebase.database(
            "https://todo-app-groww-assignment-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).reference.child("todos")
    }

    fun fetchData(uid: String): MutableLiveData<DataSnapshot?> {
        Log.d("testing", "ln 30 FirebaseRealtimeDBHelper $uid ")
        var returnList: DataSnapshot? = null
        initUserKey(uid)
        database.child(uid).orderByChild("updatedAt").get()
            .addOnSuccessListener {
                Log.e("testing", "ln 30 FirebaseRealtimeDBHelper $uid ")
                returnList = it
                allItemsMutableLiveData.postValue(returnList)
            }
            .addOnFailureListener {
                Log.e("testing", "ln 33̉ FirebaseRealtimeDBHelper $uid ")
            }
        return allItemsMutableLiveData
    }

    fun changeItemStatus(uid: String, itemData: StoredTodo, status: Boolean) {
        database.child(uid).child(itemData.id!!).child("status").setValue(status)
        fetchData(uid)
    }

    fun addItem(uid: String, itemData: StoredTodo): String {
        val newEntryRef = database.child(uid).push()
        itemData.id = newEntryRef.key!!
        newEntryRef.setValue(itemData)
        return itemData.id!!
    }

    fun editItem(uid: String, itemData: StoredTodo) {
        database.child(uid).child(itemData.id!!).setValue(itemData)
    }

    fun deleteItem(uid: String, itemData: StoredTodo) {
        database.child(uid).child(itemData.id!!).removeValue()
        fetchData(uid)
    }

    private fun initUserKey(uid : String) {
        Log.e("testing", "ln 50 FirebaseRealtimeDBHelper $uid ")
        database.child(uid).get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    database.child(uid).setValue("Temp")
                }
            }.addOnFailureListener {
                Log.e("testing", "ln 53 FirebaseRealtimeDBHelper $uid ${it.message}")
            }
        Log.e("testing", "ln 58 FirebaseRealtimeDBHelper $uid ")
    }

    private fun connectToRealtimeDatabase() {
        val connectedRef = Firebase.database("https://todo-app-groww-assignment-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("testing", "connected")
                } else {
                    Log.d("testing", "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("testing", "Listener was cancelled")
            }
        })
    }
}