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
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
        binding.llDates.visibility = View.VISIBLE
        checkSaveEnabled(true)
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

    override fun onBackPressed() {
        if (isItemChanged) {
            dialogYesOrNo(
                this,
                "Discard Changes?",
                "The current change to the item will be permanently lost."
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
            WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
            onBackPressed()
        }

        binding.imgSave.setOnClickListener {
            WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
            if (it.alpha == 0.5f) {
                if (title.isNullOrBlank())
                    showSnackBar("ToDo Title can not be empty.", false)
                else
                    showSnackBar("No changes to be saved.", false)
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

    private fun showSnackBar(msg: String, succeed: Boolean) {
        val snackBar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)

        succeed.let {
            val color = if (it) R.color.snack_succeed else R.color.snack_fail
            snackBar.setBackgroundTint(ContextCompat.getColor(this, color))
        }
        snackBar.show()
    }

    private fun dateParser(date: String): String {
        val localDateTime = LocalDateTime.parse(date)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd,yyyy HH:mm")
        return formatter.format(localDateTime)
    }

    private fun saveItemToDB() {
        updatedAt = LocalDateTime.now().toString()
        if (createdAt == null)
            createdAt = updatedAt
        itemData = StoredTodo(id, createdAt!!, updatedAt!!, title!!, desc, status)
        if (id == null) {
            id = addItem(getCurrentUserUid(), itemData!!)
            showSnackBar("New ToDo Added Successfully.", true)
        } else {
            editItem(getCurrentUserUid(), itemData!!)
            showSnackBar("ToDo edited Successfully.", true)
        }
        setData()
        isItemChanged = false
    }
}