package com.fluxo.pedidos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;
import com.fluxo.pedidos.entity.Revenda;

@Mapper(componentModel = "spring", uses = {TelefoneMapper.class, ContatoMapper.class, EnderecoMapper.class})
public interface RevendaMapper {
    
    @Mapping(target = "id", ignore = true)
    Revenda toEntity(RevendaDTO dto);
    
    RevendaResponseDTO toResponseDTO(Revenda entity);
    
    /**
     * Updates an existing Revenda entity with data from a RevendaDTO
     * @param entity The existing entity to update
     * @param dto The DTO containing the new data
     * @return The updated entity
     */
    @Mapping(target = "id", ignore = true)
    Revenda updateEntity(@MappingTarget Revenda entity, RevendaDTO dto);
} 