package com.fluxo.pedidos.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fluxo.pedidos.dto.request.ContatoDTO;
import com.fluxo.pedidos.dto.request.RevendaDTO;
import com.fluxo.pedidos.dto.response.RevendaResponseDTO;
import com.fluxo.pedidos.entity.Revenda;
import com.fluxo.pedidos.exception.BusinessException;
import com.fluxo.pedidos.exception.ResourceNotFoundException;
import com.fluxo.pedidos.mapper.RevendaMapper;
import com.fluxo.pedidos.repository.RevendaRepository;
import com.fluxo.pedidos.service.RevendaService;
import com.fluxo.pedidos.util.CNPJValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevendaServiceImpl implements RevendaService {
    
    private final RevendaRepository revendaRepository;
    private final RevendaMapper revendaMapper;
    private final CNPJValidator cnpjValidator;
    
    @Override
    @Transactional
    public RevendaResponseDTO criarRevenda(RevendaDTO revendaDTO) {
        // Validações de negócio
        validarRevenda(revendaDTO);
        
        // Converter DTO para entidade e definir relacionamentos
        Revenda revenda = revendaMapper.toEntity(revendaDTO);
        
        // Configurar relacionamentos entre entidades
        if (revenda.getTelefones() != null) {
            revenda.getTelefones().forEach(telefone -> telefone.setRevenda(revenda));
        }
        
        if (revenda.getContatos() != null) {
            revenda.getContatos().forEach(contato -> contato.setRevenda(revenda));
        }
        
        if (revenda.getEnderecos() != null) {
            revenda.getEnderecos().forEach(endereco -> endereco.setRevenda(revenda));
        }
        
        // Salvar no banco de dados
        Revenda revendaSalva = revendaRepository.save(revenda);
        
        // Converter a entidade salva para DTO de resposta
        return revendaMapper.toResponseDTO(revendaSalva);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevendaResponseDTO buscarPorId(Long id) {
        Revenda revenda = revendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revenda não encontrada com id: " + id));
        
        return revendaMapper.toResponseDTO(revenda);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevendaResponseDTO buscarPorCnpj(String cnpj) {
        Revenda revenda = revendaRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Revenda não encontrada com CNPJ: " + cnpj));
        
        return revendaMapper.toResponseDTO(revenda);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RevendaResponseDTO> listarTodas() {
        return revendaRepository.findAll().stream()
                .map(revendaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RevendaResponseDTO atualizarRevenda(Long id, RevendaDTO revendaDTO) {
        Revenda revenda = revendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revenda não encontrada com id: " + id));
        
        // Validações específicas para atualização
        if (!revenda.getCnpj().equals(revendaDTO.getCnpj())) {
            throw new BusinessException("O CNPJ não pode ser alterado");
        }
        
        // Atualizar a entidade
        revendaMapper.updateEntity(revenda, revendaDTO);
        
        // Reconfigurar relacionamentos
        if (revenda.getTelefones() != null) {
            revenda.getTelefones().forEach(telefone -> telefone.setRevenda(revenda));
        }
        
        if (revenda.getContatos() != null) {
            revenda.getContatos().forEach(contato -> contato.setRevenda(revenda));
        }
        
        if (revenda.getEnderecos() != null) {
            revenda.getEnderecos().forEach(endereco -> endereco.setRevenda(revenda));
        }
        
        // Salvar no banco de dados
        Revenda revendaAtualizada = revendaRepository.save(revenda);
        
        // Converter a entidade atualizada para DTO de resposta
        return revendaMapper.toResponseDTO(revendaAtualizada);
    }
    
    @Override
    @Transactional
    public void deletarRevenda(Long id) {
        if (!revendaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Revenda não encontrada com id: " + id);
        }
        
        revendaRepository.deleteById(id);
    }
    
    private void validarRevenda(RevendaDTO revendaDTO) {
        // Validar CNPJ
        if (!cnpjValidator.isValid(revendaDTO.getCnpj())) {
            throw new BusinessException("CNPJ inválido");
        }
        
        // Verificar se já existe uma revenda com o mesmo CNPJ
        if (revendaRepository.existsByCnpj(revendaDTO.getCnpj())) {
            throw new BusinessException("Já existe uma revenda cadastrada com este CNPJ");
        }
        
        // Verificar se já existe uma revenda com o mesmo email
        if (revendaRepository.existsByEmail(revendaDTO.getEmail())) {
            throw new BusinessException("Já existe uma revenda cadastrada com este email");
        }
        
        // Verificar se há pelo menos um contato principal
        boolean temContatoPrincipal = revendaDTO.getContatos().stream()
                .anyMatch(ContatoDTO::isPrincipal);
        
        if (!temContatoPrincipal) {
            throw new BusinessException("Deve haver pelo menos um contato principal");
        }
    }
} 