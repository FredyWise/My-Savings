package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Repository.AccountRepository
import com.fredy.mysavings.Repository.CategoryRepository
import com.fredy.mysavings.Repository.RecordRepository
import com.fredy.mysavings.Repository.TrueRecord
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.minusFilterDate
import com.fredy.mysavings.Util.plusFilterDate
import com.fredy.mysavings.Util.updateFilterState
import com.fredy.mysavings.ViewModels.Event.DetailEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
): ViewModel() {
    private val _resource = mutableStateOf(
        ResourceState()
    )
    val resource: State<ResourceState> = _resource

    private val _sortType = MutableStateFlow(
        SortType.DESCENDING
    )

    private val _data = MutableStateFlow(
        DetailData()
    )

    private val _records = _data.flatMapLatest { data ->
        when (data.title) {
            DetailType.Account -> {
//                data.update {
//                    it.copy(
//                        account = accountRepository.getAccount(data.id)
//                    )
//                }
                recordRepository.getUserAccountRecordsOrderedByDateTime(
                    data.id
                )
            }

            DetailType.Category -> {
                recordRepository.getUserCategoryRecordsOrderedByDateTime(
                    data.id
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        listOf(TrueRecord())
    )


    private val _state = MutableStateFlow(
        DetailState()
    )

    val state = combine(
        _state,
        _sortType,
        _records,
        _data,
    ) { state, sortType, records, data ->
        _resource.value = ResourceState(isLoading = true)
        state.copy(
            trueRecordMaps = records.groupBy {
                it.record.recordDateTime.toLocalDate()
            }.toSortedMap(if (sortType == SortType.DESCENDING) {
                compareByDescending { it }
            } else {
                compareBy { it }
            }).map {
                RecordMap(
                    recordDate = it.key,
                    records = it.value
                )
            },
            data = data,
            sortType = sortType,
        )
    }.onCompletion {
        _resource.value = ResourceState(isLoading = false)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DetailState()
    )


    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        showDialog = true,
                    )
                }
            }

            is DetailEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        showDialog = false,
                    )
                }
            }

            is DetailEvent.Init -> {
                _state.update {
                    it.copy(
                        data = event.detailData
                    )
                }
            }
        }
    }

}

data class DetailState(
    val trueRecordMaps: List<RecordMap> = listOf(),
    val showDialog: Boolean = false,
    val sortType: SortType = SortType.ASCENDING,
    val data: DetailData = DetailData()
)

data class DetailData(
    val id: String = "-1",
    val title: DetailType = DetailType.Account,
    val account: Account = Account(),
    val category: Category = Category()
)

enum class DetailType {
    Category,
    Account
}