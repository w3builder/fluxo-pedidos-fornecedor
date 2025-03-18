package com.fluxo.pedidos.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fluxo.pedidos.dto.request.ItemPedidoDTO;
import com.fluxo.pedidos.dto.request.PedidoDTO;
import com.fluxo.pedidos.dto.response.PedidoResponseDTO;
import com.fluxo.pedidos.entity.Cliente;
import com.fluxo.pedidos.entity.ItemPedido;
import com.fluxo.pedidos.entity.Pedido;
import com.fluxo.pedidos.entity.Produto;
import com.fluxo.pedidos.entity.Revenda;
import com.fluxo.pedidos.entity.StatusPedido;
import com.fluxo.pedidos.exception.BusinessException;
import com.fluxo.pedidos.exception.ResourceNotFoundException;
import com.fluxo.pedidos.mapper.PedidoMapper;
import com.fluxo.pedidos.repository.ClienteRepository;
import com.fluxo.pedidos.repository.PedidoRepository;
import com.fluxo.pedidos.repository.ProdutoRepository;
import com.fluxo.pedidos.repository.RevendaRepository;
import com.fluxo.pedidos.service.PedidoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final RevendaRepository revendaRepository;
    private final PedidoMapper pedidoMapper;
    
    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(Long revendaId, PedidoDTO pedidoDTO) {
        log.info("Criando novo pedido para revenda ID: {}", revendaId);
        
        // Buscar revenda
        Revenda revenda = revendaRepository.findById(revendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Revenda não encontrada com id: " + revendaId));
        
        // Buscar cliente
        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + pedidoDTO.getClienteId()));
        
        // Verificar se o cliente pertence à revenda
        if (!cliente.getRevenda().getId().equals(revendaId)) {
            throw new BusinessException("Cliente não pertence à revenda informada");
        }
        
        // Criar o pedido
        Pedido pedido = new Pedido();
        pedido.setRevenda(revenda);
        pedido.setCliente(cliente);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setNumero(gerarNumeroPedido());
        pedido.setValorTotal(BigDecimal.ZERO);
        pedido.setItens(new ArrayList<>());
        
        // Processar itens do pedido
        processarItensPedido(pedido, pedidoDTO.getItens(), revendaId);
        
        // Calcular valor total do pedido
        pedido.calcularValorTotal();
        
        // Salvar o pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido criado com sucesso. ID: {}, Número: {}", pedidoSalvo.getId(), pedidoSalvo.getNumero());
        
        // Converter para DTO de resposta
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + id));
        
        return pedidoMapper.toResponseDTO(pedido);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorNumero(String numero) {
        log.info("Buscando pedido por número: {}", numero);
        
        Pedido pedido = pedidoRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com número: " + numero));
        
        return pedidoMapper.toResponseDTO(pedido);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorRevenda(Long revendaId) {
        log.info("Listando pedidos para revenda ID: {}", revendaId);
        
        // Verificar se a revenda existe
        if (!revendaRepository.existsById(revendaId)) {
            throw new ResourceNotFoundException("Revenda não encontrada com id: " + revendaId);
        }
        
        List<Pedido> pedidos = pedidoRepository.findByRevendaId(revendaId);
        
        return pedidos.stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        log.info("Listando pedidos para cliente ID: {}", clienteId);
        
        // Verificar se o cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId);
        }
        
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        
        return pedidos.stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PedidoResponseDTO cancelarPedido(Long id) {
        log.info("Cancelando pedido ID: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + id));
        
        // Verifica se o pedido pode ser cancelado
        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            throw new BusinessException("Não é possível cancelar um pedido já concluído");
        }
        
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já está cancelado");
        }
        
        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        
        log.info("Pedido cancelado com sucesso. ID: {}, Número: {}", pedidoCancelado.getId(), pedidoCancelado.getNumero());
        
        return pedidoMapper.toResponseDTO(pedidoCancelado);
    }
    
    /**
     * Processa os itens do pedido
     * @param pedido Pedido para adicionar os itens
     * @param itensDTO Lista de DTOs com os itens do pedido
     * @param revendaId ID da revenda
     */
    private void processarItensPedido(Pedido pedido, List<ItemPedidoDTO> itensDTO, Long revendaId) {
        if (itensDTO == null || itensDTO.isEmpty()) {
            throw new BusinessException("O pedido deve ter pelo menos um item");
        }
        
        for (ItemPedidoDTO itemDTO : itensDTO) {
            // Buscar produto
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + itemDTO.getProdutoId()));
            
            // Verificar se o produto pertence à revenda
            if (!produto.getRevenda().getId().equals(revendaId)) {
                throw new BusinessException("Produto não pertence à revenda informada: " + produto.getNome());
            }
            
            // Criar item do pedido
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.calcularValorTotal();
            
            // Adicionar ao pedido
            pedido.adicionarItem(item);
        }
    }
    
    /**
     * Gera um número único para o pedido baseado na data e hora atual
     * @return Número do pedido
     */
    private String gerarNumeroPedido() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return "PED" + agora.format(formatter);
    }
} 