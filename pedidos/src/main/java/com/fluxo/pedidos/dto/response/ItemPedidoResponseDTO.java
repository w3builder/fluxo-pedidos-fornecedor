package com.fluxo.pedidos.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {
    
    private Long id;
    private ProdutoResponseDTO produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorTotal;
} 