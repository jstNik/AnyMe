package com.example.anyme.data.visitors.converters

interface ConverterAcceptor {

   fun <T> acceptConverter(converterVisitor: ConverterVisitor, map: (LayerMapper) -> T): T

}