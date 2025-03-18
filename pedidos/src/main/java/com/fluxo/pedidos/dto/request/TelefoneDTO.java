package com.fluxo.pedidos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefoneDTO {
    
    private Long id;
    
    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$", message = "Formato de telefone inválido. Use o formato (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    private String numero;
} 