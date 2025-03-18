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
public class EnderecoDTO {
    
    private Long id;
    
    @NotBlank(message = "O logradouro é obrigatório")
    private String logradouro;
    
    @NotBlank(message = "O número é obrigatório")
    private String numero;
    
    private String complemento;
    
    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;
    
    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;
    
    @NotBlank(message = "O estado é obrigatório")
    private String estado;
    
    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Formato de CEP inválido. Use o formato XXXXX-XXX")
    private String cep;
} 