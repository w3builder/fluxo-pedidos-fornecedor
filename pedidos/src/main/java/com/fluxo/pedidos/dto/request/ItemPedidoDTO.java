package com.fluxo.pedidos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    
    @NotNull(message = "O ID do produto é obrigatório")
    @Positive(message = "O ID do produto deve ser positivo")
    private Long produtoId;
    
    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade;
} 