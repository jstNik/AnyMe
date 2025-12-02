package com.example.anyme.data.visitors.renders

import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.utils.Resource
import kotlinx.coroutines.flow.StateFlow

data class CallbacksBundle (
   val updatingStatus: Resource<Unit> = Resource.success(Unit),
   val isRefreshing: Boolean = false,
   val onRefresh: (MediaUi) -> Unit = { },
   val onSave: (MediaUi) -> Unit = { },
   val onSwipeLeftToRight: (MediaUi) -> Unit = { },
   val onSwipeRightToLeft: (MediaUi) -> Unit = { }
)