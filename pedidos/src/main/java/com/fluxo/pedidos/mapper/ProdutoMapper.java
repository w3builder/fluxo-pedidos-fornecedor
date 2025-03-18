package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.response.ProdutoResponseDTO;
import com.fluxo.pedidos.entity.Produto;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {
    
    @Mapping(target = "id", source = "id")
    ProdutoResponseDTO toResponseDTO(Produto entity);
} 