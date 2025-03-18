package com.fluxo.pedidos.service;

import java.util.List;

import com.fluxo.pedidos.dto.request.PedidoDTO;
import com.fluxo.pedidos.dto.response.PedidoResponseDTO;

public interface PedidoService {
    
    /**
     * Cria um novo pedido para uma revenda
     * @param revendaId ID da revenda
     * @param pedidoDTO Dados do pedido
     * @return DTO de resposta com os dados do pedido criado
     */
    PedidoResponseDTO criarPedido(Long revendaId, PedidoDTO pedidoDTO);
    
    /**
     * Busca um pedido pelo ID
     * @param id ID do pedido
     * @return DTO de resposta com os dados do pedido
     */
    PedidoResponseDTO buscarPorId(Long id);
    
    /**
     * Busca um pedido pelo número
     * @param numero Número do pedido
     * @return DTO de resposta com os dados do pedido
     */
    PedidoResponseDTO buscarPorNumero(String numero);
    
    /**
     * Lista todos os pedidos de uma revenda
     * @param revendaId ID da revenda
     * @return Lista de DTOs de resposta com os dados dos pedidos
     */
    List<PedidoResponseDTO> listarPorRevenda(Long revendaId);
    
    /**
     * Lista todos os pedidos de um cliente
     * @param clienteId ID do cliente
     * @return Lista de DTOs de resposta com os dados dos pedidos
     */
    List<PedidoResponseDTO> listarPorCliente(Long clienteId);
    
    /**
     * Cancela um pedido
     * @param id ID do pedido
     * @return DTO de resposta com os dados do pedido cancelado
     */
    PedidoResponseDTO cancelarPedido(Long id);
} 