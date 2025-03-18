package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.request.TelefoneDTO;
import com.fluxo.pedidos.entity.Telefone;

@Mapper(componentModel = "spring")
public interface TelefoneMapper {
    
    @Mapping(target = "revenda", ignore = true)
    Telefone toEntity(TelefoneDTO dto);
    
    TelefoneDTO toDTO(Telefone entity);
} 