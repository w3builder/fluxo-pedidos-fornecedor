package com.fluxo.pedidos.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fluxo.pedidos.dto.request.ItemPedidoDTO;
import com.fluxo.pedidos.dto.request.PedidoDTO;
import com.fluxo.pedidos.dto.response.ClienteResponseDTO;
import com.fluxo.pedidos.dto.response.ItemPedidoResponseDTO;
import com.fluxo.pedidos.dto.response.PedidoResponseDTO;
import com.fluxo.pedidos.dto.response.ProdutoResponseDTO;
import com.fluxo.pedidos.entity.StatusPedido;
import com.fluxo.pedidos.exception.BusinessException;
import com.fluxo.pedidos.exception.ResourceNotFoundException;
import com.fluxo.pedidos.service.PedidoService;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    private ObjectMapper objectMapper;
    private PedidoDTO pedidoDTO;
    private PedidoResponseDTO responseDTO;
    private PedidoResponseDTO responseDTO2;
    
    private final Long TEST_REVENDA_ID = 1L;
    private final Long TEST_PEDIDO_ID = 1L;
    private final Long TEST_CLIENTE_ID = 1L;
    private final String TEST_NUMERO_PEDIDO = "PED20230715123456789";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime serialization
        
        // Setup test data
        pedidoDTO = createPedidoDTO();
        responseDTO = createPedidoResponseDTO();
        responseDTO2 = createPedidoResponseDTO2();
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso")
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        when(pedidoService.criarPedido(anyLong(), any(PedidoDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/revendas/{revendaId}/pedidos", TEST_REVENDA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(TEST_PEDIDO_ID.intValue())))
                .andExpect(jsonPath("$.numero", is(TEST_NUMERO_PEDIDO)))
                .andExpect(jsonPath("$.cliente.id", is(TEST_CLIENTE_ID.intValue())));
        
        // Verify
        verify(pedidoService, times(1)).criarPedido(TEST_REVENDA_ID, pedidoDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao criar pedido com dados inválidos")
    void deveRetornarErroCriarPedidoDadosInvalidos() throws Exception {
        // Arrange - pedidoDTO without required fields
        PedidoDTO invalidPedidoDTO = new PedidoDTO();

        // Act & Assert
        mockMvc.perform(post("/api/revendas/{revendaId}/pedidos", TEST_REVENDA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPedidoDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro ao criar pedido com revenda inexistente")
    void deveRetornarErroCriarPedidoRevendaInexistente() throws Exception {
        // Arrange
        when(pedidoService.criarPedido(anyLong(), any(PedidoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Revenda não encontrada com id: " + TEST_REVENDA_ID));

        // Act & Assert
        mockMvc.perform(post("/api/revendas/{revendaId}/pedidos", TEST_REVENDA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(anyLong())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos/{id}", TEST_REVENDA_ID, TEST_PEDIDO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(TEST_PEDIDO_ID.intValue())))
                .andExpect(jsonPath("$.numero", is(TEST_NUMERO_PEDIDO)));
        
        // Verify
        verify(pedidoService, times(1)).buscarPorId(TEST_PEDIDO_ID);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar pedido com ID inexistente")
    void deveRetornarErroBuscarPedidoIdInexistente() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(anyLong()))
                .thenThrow(new ResourceNotFoundException("Pedido não encontrado com id: " + TEST_PEDIDO_ID));

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos/{id}", TEST_REVENDA_ID, TEST_PEDIDO_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar pedido de outra revenda")
    void deveRetornarErroBuscarPedidoOutraRevenda() throws Exception {
        // Arrange
        // Create a pedido that belongs to a different revenda (ID = 2)
        PedidoResponseDTO otherRevendaPedido = responseDTO;
        otherRevendaPedido.getCliente().setRevendaId(2L);
        
        when(pedidoService.buscarPorId(anyLong())).thenReturn(otherRevendaPedido);

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos/{id}", TEST_REVENDA_ID, TEST_PEDIDO_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve buscar pedido por número com sucesso")
    void deveBuscarPedidoPorNumeroComSucesso() throws Exception {
        // Arrange
        when(pedidoService.buscarPorNumero(anyString())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos/numero/{numero}", TEST_REVENDA_ID, TEST_NUMERO_PEDIDO))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(TEST_PEDIDO_ID.intValue())))
                .andExpect(jsonPath("$.numero", is(TEST_NUMERO_PEDIDO)));
        
        // Verify
        verify(pedidoService, times(1)).buscarPorNumero(TEST_NUMERO_PEDIDO);
    }

    @Test
    @DisplayName("Deve listar todos pedidos da revenda com sucesso")
    void deveListarTodosPedidosRevendaComSucesso() throws Exception {
        // Arrange
        List<PedidoResponseDTO> pedidos = Arrays.asList(responseDTO, responseDTO2);
        when(pedidoService.listarPorRevenda(anyLong())).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos", TEST_REVENDA_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(TEST_PEDIDO_ID.intValue())))
                .andExpect(jsonPath("$[1].id", is(2)));
        
        // Verify
        verify(pedidoService, times(1)).listarPorRevenda(TEST_REVENDA_ID);
    }

    @Test
    @DisplayName("Deve listar pedidos do cliente com sucesso")
    void deveListarPedidosClienteComSucesso() throws Exception {
        // Arrange
        List<PedidoResponseDTO> pedidos = Arrays.asList(responseDTO);
        when(pedidoService.listarPorCliente(anyLong())).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/api/revendas/{revendaId}/pedidos/cliente/{clienteId}", 
                TEST_REVENDA_ID, TEST_CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(TEST_PEDIDO_ID.intValue())));
        
        // Verify
        verify(pedidoService, times(1)).listarPorCliente(TEST_CLIENTE_ID);
    }

    @Test
    @DisplayName("Deve cancelar pedido com sucesso")
    void deveCancelarPedidoComSucesso() throws Exception {
        // Arrange
        PedidoResponseDTO canceledPedido = responseDTO;
        canceledPedido.setStatus(StatusPedido.CANCELADO);
        
        when(pedidoService.buscarPorId(anyLong())).thenReturn(responseDTO);
        when(pedidoService.cancelarPedido(anyLong())).thenReturn(canceledPedido);

        // Act & Assert
        mockMvc.perform(put("/api/revendas/{revendaId}/pedidos/{id}/cancelar", 
                TEST_REVENDA_ID, TEST_PEDIDO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(TEST_PEDIDO_ID.intValue())))
                .andExpect(jsonPath("$.status", is("CANCELADO")));
        
        // Verify
        verify(pedidoService, times(1)).buscarPorId(TEST_PEDIDO_ID);
        verify(pedidoService, times(1)).cancelarPedido(TEST_PEDIDO_ID);
    }

    @Test
    @DisplayName("Deve retornar erro ao cancelar pedido já concluído")
    void deveRetornarErroCancelarPedidoConcluido() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(anyLong())).thenReturn(responseDTO);
        doThrow(new BusinessException("Não é possível cancelar um pedido já concluído"))
            .when(pedidoService).cancelarPedido(anyLong());

        // Act & Assert
        mockMvc.perform(put("/api/revendas/{revendaId}/pedidos/{id}/cancelar", 
                TEST_REVENDA_ID, TEST_PEDIDO_ID))
                .andExpect(status().isBadRequest());
    }

    // Helper methods for creating test objects
    
    private PedidoDTO createPedidoDTO() {
        ItemPedidoDTO itemDTO = ItemPedidoDTO.builder()
                .produtoId(1L)
                .quantidade(2)
                .build();
        
        return PedidoDTO.builder()
                .clienteId(TEST_CLIENTE_ID)
                .itens(Arrays.asList(itemDTO))
                .build();
    }
    
    private PedidoResponseDTO createPedidoResponseDTO() {
        ClienteResponseDTO clienteDTO = ClienteResponseDTO.builder()
                .id(TEST_CLIENTE_ID)
                .nome("Cliente Teste")
                .cpfCnpj("123.456.789-00")
                .email("cliente@teste.com")
                .telefone("(11) 99999-8888")
                .revendaId(TEST_REVENDA_ID)
                .build();
        
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.builder()
                .id(1L)
                .codigo("PROD-001")
                .nome("Produto Teste")
                .descricao("Descrição do produto teste")
                .preco(new BigDecimal("100.00"))
                .build();
        
        ItemPedidoResponseDTO itemDTO = ItemPedidoResponseDTO.builder()
                .id(1L)
                .produto(produtoDTO)
                .quantidade(2)
                .precoUnitario(new BigDecimal("100.00"))
                .valorTotal(new BigDecimal("200.00"))
                .build();
        
        return PedidoResponseDTO.builder()
                .id(TEST_PEDIDO_ID)
                .numero(TEST_NUMERO_PEDIDO)
                .cliente(clienteDTO)
                .dataHora(LocalDateTime.now())
                .status(StatusPedido.PENDENTE)
                .valorTotal(new BigDecimal("200.00"))
                .itens(Arrays.asList(itemDTO))
                .build();
    }
    
    private PedidoResponseDTO createPedidoResponseDTO2() {
        ClienteResponseDTO clienteDTO = ClienteResponseDTO.builder()
                .id(2L)
                .nome("Outro Cliente")
                .cpfCnpj("987.654.321-00")
                .email("outro@cliente.com")
                .telefone("(21) 98888-7777")
                .revendaId(TEST_REVENDA_ID)
                .build();
        
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.builder()
                .id(2L)
                .codigo("PROD-002")
                .nome("Outro Produto")
                .descricao("Descrição do outro produto")
                .preco(new BigDecimal("50.00"))
                .build();
        
        ItemPedidoResponseDTO itemDTO = ItemPedidoResponseDTO.builder()
                .id(2L)
                .produto(produtoDTO)
                .quantidade(3)
                .precoUnitario(new BigDecimal("50.00"))
                .valorTotal(new BigDecimal("150.00"))
                .build();
        
        return PedidoResponseDTO.builder()
                .id(2L)
                .numero("PED20230716123456789")
                .cliente(clienteDTO)
                .dataHora(LocalDateTime.now())
                .status(StatusPedido.PENDENTE)
                .valorTotal(new BigDecimal("150.00"))
                .itens(Arrays.asList(itemDTO))
                .build();
    }
} 