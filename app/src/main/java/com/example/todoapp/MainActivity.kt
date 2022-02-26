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
import com.example.todoapp.utils.dialogYesOrNo
import com.example.todoapp.utils.hide
import com.example.todoapp.utils.hideStatusBar
import com.example.todoapp.utils.show
import com.google.firebase.database.DataSnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var incompleteListAdapter: MainActivityAdapter
    private lateinit var completeListAdapter: MainActivityAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
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
         // todo
         binding.noTodoItems.show()
         binding.tvCompleted.hide()
     }

    private fun initViews() {
        incompleteListAdapter = MainActivityAdapter()
        completeListAdapter = MainActivityAdapter()
        binding.apply {
            fabAddItem.setOnClickListener {
                val intent = Intent(this@MainActivity, ListDetailActivity::class.java)
                startActivity(intent)
            }
            imgLogout.setOnClickListener {
                dialogYesOrNo(
                    getString(R.string.logout),
                    getString(R.string.logout_desc)
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
            binding.noTodoItems.show()
        } else {
            binding.noTodoItems.hide()
        }
    }

    private fun setCompleteItemsData(completedList: List<StoredTodo>) {
        completeListAdapter.items = completedList
        if (completedList.isNullOrEmpty()) {
            binding.tvCompleted.hide()
        } else {
            binding.tvCompleted.show()
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