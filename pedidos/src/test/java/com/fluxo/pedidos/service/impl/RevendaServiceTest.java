package com.fluxo.pedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fluxo.pedidos.dto.request.ContatoDTO;
import com.fluxo.pedidos.dto.request.EnderecoDTO;
import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.request.TelefoneDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;
import com.fluxo.pedidos.exception.BusinessException;
import com.fluxo.pedidos.exception.ResourceNotFoundException;
import com.fluxo.pedidos.service.RevendaService;

@ExtendWith(MockitoExtension.class)
public class RevendaServiceTest {

    @Mock
    private RevendaService revendaService;

    private RevendaDTO revendaDTO;
    private RevendaResponseDTO responseDTO;
    private final Long TEST_ID = 1L;
    private final String TEST_CNPJ = "12.345.678/0001-99";
    private final String TEST_EMAIL = "contato@empresateste.com.br";

    @BeforeEach
    void setUp() {
        // Setup test data
        revendaDTO = createRevendaDTO();
        responseDTO = createRevendaResponseDTO();
    }

    @Test
    @DisplayName("Deve criar uma revenda com sucesso")
    void deveCriarRevendaComSucesso() {
        // Arrange
        when(revendaService.criarRevenda(any(RevendaDTO.class))).thenReturn(responseDTO);

        // Act
        RevendaResponseDTO result = revendaService.criarRevenda(revendaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_CNPJ, result.getCnpj());
        
        // Verify
        verify(revendaService, times(1)).criarRevenda(revendaDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar revenda com CNPJ inválido")
    void deveLancarExcecaoCriarRevendaCnpjInvalido() {
        // Arrange
        when(revendaService.criarRevenda(any(RevendaDTO.class)))
            .thenThrow(new BusinessException("CNPJ inválido"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> revendaService.criarRevenda(revendaDTO));
        
        assertEquals("CNPJ inválido", exception.getMessage());
        
        // Verify
        verify(revendaService, times(1)).criarRevenda(revendaDTO);
    }

    @Test
    @DisplayName("Deve buscar revenda por ID com sucesso")
    void deveBuscarRevendaPorIdComSucesso() {
        // Arrange
        when(revendaService.buscarPorId(anyLong())).thenReturn(responseDTO);

        // Act
        RevendaResponseDTO result = revendaService.buscarPorId(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_CNPJ, result.getCnpj());
        
        // Verify
        verify(revendaService, times(1)).buscarPorId(TEST_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar revenda com ID inexistente")
    void deveLancarExcecaoBuscarRevendaIdInexistente() {
        // Arrange
        when(revendaService.buscarPorId(anyLong()))
            .thenThrow(new ResourceNotFoundException("Revenda não encontrada com id: " + TEST_ID));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> revendaService.buscarPorId(TEST_ID));
        
        assertEquals("Revenda não encontrada com id: " + TEST_ID, exception.getMessage());
        
        // Verify
        verify(revendaService, times(1)).buscarPorId(TEST_ID);
    }

    @Test
    @DisplayName("Deve buscar revenda por CNPJ com sucesso")
    void deveBuscarRevendaPorCnpjComSucesso() {
        // Arrange
        when(revendaService.buscarPorCnpj(anyString())).thenReturn(responseDTO);

        // Act
        RevendaResponseDTO result = revendaService.buscarPorCnpj(TEST_CNPJ);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_CNPJ, result.getCnpj());
        
        // Verify
        verify(revendaService, times(1)).buscarPorCnpj(TEST_CNPJ);
    }

    @Test
    @DisplayName("Deve listar todas as revendas com sucesso")
    void deveListarTodasRevendasComSucesso() {
        // Arrange
        RevendaResponseDTO responseDTO2 = createRevendaResponseDTO2();
        when(revendaService.listarTodas()).thenReturn(Arrays.asList(responseDTO, responseDTO2));

        // Act
        List<RevendaResponseDTO> result = revendaService.listarTodas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        
        // Verify
        verify(revendaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve atualizar revenda com sucesso")
    void deveAtualizarRevendaComSucesso() {
        // Arrange
        when(revendaService.atualizarRevenda(anyLong(), any(RevendaDTO.class))).thenReturn(responseDTO);

        // Act
        RevendaResponseDTO result = revendaService.atualizarRevenda(TEST_ID, revendaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_CNPJ, result.getCnpj());
        
        // Verify
        verify(revendaService, times(1)).atualizarRevenda(TEST_ID, revendaDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar revenda com ID inexistente")
    void deveLancarExcecaoAtualizarRevendaIdInexistente() {
        // Arrange
        when(revendaService.atualizarRevenda(anyLong(), any(RevendaDTO.class)))
            .thenThrow(new ResourceNotFoundException("Revenda não encontrada com id: " + TEST_ID));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> revendaService.atualizarRevenda(TEST_ID, revendaDTO));
        
        assertEquals("Revenda não encontrada com id: " + TEST_ID, exception.getMessage());
        
        // Verify
        verify(revendaService, times(1)).atualizarRevenda(TEST_ID, revendaDTO);
    }

    @Test
    @DisplayName("Deve excluir revenda com sucesso")
    void deveExcluirRevendaComSucesso() {
        // Arrange
        doNothing().when(revendaService).deletarRevenda(anyLong());

        // Act
        revendaService.deletarRevenda(TEST_ID);
        
        // Verify
        verify(revendaService, times(1)).deletarRevenda(TEST_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir revenda com ID inexistente")
    void deveLancarExcecaoExcluirRevendaIdInexistente() {
        // Arrange
        doThrow(new ResourceNotFoundException("Revenda não encontrada com id: " + TEST_ID))
            .when(revendaService).deletarRevenda(anyLong());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> revendaService.deletarRevenda(TEST_ID));
        
        assertEquals("Revenda não encontrada com id: " + TEST_ID, exception.getMessage());
        
        // Verify
        verify(revendaService, times(1)).deletarRevenda(TEST_ID);
    }

    // Helper methods for creating test objects
    
    private RevendaDTO createRevendaDTO() {
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
                .cnpj(TEST_CNPJ)
                .razaoSocial("Empresa Teste LTDA")
                .nomeFantasia("Empresa Teste")
                .email(TEST_EMAIL)
                .telefones(Arrays.asList(telefoneDTO))
                .contatos(Arrays.asList(contatoDTO))
                .enderecos(Arrays.asList(enderecoDTO))
                .build();
    }
    
    private RevendaResponseDTO createRevendaResponseDTO() {
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
                .id(TEST_ID)
                .cnpj(TEST_CNPJ)
                .razaoSocial("Empresa Teste LTDA")
                .nomeFantasia("Empresa Teste")
                .email(TEST_EMAIL)
                .telefones(Arrays.asList(telefoneDTO))
                .contatos(Arrays.asList(contatoDTO))
                .enderecos(Arrays.asList(enderecoDTO))
                .build();
    }
    
    private RevendaResponseDTO createRevendaResponseDTO2() {
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