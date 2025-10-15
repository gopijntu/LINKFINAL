package com.clipvault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clipvault.R
import com.clipvault.databinding.ItemLinkBinding
import com.clipvault.model.Link
import com.squareup.picasso.Picasso

class LinkAdapter(
    private var links: List<Link>,
    private val onFavoriteClicked: (Link) -> Unit
) : RecyclerView.Adapter<LinkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val link = links[position]
        holder.binding.linkTitle.text = link.title
        holder.binding.linkUrl.text = link.url
        if (link.notes.isNullOrBlank()) {
            holder.binding.linkNotes.visibility = android.view.View.GONE
        } else {
            holder.binding.linkNotes.visibility = android.view.View.VISIBLE
            holder.binding.linkNotes.text = link.notes
        }

        if (link.thumbnailUrl.isNotBlank()) {
            Picasso.get().load(link.thumbnailUrl).into(holder.binding.thumbnailImage)
        } else {
            // Placeholder logic
            val placeholderIcon = when {
                link.url.contains("instagram.com") -> R.drawable.ic_instagram
                link.url.contains("facebook.com") -> R.drawable.ic_facebook
                link.url.contains("twitter.com") -> R.drawable.ic_twitter
                // Add more cases for other apps
                else -> R.drawable.ic_launcher_foreground // Default placeholder
            }
            holder.binding.thumbnailImage.setImageResource(placeholderIcon)
        }

        if (link.isFavorite) {
            holder.binding.favoriteButton.setImageResource(R.drawable.ic_heart)
        } else {
            holder.binding.favoriteButton.setImageResource(R.drawable.ic_heart_outline)
        }

        holder.binding.favoriteButton.setOnClickListener {
            onFavoriteClicked(link)
        }
    }

    override fun getItemCount(): Int = links.size

    fun updateLinks(newLinks: List<Link>) {
        this.links = newLinks
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemLinkBinding) : RecyclerView.ViewHolder(binding.root)
}
