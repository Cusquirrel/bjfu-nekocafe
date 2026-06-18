package com.bjfu.nekocafe.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class PostgresReservationTestcontainersIT {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("nekocafe_it")
            .withUsername("nekocafe")
            .withPassword("nekocafe");

    @Autowired private DataSource dataSource;
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.sql.init.mode", new java.util.function.Supplier<Object>() {
            @Override public Object get() { return "never"; }
        });
    }

    @BeforeEach
    public void loadSchemaAndSeedData() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new FileSystemResource("../db/init/001_schema.sql"));
            ScriptUtils.executeSqlScript(connection, new FileSystemResource("../db/init/002_seed.sql"));
        }
    }

    @Test
    public void shouldRunReservationFlowAgainstRealPostgresContainer() throws Exception {
        String tomorrow = LocalDate.now().plusDays(16).toString();
        String body = "{\"userId\":1,\"storeId\":1,\"visitDate\":\"" + tomorrow
                + "\",\"slot\":\"18:00-20:00\",\"partySize\":2,\"requestId\":\"tc-postgres-reservation\"}";

        mvc.perform(post("/api/reservations")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
        mvc.perform(get("/api/stores/1/slots?date=" + tomorrow))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThan(0)));

        Integer auditCount = jdbc.queryForObject("SELECT COUNT(*) FROM audit_logs WHERE action='CREATE_RESERVATION'", Integer.class);
        org.junit.jupiter.api.Assertions.assertTrue(auditCount.intValue() >= 1);
    }
}
