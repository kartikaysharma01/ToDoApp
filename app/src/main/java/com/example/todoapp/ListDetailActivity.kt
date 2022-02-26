package com.example.todoapp

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.example.todoapp.databinding.ActivityListDetailBinding
import com.example.todoapp.helper.FirebaseAuthenticationHelper.getCurrentUserUid
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.addItem
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.editItem
import com.example.todoapp.models.StoredTodo
import com.example.todoapp.utils.*
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityListDetailBinding
    private var itemData : StoredTodo? = null
    private var title: String? = null
    private var desc: String? = null
    private var status: Boolean = false
    private var createdAt: String? = null
    private var updatedAt: String? = null
    private var id: String? = null
    private var isItemChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_list_detail
        )
        itemData = intent.getParcelableExtra("item_data") as StoredTodo?

        initViews()
    }

    private fun setData() {
        binding.etItemTitle.setText(itemData!!.title)
        binding.etItemDesc.setText(itemData!!.desc)
        binding.tvCreated.text = dateParser(itemData!!.createdAt)
        binding.tvLastModified.text = dateParser(itemData!!.updatedAt)
        binding.llDates.show()
        checkSaveEnabled(true)
    }

    override fun onBackPressed() {
        if (isItemChanged) {
            dialogYesOrNo(
                getString(R.string.discard_changes),
                getString(R.string.discard_desc)
            ) { _, _ ->
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initViews() {
        if (itemData != null) {
            title = itemData!!.title
            createdAt = itemData!!.createdAt
            status = itemData!!.status
            desc = itemData!!.desc
            id = itemData!!.id
            setData()
        }

        binding.imgBack.setOnClickListener {
            hideKeyboard()
            onBackPressed()
        }

        binding.imgSave.setOnClickListener {
            hideKeyboard()
            if (it.alpha == 0.5f) {
                if (title.isNullOrBlank())
                    snack(binding.root, getString(R.string.title_can_not_be_empty), false)
                else
                    snack(binding.root, getString(R.string.no_changes), false)
            } else {
                saveItemToDB()
            }
        }

        binding.etItemTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                title = s.toString()
                checkSaveEnabled()
                isItemChanged = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // to nothing
            }
        })

        binding.etItemDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                desc = s.toString()
                checkSaveEnabled()
                isItemChanged = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // to nothing
            }
        })
    }

    private fun checkSaveEnabled(disableButton: Boolean = false) {
        binding.imgSave.also {
            if (title.isNullOrBlank() || disableButton) {
                it.alpha = 0.5f
            } else {
                it.alpha = 1f
            }
        }
    }

    private fun saveItemToDB() {
        updatedAt = LocalDateTime.now().toString()
        if (createdAt == null)
            createdAt = updatedAt
        itemData = StoredTodo(id, createdAt!!, updatedAt!!, title!!, desc, status)
        if (id == null) {
            id = addItem(getCurrentUserUid(), itemData!!)
            snack(binding.root, getString(R.string.new_todo_added), true)
        } else {
            editItem(getCurrentUserUid(), itemData!!)
            snack(binding.root, getString(R.string.todo_edited), true)
        }
        setData()
        isItemChanged = false
    }
}