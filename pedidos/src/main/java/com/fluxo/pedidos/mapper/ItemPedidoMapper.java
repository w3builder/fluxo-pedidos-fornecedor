package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.response.ItemPedidoResponseDTO;
import com.fluxo.pedidos.entity.ItemPedido;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public interface ItemPedidoMapper {
    
    @Mapping(target = "produto", source = "produto")
    ItemPedidoResponseDTO toResponseDTO(ItemPedido entity);
} 