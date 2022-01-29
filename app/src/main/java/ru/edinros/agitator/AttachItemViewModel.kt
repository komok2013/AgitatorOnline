package ru.edinros.agitator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttachItemViewModel @Inject constructor(val repository: MainRepository):ViewModel() {
    private val _items = MutableStateFlow(emptyList<String>())
    val items = _items.asStateFlow()
    init {
        fetchItems()
    }
    fun fetchItems(){
        _items.value = (0..12).map{
            it.toString()
        }.toList()
    }
    override fun onCleared() {
        Timber.d("Cleared %s",this)
        super.onCleared()
    }

    fun doSomething() {
        _items.value = _items.value.mapIndexed {i,e->if(i==6) e.plus("mod") else e  }
    }
}