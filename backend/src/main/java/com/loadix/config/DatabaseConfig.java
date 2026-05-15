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

        boolean testProfile = isTestProfile(environment);
        String explicitUrl = testProfile
                ? firstNonBlank(
                    environment.getProperty("spring.datasource.url"),
                    environment.getProperty("DATABASE_URL")
                )
                : environment.getProperty("DATABASE_URL");

        if (hasText(explicitUrl)) {
            String jdbcUrl = normalizeJdbcUrl(explicitUrl);
            validateJdbcUrl(jdbcUrl);
            validateSupabaseRuntimeDatabase(jdbcUrl, testProfile);

            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setJdbcUrl(jdbcUrl);

            String username = firstNonBlank(
                    testProfile ? environment.getProperty("spring.datasource.username") : null,
                    environment.getProperty("DATABASE_USERNAME")
            );
            String password = firstNonBlank(
                    testProfile ? environment.getProperty("spring.datasource.password") : null,
                    environment.getProperty("DATABASE_PASSWORD")
            );

            if (hasText(username)) {
                dataSource.setUsername(username);
            }
            if (hasText(password)) {
                dataSource.setPassword(password);
            }

            log.info("database_config mode=explicit profile_test={} jdbc_url={} username_present={} password_present={}",
                    testProfile, sanitizeJdbcUrl(jdbcUrl), hasText(username), hasText(password));

            return dataSource;
        }

        throw new IllegalStateException(
                "No database URL configured. Set DATABASE_URL to the Supabase PostgreSQL JDBC URL."
        );
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
                    "Invalid DATABASE_URL. Expected a PostgreSQL URL like jdbc:postgresql://host:port/database or postgresql://host:port/database"
            );
        }
    }

    private void validateSupabaseRuntimeDatabase(String jdbcUrl, boolean testProfile) {
        if (testProfile) {
            return;
        }

        String lower = jdbcUrl.toLowerCase();
        if (lower.contains("//localhost:")
                || lower.contains("//127.0.0.1:")
                || lower.contains("//host.docker.internal:")
                || lower.contains("//loadix-postgres:")) {
            throw new IllegalStateException(
                    "Refusing to use local PostgreSQL for application runtime. Set DATABASE_URL to Supabase PostgreSQL."
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

    private boolean isTestProfile(Environment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if ("test".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
