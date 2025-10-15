package com.clipvault.repositories

import androidx.lifecycle.LiveData
import com.clipvault.db.LinkDao
import com.clipvault.model.Link

class LinkRepository(private val linkDao: LinkDao) {

    val allLinks: LiveData<List<Link>> = linkDao.getAllLinks()
    val favoriteLinks: LiveData<List<Link>> = linkDao.getFavoriteLinks()

    suspend fun insert(link: Link) {
        linkDao.insert(link)
    }

    suspend fun update(link: Link) {
        linkDao.update(link)
    }

    fun getLinksByCategory(category: String): LiveData<List<Link>> {
        return linkDao.getLinksByCategory(category)
    }

    fun searchLinks(searchQuery: String): LiveData<List<Link>> {
        return linkDao.searchLinks("%$searchQuery%")
    }
}
