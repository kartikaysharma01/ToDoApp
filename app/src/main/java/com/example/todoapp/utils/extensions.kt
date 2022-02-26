package com.example.todoapp.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.todoapp.R
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Activity.hideKeyboard() {
    WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
}

@Suppress("DEPRECATION")
fun Activity.hideStatusBar() {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Context.snack(view: View, msg: String, succeed: Boolean) {
    val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
    val color = if (succeed) R.color.snack_succeed else R.color.snack_fail
    snackBar.setBackgroundTint(ContextCompat.getColor(this, color))
    snackBar.show()
}

fun dateParser(date: String): String {
    val localDateTime = LocalDateTime.parse(date)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd,yyyy HH:mm")
    return formatter.format(localDateTime)
}

fun Context.dialogYesOrNo(
    title: String,
    message: String,
    listener: DialogInterface.OnClickListener
) {
    val builder = AlertDialog.Builder(this)
    builder.setPositiveButton("Yes") { dialog, id ->
        dialog.dismiss()
        listener.onClick(dialog, id)
    }
    builder.setNegativeButton("No", null)
    val alert = builder.create()
    alert.setTitle(title)
    alert.setMessage(message)
    alert.show()
}
