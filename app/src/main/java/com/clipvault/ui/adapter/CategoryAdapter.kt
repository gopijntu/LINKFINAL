package com.clipvault.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clipvault.R
import com.clipvault.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, selectedPosition == position)
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(category)
        }
    }

    override fun getItemCount() = categories.size

    fun getSelectedCategory(): Category? {
        return if (selectedPosition != -1) categories[selectedPosition] else null
    }

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)
        private val categoryName: TextView = itemView.findViewById(R.id.category_name)

        fun bind(category: Category, isSelected: Boolean) {
            categoryIcon.setImageResource(category.iconRes)
            categoryName.text = category.name
            itemView.setBackgroundResource(if (isSelected) R.drawable.category_selected_background else 0)
        }
    }
}