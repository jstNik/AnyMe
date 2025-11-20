package com.example.anyme.local.db

interface OrderOption{

   val by: OrderBy
   val direction: OrderDirection

}

sealed interface OrderBy{



}

sealed interface OrderDirection{

   data object Asc: OrderDirection {
      override fun toString(): String = "asc"
   }

   data object Desc: OrderDirection {
      override fun toString(): String = "desc"
   }

}