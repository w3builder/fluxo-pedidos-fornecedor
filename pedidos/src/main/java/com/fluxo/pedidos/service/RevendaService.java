package com.fluxo.pedidos.service;

import java.util.List;

import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;

public interface RevendaService {
    
    RevendaResponseDTO criarRevenda(RevendaDTO revendaDTO);
    
    RevendaResponseDTO buscarPorId(Long id);
    
    RevendaResponseDTO buscarPorCnpj(String cnpj);
    
    List<RevendaResponseDTO> listarTodas();
    
    RevendaResponseDTO atualizarRevenda(Long id, RevendaDTO revendaDTO);
    
    void deletarRevenda(Long id);
} 