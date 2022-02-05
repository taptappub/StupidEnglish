package io.taptap.stupidenglish.features.stack.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.ui.flipCard

private const val FRONT = "front"
private const val BACK = "back"

class CardStackAdapter(
    var words: List<Word> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private val clickListener = View.OnClickListener { v: View ->
        flip(v)
    }

    private fun flip(v: View) {
        val frontView = v.findViewById<View>(R.id.view_front)
        val backView = v.findViewById<View>(R.id.view_back)
        if (v.tag?.toString().isNullOrEmpty() || v.tag == BACK) {
            flipToFront(v, frontView, backView)
        } else {
            flipToBack(v, frontView, backView)
        }
    }

    private fun flipToFront(rootView: View, frontView: View, backView: View) {
        if (rootView.tag?.toString().isNullOrEmpty() || rootView.tag == BACK) {
            rootView.tag = FRONT
            flipCard(
                rootView.context, backView, frontView,
                doOnStart = { rootView.isEnabled = false },
                doOnEnd = { rootView.isEnabled = true }
            )
        }
    }

    private fun flipToBack(rootView: View, frontView: View, backView: View) {
        if (rootView.tag?.toString().isNullOrEmpty() || rootView.tag == FRONT) {
            rootView.tag = BACK
            flipCard(
                rootView.context, frontView, backView,
                doOnStart = { rootView.isEnabled = false },
                doOnEnd = { rootView.isEnabled = true }
            )
        }
    }

//    fun flipTopCard(topWord: TopWord) {
//        val itemView = topWord.view
//        val frontView = itemView.findViewById<View>(R.id.view_front)
//        val backView = itemView.findViewById<View>(R.id.view_back)
//
//        flipToBack(itemView, frontView, backView)
//    }

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

    override fun getItemCount(): Int = words.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word: TextView = view.findViewById(R.id.tv_word)
        val hint: TextView = view.findViewById(R.id.tv_hint)
    }
}
