package com.prinkal.quiz.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.Quiz
import com.prinkal.quiz.ui.callbacks.ListItemClickListener
import com.prinkal.quiz.utils.Const
import kotlinx.android.synthetic.main.pq_item_quiz.view.*

class QuizAdapter(
    private val listener: ListItemClickListener<Int, Quiz>,
    private val quizes: ArrayList<Quiz>
) : RecyclerView.Adapter<QuizAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(vo: Quiz) {
            itemView.tvName.text = vo.name
            itemView.tvDesc.text = vo.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.pq_item_quiz, parent,
                false
            )
        )

    override fun getItemCount(): Int = quizes.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(quizes[position])
        holder.itemView.setOnClickListener({
            listener.onItemClick(
                Const.TYPE_CLICKED,
                quizes[position]
            )
        })
    }

    fun addData(list: List<Quiz>) {
        quizes.addAll(list)
    }

    fun clearList() {
        quizes.clear()
    }

}