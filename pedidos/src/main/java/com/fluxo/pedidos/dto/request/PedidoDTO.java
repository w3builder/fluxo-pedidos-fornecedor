package com.fluxo.pedidos.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class PedidoDTO {
    
    @NotNull(message = "O ID do cliente é obrigatório")
    @Positive(message = "O ID do cliente deve ser positivo")
    private Long clienteId;
    
    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    @Valid
    private List<ItemPedidoDTO> itens;
} 