package com.example.todoapp

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.helper.FirebaseAuthenticationHelper.getCurrentUserUid
import com.example.todoapp.helper.FirebaseAuthenticationHelper.isLoggedIn
import com.example.todoapp.helper.FirebaseAuthenticationHelper.signOut
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.fetchData
import com.example.todoapp.models.StoredTodo
import com.google.firebase.database.DataSnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var incompleteListAdapter: MainActivityAdapter
    private lateinit var completeListAdapter: MainActivityAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (!isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        initViews()
    }

    override fun onResume() {
        super.onResume()
        fetchData(getCurrentUserUid()).observe(this) { data ->
            if (data == null){
                setNoDataLayout()
            } else {
                setIncompleteItemsData(getIncompleteItemsData(data))
                setCompleteItemsData(getCompleteItemsData(data))
            }
        }
    }

     private fun setNoDataLayout() {
         binding.noTodoItems.visibility = View.VISIBLE
         binding.tvCompleted.visibility = View.GONE
     }

    fun dialogYesOrNo(
        activity: Activity,
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
            listener.onClick(dialog, id)
        })
        builder.setNegativeButton("No", null)
        val alert = builder.create()
        alert.setTitle(title)
        alert.setMessage(message)
        alert.show()
    }

    private fun initViews() {
        incompleteListAdapter = MainActivityAdapter()
        completeListAdapter = MainActivityAdapter()
        binding.apply {
            fabAddItem.setOnClickListener {
                Log.d("testing1","fab clicked")
                val intent = Intent(this@MainActivity, ListDetailActivity::class.java)
                startActivity(intent)
            }
            imgLogout.setOnClickListener {
                dialogYesOrNo(
                    this@MainActivity,
                    "Logout?",
                    "Do you want to logout of your account?"
                ) { _, _ ->
                    signOut(this@MainActivity)
                }
            }
            rvCompleteToDoList.adapter = completeListAdapter
            rvIncompleteToDoList.adapter = incompleteListAdapter
        }
    }

    private fun setIncompleteItemsData(notCompletedList: List<StoredTodo>) {
        incompleteListAdapter.items = notCompletedList
        if (notCompletedList.isNullOrEmpty()) {
            binding.noTodoItems.visibility = View.VISIBLE
        } else {
            binding.noTodoItems.visibility = View.GONE
        }
    }

    private fun setCompleteItemsData(completedList: List<StoredTodo>) {
        completeListAdapter.items = completedList
        if (completedList.isNullOrEmpty()) {
            binding.tvCompleted.visibility = View.GONE
        } else {
            binding.tvCompleted.visibility = View.VISIBLE
        }
    }

    private fun getIncompleteItemsData(allDataList: DataSnapshot) : List<StoredTodo> {
        val notCompletedList = mutableListOf<StoredTodo>()
        for (child in allDataList.children) {
            val todo2 = child.getValue(StoredTodo::class.java) ?: continue
            todo2.id = child.key.toString()
            if (!todo2.status) {
                notCompletedList.add(todo2)
            }
        }
        notCompletedList.reverse()
        return notCompletedList
    }

    private fun getCompleteItemsData(allDataList: DataSnapshot) : List<StoredTodo> {
        val completedList = mutableListOf<StoredTodo>()
        for (child in allDataList.children) {
            val todo2 = child.getValue(StoredTodo::class.java) ?: continue
            todo2.id = child.key.toString()
            if (todo2.status) {
                completedList.add(todo2)
            }
        }
        completedList.reverse()
        return completedList
    }

}