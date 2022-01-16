package io.taptap.stupidenglish.features.stack.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.model.Word

class CardStackAdapter(
    var words: List<Word> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.stack_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.word.text = word.word
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, word.word, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var word: TextView = view.findViewById(R.id.tv_word)
    }

}
