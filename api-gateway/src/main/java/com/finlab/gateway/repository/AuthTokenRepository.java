package com.finlab.gateway.repository;

import com.finlab.gateway.model.AuthToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(AuthToken authToken) {
        String sql = """
            INSERT INTO auth_tokens (user_id, jwt_id, token, issued_at, expires_at, revoked)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                authToken.getUserId(),
                authToken.getJwtId(),
                authToken.getToken(),
                Timestamp.from(authToken.getIssuedAt()),
                Timestamp.from(authToken.getExpiresAt()),
                authToken.isRevoked()
        );
    }

    public Optional<AuthToken> findByJwtId(String jwtId) {
        String sql = "SELECT * FROM auth_tokens WHERE jwt_id = ?";

        List<AuthToken> tokens = jdbcTemplate.query(sql, this::mapRow, jwtId);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    public List<AuthToken> findAllActiveByUserId(UUID userId) {
        String sql = """
            SELECT * FROM auth_tokens
            WHERE user_id = ? AND revoked = FALSE AND expires_at > NOW()
        """;
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    public void revokeToken(String jwtId) {
        String sql = "UPDATE auth_tokens SET revoked = TRUE WHERE jwt_id = ?";
        jdbcTemplate.update(sql, jwtId);
    }

    private AuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthToken t = new AuthToken();
        t.setId(UUID.fromString(rs.getString("id")));
        t.setUserId(UUID.fromString(rs.getString("user_id")));
        t.setJwtId(rs.getString("jwt_id"));
        t.setToken(rs.getString("token"));
        t.setIssuedAt(rs.getTimestamp("issued_at").toInstant());
        t.setExpiresAt(rs.getTimestamp("expires_at").toInstant());
        t.setRevoked(rs.getBoolean("revoked"));
        return t;
    }
}
