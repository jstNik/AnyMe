package com.example.anyme.data.visitors

import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.domain.dl.Media

interface ConverterAcceptor {

   fun <T> acceptConverter(converterVisitor: ConverterVisitor, map: (LayerMapper) -> T): T

}