package com.example.todoapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class StoredTodo(
    var id: String?,
    var createdAt: String,
    var updatedAt: String,
    var title: String,
    var desc: String?,
    var status: Boolean
) : Parcelable {
    constructor() : this("testId", LocalDateTime.now().toString(), LocalDateTime.now().toString(), "", null, false)
}