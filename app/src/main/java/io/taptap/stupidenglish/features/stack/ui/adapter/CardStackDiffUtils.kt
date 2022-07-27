package io.taptap.stupidenglish.features.stack.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class CardStackDiffUtils(
    private val newList: List<CardStackModel>,
    private val oldList: List<CardStackModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct: CardStackModel = oldList[oldItemPosition]
        val newProduct: CardStackModel = newList[newItemPosition]
        return oldProduct.id == newProduct.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct: CardStackModel = oldList[oldItemPosition]
        val newProduct: CardStackModel = newList[newItemPosition]
        return oldProduct == newProduct
    }
}
