package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fluxo.pedidos.dto.request.EnderecoDTO;
import com.fluxo.pedidos.entity.Endereco;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {
    
    @Mapping(target = "revenda", ignore = true)
    Endereco toEntity(EnderecoDTO dto);
    
    EnderecoDTO toDTO(Endereco entity);
} 