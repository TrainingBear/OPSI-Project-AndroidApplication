package com.trbear9.openfarm

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class PlantResultViewModel
    (val items: MutableList<Pair<Int, String>>): ViewModel() {
    val pager = Pager(
        config = PagingConfig(
            pageSize = 8,
            prefetchDistance = 4,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { ResultPagingSource(items) }
    ).flow.cachedIn(viewModelScope)
}