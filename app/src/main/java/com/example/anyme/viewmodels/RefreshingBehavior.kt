package com.example.anyme.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RefreshingBehavior(startingState: RefreshingStatus = RefreshingStatus.InitialRefreshing) {

   enum class RefreshingStatus{
      InitialRefreshing, Refreshing, NotRefreshing
   }

   private val _isRefreshing = MutableStateFlow(startingState)
   val isRefreshing get () = _isRefreshing.asStateFlow()

   fun refresh(){
      _isRefreshing.value = RefreshingStatus.Refreshing
   }

   fun stop(){
      _isRefreshing.value = RefreshingStatus.NotRefreshing
   }

   fun restart(){
      _isRefreshing.value = RefreshingStatus.InitialRefreshing
   }

}