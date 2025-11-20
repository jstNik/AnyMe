package com.example.anyme.local.db

sealed class MalOrderOption (
   override val by: OrderBy,
   override val direction: OrderDirection
): OrderOption {

   data object Title: OrderBy {

      data object Asc : MalOrderOption(this, OrderDirection.Asc)

      data object Desc : MalOrderOption(this, OrderDirection.Desc)

      override fun toString(): String = "title"

   }

   data object LastUpdatedAt: OrderBy {

      data object Asc : MalOrderOption(this, OrderDirection.Asc)

      data object Desc : MalOrderOption(this, OrderDirection.Desc)

   }

   override fun toString(): String = "$by $direction"

}