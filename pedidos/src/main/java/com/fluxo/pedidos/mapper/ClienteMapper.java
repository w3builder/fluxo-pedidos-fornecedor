package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.response.ClienteResponseDTO;
import com.fluxo.pedidos.entity.Cliente;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "revendaId", source = "revenda.id")
    ClienteResponseDTO toResponseDTO(Cliente entity);
} 