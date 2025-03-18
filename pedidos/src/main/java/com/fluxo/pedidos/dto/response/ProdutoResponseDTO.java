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
public class ProdutoResponseDTO {
    
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal preco;
} 