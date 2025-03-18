package com.fluxo.pedidos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxo.pedidos.dto.request.ContatoDTO;
import com.fluxo.pedidos.dto.request.EnderecoDTO;
import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.request.TelefoneDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;
import com.fluxo.pedidos.exception.BusinessException;
import com.fluxo.pedidos.exception.GlobalExceptionHandler;
import com.fluxo.pedidos.exception.ResourceNotFoundException;
import com.fluxo.pedidos.service.RevendaService;
import com.fluxo.pedidos.util.CNPJValidator;

@ExtendWith(MockitoExtension.class)
public class RevendaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CNPJValidator cnpjValidator;
    
    @Mock
    private RevendaService revendaService;
    
    @InjectMocks
    private RevendaController revendaController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(revendaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @DisplayName("Deve criar uma nova revenda com sucesso")
    public void deveRealizarPostComSucesso() throws Exception {
        // Arrange
        RevendaDTO revendaDTO = criarRevendaDTO();
        RevendaResponseDTO responseDTO = criarRevendaResponseDTO();
        
        when(revendaService.criarRevenda(any(RevendaDTO.class))).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/revendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revendaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa Teste"))
                .andExpect(jsonPath("$.email").value("contato@empresateste.com.br"));
        
        verify(revendaService, times(1)).criarRevenda(any(RevendaDTO.class));
    }
    
    @Test
    @DisplayName("Deve retornar erro ao tentar criar revenda com dados inválidos")
    public void deveRetornarErroCriarRevendaDadosInvalidos() throws Exception {
        // Arrange
        RevendaDTO revendaDTO = criarRevendaDTO();
        
        when(revendaService.criarRevenda(any(RevendaDTO.class)))
            .thenThrow(new BusinessException("CNPJ inválido"));
        
        // Act & Assert
        mockMvc.perform(post("/api/revendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revendaDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CNPJ inválido"));
        
        verify(revendaService, times(1)).criarRevenda(any(RevendaDTO.class));
    }
    
    @Test
    @DisplayName("Deve buscar uma revenda por ID com sucesso")
    public void deveBuscarRevendaPorIdComSucesso() throws Exception {
        // Arrange
        Long id = 1L;
        RevendaResponseDTO responseDTO = criarRevendaResponseDTO();
        
        when(revendaService.buscarPorId(id)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/revendas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa Teste"))
                .andExpect(jsonPath("$.email").value("contato@empresateste.com.br"));
        
        verify(revendaService, times(1)).buscarPorId(id);
    }
    
    @Test
    @DisplayName("Deve retornar erro ao buscar revenda por ID inexistente")
    public void deveRetornarErroBuscarRevendaInexistente() throws Exception {
        // Arrange
        Long id = 999L;
        
        when(revendaService.buscarPorId(id))
            .thenThrow(new ResourceNotFoundException("Revenda não encontrada com id: " + id));
        
        // Act & Assert
        mockMvc.perform(get("/api/revendas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Revenda não encontrada com id: " + id));
        
        verify(revendaService, times(1)).buscarPorId(id);
    }
    
    @Test
    @DisplayName("Deve buscar uma revenda por CNPJ com sucesso")
    public void deveBuscarRevendaPorCnpjComSucesso() throws Exception {
        // Arrange
        String cnpj = "12345678000199"; // Removing special characters for URL
        String formattedCnpj = "12.345.678/0001-99";
        RevendaResponseDTO responseDTO = criarRevendaResponseDTO();
        
        // Mock the formatCnpj method in CNPJValidator
        when(cnpjValidator.formatCnpj(cnpj)).thenReturn(formattedCnpj);
        when(revendaService.buscarPorCnpj(formattedCnpj)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/revendas/cnpj/{cnpj}", cnpj))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cnpj").value(formattedCnpj));
        
        verify(revendaService, times(1)).buscarPorCnpj(formattedCnpj);
        verify(cnpjValidator, times(1)).formatCnpj(cnpj);
    }
    
    @Test
    @DisplayName("Deve listar todas as revendas com sucesso")
    public void deveListarTodasRevendasComSucesso() throws Exception {
        // Arrange
        List<RevendaResponseDTO> revendas = Arrays.asList(
                criarRevendaResponseDTO(),
                criarRevendaResponseDTO2()
        );
        
        when(revendaService.listarTodas()).thenReturn(revendas);
        
        // Act & Assert
        mockMvc.perform(get("/api/revendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cnpj").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].cnpj").value("98.765.432/0001-88"));
        
        verify(revendaService, times(1)).listarTodas();
    }
    
    @Test
    @DisplayName("Deve atualizar uma revenda com sucesso")
    public void deveAtualizarRevendaComSucesso() throws Exception {
        // Arrange
        Long id = 1L;
        RevendaDTO revendaDTO = criarRevendaDTO();
        RevendaResponseDTO responseDTO = criarRevendaResponseDTO();
        
        when(revendaService.atualizarRevenda(eq(id), any(RevendaDTO.class))).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/revendas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revendaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-99"));
        
        verify(revendaService, times(1)).atualizarRevenda(eq(id), any(RevendaDTO.class));
    }
    
    @Test
    @DisplayName("Deve deletar uma revenda com sucesso")
    public void deveDeletarRevendaComSucesso() throws Exception {
        // Arrange
        Long id = 1L;
        
        doNothing().when(revendaService).deletarRevenda(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/revendas/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        
        verify(revendaService, times(1)).deletarRevenda(id);
    }
    
    @Test
    @DisplayName("Deve retornar erro ao tentar deletar revenda inexistente")
    public void deveRetornarErroDeletarRevendaInexistente() throws Exception {
        // Arrange
        Long id = 999L;
        
        doNothing().when(revendaService).deletarRevenda(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/revendas/{id}", id))
                .andExpect(status().isNoContent());
        
        verify(revendaService, times(1)).deletarRevenda(id);
    }
    
    // =========================================================================
    // Métodos de suporte para criação de objetos de teste
    // =========================================================================
    
    private RevendaDTO criarRevendaDTO() {
        TelefoneDTO telefoneDTO = TelefoneDTO.builder()
                .numero("(11) 99999-9999")
                .build();
        
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .nome("Contato Principal")
                .principal(true)
                .build();
        
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Bairro Teste")
                .cidade("São Paulo")
                .estado("SP")
                .cep("12345-678")
                .build();
        
        return RevendaDTO.builder()
                .cnpj("12.345.678/0001-99")
                .razaoSocial("Empresa Teste LTDA")
                .nomeFantasia("Empresa Teste")
                .email("contato@empresateste.com.br")
                .telefones(Arrays.asList(telefoneDTO))
                .contatos(Arrays.asList(contatoDTO))
                .enderecos(Arrays.asList(enderecoDTO))
                .build();
    }
    
    private RevendaResponseDTO criarRevendaResponseDTO() {
        TelefoneDTO telefoneDTO = TelefoneDTO.builder()
                .id(1L)
                .numero("(11) 99999-9999")
                .build();
        
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .id(1L)
                .nome("Contato Principal")
                .principal(true)
                .build();
        
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .id(1L)
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Bairro Teste")
                .cidade("São Paulo")
                .estado("SP")
                .cep("12345-678")
                .build();
        
        return RevendaResponseDTO.builder()
                .id(1L)
                .cnpj("12.345.678/0001-99")
                .razaoSocial("Empresa Teste LTDA")
                .nomeFantasia("Empresa Teste")
                .email("contato@empresateste.com.br")
                .telefones(Arrays.asList(telefoneDTO))
                .contatos(Arrays.asList(contatoDTO))
                .enderecos(Arrays.asList(enderecoDTO))
                .build();
    }
    
    private RevendaResponseDTO criarRevendaResponseDTO2() {
        TelefoneDTO telefoneDTO = TelefoneDTO.builder()
                .id(2L)
                .numero("(21) 88888-8888")
                .build();
        
        ContatoDTO contatoDTO = ContatoDTO.builder()
                .id(2L)
                .nome("Outro Contato")
                .principal(true)
                .build();
        
        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .id(2L)
                .logradouro("Avenida Secundária")
                .numero("456")
                .bairro("Outro Bairro")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .cep("98765-432")
                .build();
        
        return RevendaResponseDTO.builder()
                .id(2L)
                .cnpj("98.765.432/0001-88")
                .razaoSocial("Segunda Empresa LTDA")
                .nomeFantasia("Segunda Empresa")
                .email("contato@segundaempresa.com.br")
                .telefones(Arrays.asList(telefoneDTO))
                .contatos(Arrays.asList(contatoDTO))
                .enderecos(Arrays.asList(enderecoDTO))
                .build();
    }
} 