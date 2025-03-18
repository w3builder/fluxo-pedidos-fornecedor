package com.fluxo.pedidos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;
import com.fluxo.pedidos.service.RevendaService;
import com.fluxo.pedidos.util.CNPJValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/revendas")
@RequiredArgsConstructor
@Tag(name = "Revendas", description = "API para gerenciamento de revendas")
public class RevendaController {
    
    private final RevendaService revendaService;
    private final CNPJValidator cnpjValidator;
    
    @PostMapping
    @Operation(summary = "Criar uma nova revenda", description = "Cria um novo registro de revenda com todos os dados necessários")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Revenda criada com sucesso", 
                     content = @Content(schema = @Schema(implementation = RevendaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "409", description = "Revenda já existe com o CNPJ ou email fornecido")
    })
    public ResponseEntity<RevendaResponseDTO> criarRevenda(@Valid @RequestBody RevendaDTO revendaDTO) {
        RevendaResponseDTO novaRevenda = revendaService.criarRevenda(revendaDTO);
        return new ResponseEntity<>(novaRevenda, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar revenda por ID", description = "Retorna uma revenda única pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenda encontrada", 
                     content = @Content(schema = @Schema(implementation = RevendaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Revenda não encontrada")
    })
    public ResponseEntity<RevendaResponseDTO> buscarPorId(
            @Parameter(description = "ID da revenda", required = true) @PathVariable Long id) {
        RevendaResponseDTO revenda = revendaService.buscarPorId(id);
        return ResponseEntity.ok(revenda);
    }
    
    @GetMapping("/cnpj/{cnpj}")
    @Operation(summary = "Buscar revenda por CNPJ", description = "Retorna uma revenda única pelo seu CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenda encontrada", 
                     content = @Content(schema = @Schema(implementation = RevendaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Revenda não encontrada")
    })
    public ResponseEntity<RevendaResponseDTO> buscarPorCnpj(
            @Parameter(description = "CNPJ da revenda (formato numérico ou XX.XXX.XXX/XXXX-XX)", required = true) 
            @PathVariable String cnpj) {
        // Format CNPJ if it's in numeric format
        String formattedCnpj = cnpjValidator.formatCnpj(cnpj);
        RevendaResponseDTO revenda = revendaService.buscarPorCnpj(formattedCnpj);
        return ResponseEntity.ok(revenda);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as revendas", description = "Retorna uma lista com todas as revendas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de revendas recuperada com sucesso")
    public ResponseEntity<List<RevendaResponseDTO>> listarTodas() {
        List<RevendaResponseDTO> revendas = revendaService.listarTodas();
        return ResponseEntity.ok(revendas);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma revenda", description = "Atualiza os dados de uma revenda existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenda atualizada com sucesso", 
                     content = @Content(schema = @Schema(implementation = RevendaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Revenda não encontrada")
    })
    public ResponseEntity<RevendaResponseDTO> atualizarRevenda(
            @Parameter(description = "ID da revenda", required = true) @PathVariable Long id,
            @Valid @RequestBody RevendaDTO revendaDTO) {
        RevendaResponseDTO revendaAtualizada = revendaService.atualizarRevenda(id, revendaDTO);
        return ResponseEntity.ok(revendaAtualizada);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma revenda", description = "Remove uma revenda existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Revenda excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Revenda não encontrada")
    })
    public ResponseEntity<Void> deletarRevenda(
            @Parameter(description = "ID da revenda", required = true) @PathVariable Long id) {
        revendaService.deletarRevenda(id);
        return ResponseEntity.noContent().build();
    }
} 