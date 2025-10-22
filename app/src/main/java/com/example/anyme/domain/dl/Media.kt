package com.example.anyme.domain.dl

import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.remote.Host

interface Media{

   val id: Int
   val title: String
   val mainPicture: MainPicture
   val host: Host

}