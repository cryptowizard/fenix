/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.library.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import org.mozilla.fenix.components.history.PagedHistoryProvider

class HistoryViewModel(private val historyProvider: PagedHistoryProvider) : ViewModel() {
    var history: LiveData<PagedList<HistoryItem>>
    var userHasHistory = MutableLiveData(true)
    var datasource: LiveData<HistoryDataSource>
    val query = MutableLiveData<String>(null)

    init {
        val historyDataSourceFactory = HistoryDataSourceFactory("", historyProvider)
        datasource = historyDataSourceFactory.datasource

        history = Transformations.switchMap(query) { query ->
            getHistoryLiveData(query ?: "")
        }
    }

    private fun getHistoryLiveData(query: String): LiveData<PagedList<HistoryItem>> {
        val historyDataSourceFactory = HistoryDataSourceFactory(query, historyProvider)
        datasource = historyDataSourceFactory.datasource

        return LivePagedListBuilder(historyDataSourceFactory, PAGE_SIZE)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<HistoryItem>() {
                override fun onZeroItemsLoaded() {
                    userHasHistory.value = false
                }
            })
            .build()
    }


    fun invalidate() {
        datasource.value?.invalidate()
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
