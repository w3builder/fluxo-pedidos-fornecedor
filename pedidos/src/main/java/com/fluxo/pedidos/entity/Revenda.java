package com.fluxo.pedidos.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "revendas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Revenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false, name = "razao_social")
    private String razaoSocial;

    @Column(nullable = false, name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "revenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones;

    @OneToMany(mappedBy = "revenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contato> contatos;

    @OneToMany(mappedBy = "revenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos;
} 