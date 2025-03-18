package com.fluxo.pedidos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fluxo.pedidos.dto.request.PedidoDTO;
import com.fluxo.pedidos.dto.response.PedidoResponseDTO;
import com.fluxo.pedidos.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/revendas/{revendaId}/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos das revendas")
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    @PostMapping
    @Operation(summary = "Criar um novo pedido", description = "Cria um novo pedido para uma revenda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso", 
                     content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Revenda ou cliente não encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId,
            @Valid @RequestBody PedidoDTO pedidoDTO) {
        
        PedidoResponseDTO pedidoCriado = pedidoService.criarPedido(revendaId, pedidoDTO);
        return new ResponseEntity<>(pedidoCriado, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido único pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado", 
                     content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId,
            @Parameter(description = "ID do pedido", required = true) 
            @PathVariable Long id) {
        
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        
        // Verificar se o pedido pertence à revenda
        if (!pedido.getCliente().getRevendaId().equals(revendaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(pedido);
    }
    
    @GetMapping("/numero/{numero}")
    @Operation(summary = "Buscar pedido por número", description = "Retorna um pedido único pelo seu número")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado", 
                     content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorNumero(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId,
            @Parameter(description = "Número do pedido", required = true) 
            @PathVariable String numero) {
        
        PedidoResponseDTO pedido = pedidoService.buscarPorNumero(numero);
        
        // Verificar se o pedido pertence à revenda
        if (!pedido.getCliente().getRevendaId().equals(revendaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(pedido);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os pedidos da revenda", description = "Retorna uma lista com todos os pedidos da revenda")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada com sucesso")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPorRevenda(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId) {
        
        List<PedidoResponseDTO> pedidos = pedidoService.listarPorRevenda(revendaId);
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos por cliente", description = "Retorna uma lista com todos os pedidos de um cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPorCliente(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId,
            @Parameter(description = "ID do cliente", required = true) 
            @PathVariable Long clienteId) {
        
        List<PedidoResponseDTO> pedidos = pedidoService.listarPorCliente(clienteId);
        
        // Filtrar apenas os pedidos da revenda informada
        pedidos = pedidos.stream()
                .filter(p -> p.getCliente().getRevendaId().equals(revendaId))
                .toList();
        
        return ResponseEntity.ok(pedidos);
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar um pedido", description = "Cancela um pedido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso", 
                     content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @Parameter(description = "ID da revenda", required = true) 
            @PathVariable Long revendaId,
            @Parameter(description = "ID do pedido", required = true) 
            @PathVariable Long id) {
        
        // Verificar se o pedido pertence à revenda
        PedidoResponseDTO pedidoExistente = pedidoService.buscarPorId(id);
        if (!pedidoExistente.getCliente().getRevendaId().equals(revendaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }
} 