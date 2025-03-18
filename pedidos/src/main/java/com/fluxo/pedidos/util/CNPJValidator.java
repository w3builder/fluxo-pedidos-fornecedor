package com.fluxo.pedidos.util;

import org.springframework.stereotype.Component;

@Component
public class CNPJValidator {
    
    public boolean isValid(String cnpj) {
        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");
        
        // Verifica o tamanho
        if (cnpj.length() != 14) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        // Cálculo do primeiro dígito verificador
        int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;
        
        // Verifica o primeiro dígito verificador
        if (dv1 != (cnpj.charAt(12) - '0')) {
            return false;
        }
        
        // Cálculo do segundo dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;
        
        // Verifica o segundo dígito verificador
        return dv2 == (cnpj.charAt(13) - '0');
    }
    
    /**
     * Formata um CNPJ numérico para o formato padrão XX.XXX.XXX/XXXX-XX
     * @param cnpj CNPJ no formato numérico ou já formatado
     * @return CNPJ formatado
     */
    public String formatCnpj(String cnpj) {
        // If CNPJ is already formatted, return as is
        if (cnpj.contains(".") || cnpj.contains("/") || cnpj.contains("-")) {
            return cnpj;
        }
        
        // Format numeric CNPJ to XX.XXX.XXX/XXXX-XX
        if (cnpj.length() == 14) {
            return cnpj.substring(0, 2) + "." + 
                   cnpj.substring(2, 5) + "." + 
                   cnpj.substring(5, 8) + "/" + 
                   cnpj.substring(8, 12) + "-" + 
                   cnpj.substring(12, 14);
        }
        
        // Return original if unexpected format
        return cnpj;
    }
} 