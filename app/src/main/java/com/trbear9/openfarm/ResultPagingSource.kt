package com.trbear9.openfarm

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import kotlin.math.min

class ResultPagingSource(
    val items: MutableList<Pair<Int, String>>
): PagingSource<Int, Pair<Int, String>>(){
    var size: Int = 0
    init {
        size += items.size
        "Initialized with size: $size".info("ResultPagingSource")
    }
    override fun getRefreshKey(state: PagingState<Int, Pair<Int, String>>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pair<Int, String>> {
        val page = params.key ?: 0
        val pageSize = 7
        "loading page: $page/${size/pageSize}".info("ResultPagingSource")

        return try {
            delay(1000)
            val nextKey = if (page.toDouble() >= (size/pageSize)) null else page + 1
            val item = items.subList(page * pageSize, min((page + 1) * pageSize, size)).toList()
            LoadResult.Page(
                data = item,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            "Error when loading page $page: ${e.message}".error("ResultPagingSource")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
