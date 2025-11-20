package com.example.anyme.domain.dl

import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.local.db.OrderOption
import com.example.anyme.remote.Host

interface Media {

   val id: Int
   val title: String
   val mainPicture: MainPicture
   val host: Host

}

interface TypeRanking

interface ListStatus