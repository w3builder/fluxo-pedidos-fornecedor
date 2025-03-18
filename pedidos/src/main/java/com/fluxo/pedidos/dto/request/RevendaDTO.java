package com.fluxo.pedidos.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevendaDTO {
    
    private Long id;
    
    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "Formato de CNPJ inválido. Use o formato XX.XXX.XXX/XXXX-XX")
    private String cnpj;
    
    @NotBlank(message = "A razão social é obrigatória")
    private String razaoSocial;
    
    @NotBlank(message = "O nome fantasia é obrigatório")
    private String nomeFantasia;
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;
    
    private List<TelefoneDTO> telefones;
    
    @NotEmpty(message = "Deve haver pelo menos um contato")
    @Valid
    private List<ContatoDTO> contatos;
    
    @NotEmpty(message = "Deve haver pelo menos um endereço de entrega")
    @Valid
    private List<EnderecoDTO> enderecos;
} 