package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ItemsTodolistBinding
import com.example.todoapp.helper.FirebaseAuthenticationHelper.getCurrentUserUid
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.changeItemStatus
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.deleteItem
import com.example.todoapp.models.StoredTodo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivityAdapter :
    RecyclerView.Adapter<MainActivityAdapter.MyViewHolder>() {
    lateinit var context: Context

    var items: List<StoredTodo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val withDataBinding = ItemsTodolistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context
        return MyViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todoItem = items[position]
        val itemTitle = todoItem.title

        holder.viewDataBinding.also {
            // if the item is completed, gray it out
            if (todoItem.status) {
                it.cardView.alpha = 0.5f
                it.chkToDo.isChecked = true
            } else {
                it.chkToDo.isChecked = false
            }

            it.tvToDo.text = itemTitle
            it.imgDelete.setOnClickListener {
                deleteItem(getCurrentUserUid() ,todoItem)
            }
            it.chkToDo.setOnCheckedChangeListener { compoundButton, b ->
                changeItemStatus(getCurrentUserUid() ,todoItem, b)
            }

            it.cardView.setOnClickListener {
                val intent = Intent(context, ListDetailActivity::class.java)
                intent.putExtra("item_data", todoItem)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(val viewDataBinding: ItemsTodolistBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        init {

        }
    }
}