package com.example.anyme.domain.dl

import com.example.anyme.domain.dl.mal.MainPicture

interface Media{

   val id: Int
   val title: String
   val mainPicture: MainPicture

}