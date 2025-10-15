package com.clipvault.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.clipvault.model.Link
import com.clipvault.repositories.LinkRepository
import kotlinx.coroutines.launch

class LinkViewModel(private val repository: LinkRepository) : ViewModel() {

    private val searchQuery = MutableLiveData<String>("")

    val allLinks: LiveData<List<Link>> = searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) {
            repository.allLinks
        } else {
            repository.searchLinks(query)
        }
    }

    val favoriteLinks: LiveData<List<Link>> = repository.favoriteLinks

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun insert(link: Link) = viewModelScope.launch {
        repository.insert(link)
    }

    fun update(link: Link) = viewModelScope.launch {
        repository.update(link)
    }

    fun getLinksByCategory(category: String): LiveData<List<Link>> {
        return repository.getLinksByCategory(category)
    }
}

class LinkViewModelFactory(private val repository: LinkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LinkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LinkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
