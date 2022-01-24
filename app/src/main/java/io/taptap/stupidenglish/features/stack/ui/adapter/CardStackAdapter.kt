package io.taptap.stupidenglish.features.stack.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.ui.flipCard

class CardStackAdapter(
    var words: List<Word> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private val clickListener = View.OnClickListener { v: View ->
        val frontView = v.findViewById<View>(R.id.view_front)
        val backView = v.findViewById<View>(R.id.view_back)
        if (v.tag?.toString().isNullOrEmpty() || v.tag == "back") {
            v.tag = "front"
            flipCard(
                v.context, backView, frontView,
                doOnStart = { v.isEnabled = false },
                doOnEnd = { v.isEnabled = true }
            )
        } else {
            v.tag = "back"
            flipCard(
                v.context, frontView, backView,
                doOnStart = { v.isEnabled = false },
                doOnEnd = { v.isEnabled = true }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.stack_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.word.text = word.word
        holder.hint.text = word.description
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word: TextView = view.findViewById(R.id.tv_word)
        val hint: TextView = view.findViewById(R.id.tv_hint)
    }
}
