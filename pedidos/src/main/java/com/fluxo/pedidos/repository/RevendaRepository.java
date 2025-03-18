package com.fluxo.pedidos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fluxo.pedidos.entity.Revenda;

@Repository
public interface RevendaRepository extends JpaRepository<Revenda, Long> {
    
    Optional<Revenda> findByCnpj(String cnpj);
    
    Optional<Revenda> findByEmail(String email);
    
    Optional<Revenda> findByCnpjAndIdNot(String cnpj, Long id);
    
    Optional<Revenda> findByEmailAndIdNot(String email, Long id);
    
    boolean existsByCnpj(String cnpj);
    
    boolean existsByEmail(String email);
} 