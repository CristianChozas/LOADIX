package com.loadix.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    public DataSource dataSource(Environment environment) {
        HikariDataSource dataSource = new HikariDataSource();

        String explicitUrl = firstNonBlank(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("JDBC_DATABASE_URL"),
                environment.getProperty("DATABASE_URL")
        );

        if (hasText(explicitUrl)) {
            String jdbcUrl = normalizeJdbcUrl(explicitUrl);
            validateJdbcUrl(jdbcUrl);

            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setJdbcUrl(jdbcUrl);

            String username = firstNonBlank(
                    environment.getProperty("spring.datasource.username"),
                    environment.getProperty("DATABASE_USERNAME"),
                    environment.getProperty("SPRING_DATASOURCE_USERNAME")
            );
            String password = firstNonBlank(
                    environment.getProperty("spring.datasource.password"),
                    environment.getProperty("DATABASE_PASSWORD"),
                    environment.getProperty("SPRING_DATASOURCE_PASSWORD")
            );

            if (hasText(username)) {
                dataSource.setUsername(username);
            }
            if (hasText(password)) {
                dataSource.setPassword(password);
            }

            log.info("database_config mode=explicit jdbc_url={} username_present={} password_present={}",
                    sanitizeJdbcUrl(jdbcUrl), hasText(username), hasText(password));

            return dataSource;
        }

        String fallbackJdbcUrl = "jdbc:postgresql://%s:%s/%s".formatted(
                environment.getProperty("POSTGRES_HOST", "localhost"),
                environment.getProperty("POSTGRES_PORT", "5434"),
                environment.getProperty("POSTGRES_DB", "loadix")
        );

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(fallbackJdbcUrl);
        dataSource.setUsername(environment.getProperty("POSTGRES_USER", "loadix"));
        dataSource.setPassword(environment.getProperty("POSTGRES_PASSWORD", "loadix"));

        log.info("database_config mode=fallback jdbc_url={}", sanitizeJdbcUrl(fallbackJdbcUrl));

        return dataSource;
    }

    private String normalizeJdbcUrl(String rawUrl) {
        String normalized = stripWrappingQuotes(rawUrl.trim());

        if (normalized.startsWith("jdbc:postgresql://")) {
            return normalized;
        }
        if (normalized.startsWith("postgresql://")) {
            return "jdbc:" + normalized;
        }
        if (normalized.startsWith("postgres://")) {
            return "jdbc:" + normalized.replaceFirst("^postgres://", "postgresql://");
        }
        return normalized;
    }

    private void validateJdbcUrl(String jdbcUrl) {
        if (!jdbcUrl.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException(
                    "Invalid JDBC_DATABASE_URL/DATABASE_URL. Expected a PostgreSQL URL like jdbc:postgresql://host:port/database or postgresql://host:port/database"
            );
        }
    }

    private String stripWrappingQuotes(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1).trim();
        }
        return value;
    }

    private String sanitizeJdbcUrl(String jdbcUrl) {
        return jdbcUrl.replaceAll("//([^:/?#]+):([^@]+)@", "//$1:***@");
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
