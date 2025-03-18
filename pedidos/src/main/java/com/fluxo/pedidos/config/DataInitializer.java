package com.fluxo.pedidos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod") // Não executa em produção
public class DataInitializer {
    // Removido para usar o data.sql em vez disso
} 