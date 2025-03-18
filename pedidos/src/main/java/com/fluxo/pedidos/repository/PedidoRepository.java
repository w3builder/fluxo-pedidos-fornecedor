package com.fluxo.pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fluxo.pedidos.entity.Pedido;
import com.fluxo.pedidos.entity.Revenda;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByRevenda(Revenda revenda);
    
    Optional<Pedido> findByNumero(String numero);
    
    List<Pedido> findByRevendaId(Long revendaId);
    
    List<Pedido> findByClienteId(Long clienteId);
} 