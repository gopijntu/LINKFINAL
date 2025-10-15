package com.clipvault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clipvault.databinding.ItemCategoryBinding
import com.clipvault.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClicked: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryIcon.setImageResource(category.icon)
        holder.binding.categoryLabel.text = category.name
        holder.binding.root.isSelected = selectedPosition == position
        holder.binding.root.setOnClickListener {
            onCategoryClicked(category)
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = categories.size

    fun getSelectedCategory(): Category? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            categories[selectedPosition]
        } else {
            null
        }
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
