package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.request.ContatoDTO;
import com.fluxo.pedidos.entity.Contato;

@Mapper(componentModel = "spring")
public interface ContatoMapper {
    
    @Mapping(target = "revenda", ignore = true)
    Contato toEntity(ContatoDTO dto);
    
    ContatoDTO toDTO(Contato entity);
} 