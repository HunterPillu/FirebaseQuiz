package com.edu.mvvmtutorial.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.callbacks.ListItemClickListener
import com.edu.mvvmtutorial.utils.Const
import kotlinx.android.synthetic.main.pq_item_player.view.*

class PlayerAdapter(val context: Context, val listener: ListItemClickListener<Int, User>) :
    RecyclerView.Adapter<PlayerAdapter.RecordViewHolder>() {
    private var cDisabled: Int
    private var cOffline: Int
    private var cOnline: Int
    //private val TAG = this::class.java.simpleName

    private var list: MutableList<User>

    fun addList(list: List<User>) {
        this.list.addAll(list)
        notifyItemRangeInserted(0, list.size)
        //notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val listItem =
            LayoutInflater.from(parent.context).inflate(R.layout.pq_item_player, parent, false)
        return RecordViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val vo = list[position]
        holder.itemView.tvName.text = vo.name
        holder.itemView.tvLastMsg.text =
            context.getString(if (vo.online) R.string.online else R.string.offline)
        holder.itemView.tvLastMsg.setTextColor(if (vo.online) cOnline else cOffline)
        holder.itemView.setOnClickListener { listener.onItemClick(Const.TYPE_CLICKED, vo) }
    }

    /*private fun getStatusImg(fetchStatus: Int): Int {
        return when (fetchStatus) {
            1 -> R.drawable.round_green
            2 -> R.drawable.round_red
            else -> R.drawable.round_grey
        }
    }*/

    private fun getStatusColor(fetchStatus: Int): Int {
        return when (fetchStatus) {
            1 -> cOnline
            2 -> cOffline
            else -> cDisabled
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)

        //val ivStatus: AppCompatImageView = itemView.findViewById(R.id.ivStatus)
        //val tvLastMsg: AppCompatTextView = itemView.findViewById(R.id.tvLastMsg)

    }


    init {
        list = ArrayList()
        cOnline = getColor(context, R.color.online)
        cOffline = getColor(context, R.color.offline)
        cDisabled = getColor(context, R.color.disabled)
    }
}