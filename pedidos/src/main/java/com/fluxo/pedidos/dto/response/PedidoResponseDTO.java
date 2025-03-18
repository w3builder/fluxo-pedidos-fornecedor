package com.fluxo.pedidos.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fluxo.pedidos.entity.StatusPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    
    private Long id;
    private String numero;
    private ClienteResponseDTO cliente;
    private LocalDateTime dataHora;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemPedidoResponseDTO> itens;
} 