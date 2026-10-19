package com.agilis.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testa que o contexto Spring sobe corretamente com as configurações da aplicação.
 * Utiliza o profile "test" que configura H2 em memória e desabilita Flyway e conexão real.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

    @Test
    void contextLoads() {
        // Valida que todos os beans são criados e injetados sem erros.
    }
}
