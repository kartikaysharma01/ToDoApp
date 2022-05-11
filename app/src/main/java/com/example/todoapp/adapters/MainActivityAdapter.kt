package com.example.todoapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ItemsTodolistBinding
import com.example.todoapp.helper.FirebaseAuthenticationHelper.getCurrentUserUid
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.changeItemStatus
import com.example.todoapp.helper.FirebaseRealtimeDBHelper.deleteItem
import com.example.todoapp.models.StoredTodo
import com.example.todoapp.utils.dialogYesOrNo
import com.example.todoapp.utils.snack
import com.example.todoapp.views.ListDetailActivity

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
            // if the item status is completed, gray it out
            if (todoItem.status) {
                it.cardView.alpha = 0.5f
                it.chkToDo.isChecked = true
            } else {
                it.chkToDo.isChecked = false
            }

            it.tvToDo.text = itemTitle
            it.imgDelete.setOnClickListener {
                context.dialogYesOrNo(
                    context.getString(R.string.delete_todo),
                    context.getString(R.string.delete_desc)
                ) { _, _ ->
                    deleteItem(getCurrentUserUid() ,todoItem)
                    context.snack(
                        holder.itemView,
                        context.getString(R.string.item_deleted),
                        false
                    )
                }
            }
            it.chkToDo.setOnClickListener { view ->
                if ((view as CompoundButton).isChecked) {
                    changeItemStatus(getCurrentUserUid() ,todoItem, true)
                } else {
                    changeItemStatus(getCurrentUserUid() ,todoItem, false)
                }
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
        RecyclerView.ViewHolder(viewDataBinding.root)
}