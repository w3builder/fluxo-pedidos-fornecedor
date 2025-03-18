package com.fluxo.pedidos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome do contato é obrigatório")
    private String nome;
    
    private boolean principal;
} 