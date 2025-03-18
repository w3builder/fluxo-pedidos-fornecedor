package com.fluxo.pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fluxo.pedidos.entity.Cliente;
import com.fluxo.pedidos.entity.Revenda;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    List<Cliente> findByRevenda(Revenda revenda);
    
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
    
    Optional<Cliente> findByEmail(String email);
    
    List<Cliente> findByRevendaId(Long revendaId);
} 