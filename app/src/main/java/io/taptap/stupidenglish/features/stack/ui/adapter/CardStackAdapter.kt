package io.taptap.stupidenglish.features.stack.ui.adapter

import android.animation.Animator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.features.stack.ui.StackContract
import io.taptap.stupidenglish.ui.theme.Blue100
import io.taptap.stupidenglish.ui.theme.Grey600

class CardStackAdapter(
    var words: List<CardStackModel> = emptyList(),
    private val onEventSent: (event: StackContract.Event) -> Unit
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.stack_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.word.text = word.word
        holder.hint.text = word.description
        holder.hintButton.apply {
            setOnClickListener { onShowHintPress(word, holder) }
        }
    }

    private fun onShowHintPress(word: CardStackModel, holder: ViewHolder) {
        onEventSent(StackContract.Event.OnNo)

        holder.hint.apply {
            text = word.description
            visibility = View.VISIBLE
            alpha = 0.0f

            animate()
                .setStartDelay(500)
                .setDuration(500)
                .alpha(1.0f)
                .setListener(null)
        }
        holder.hintButton.apply {
            alpha = 1.0f

            animate()
                .setDuration(500)
                .alpha(0.0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                })

            setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = words.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word: TextView = view.findViewById(R.id.tv_word)
        val hint: TextView = view.findViewById(R.id.tv_hint)
        val hintButton: Button = view.findViewById(R.id.btn_hint)
    }
}

data class CardStackModel(
    val id: Long,
    val word: String,
    val description: String,
    val showDescription: Boolean,
    val points: Int
)