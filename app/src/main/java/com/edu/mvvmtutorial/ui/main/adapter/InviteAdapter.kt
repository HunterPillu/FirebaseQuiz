package com.edu.mvvmtutorial.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.callbacks.ListItemClickListener
import com.edu.mvvmtutorial.utils.Const
import kotlinx.android.synthetic.main.pq_item_player.view.*

class InviteAdapter(private val listener: ListItemClickListener<Int, User>, query: QueryCreator) :
    FirestoreAdapter<User, InviteAdapter.PlayerVH>(User::class.java, query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerVH {
        val listItem =
            LayoutInflater.from(parent.context).inflate(R.layout.pq_item_player, parent, false)
        return PlayerVH(listItem)
    }

    override fun onBindViewHolder(holder: PlayerVH, position: Int) {
        val vo = get(position)
        holder.itemView.tvName.text = vo.name
        holder.itemView.tvLastMsg.text =
            holder.itemView.context.getString(if (vo.online) R.string.online else R.string.offline)
        holder.itemView.tvLastMsg.setTextColor(getColor(holder.itemView.context, vo.online))
        holder.itemView.setOnClickListener { listener.onItemClick(Const.TYPE_CLICKED, vo) }
    }

    private fun getColor(context: Context, online: Boolean): Int {
        if (online) {
            return ContextCompat.getColor(context, R.color.online)
        } else {
            return ContextCompat.getColor(context, R.color.offline)
        }
    }


    class PlayerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}