package com.finlab.accounts_service.repository;

import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IbanRepository {

    private final JdbcTemplate jdbcTemplate;

    public IbanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public IbanCheckResponse findRiskForIban(String iban) {
        var sql = """
            WITH ib AS (
              SELECT id FROM iban_accounts WHERE iban = ?
            ),
            direct_reports AS (
              SELECT COUNT(*)::int AS cnt
              FROM iban_reports r
              JOIN ib ON r.iban_id = ib.id
            ),
            neighbor_reports AS (
              SELECT COUNT(*)::int AS cnt
              FROM iban_edges e
              JOIN iban_reports nr ON nr.iban_id = e.target_iban_id
              JOIN ib ON e.source_iban_id = ib.id
            )
            SELECT
              (SELECT id FROM ib) AS iban_id,
              COALESCE((SELECT cnt FROM direct_reports), 0) AS direct_cnt,
              COALESCE((SELECT cnt FROM neighbor_reports), 0) AS neighbor_cnt;
        """;

        return jdbcTemplate.query(sql, ps -> ps.setString(1, iban), rs -> {
            if (!rs.next() || rs.getObject("iban_id") == null) {
                return new IbanCheckResponse(iban, "ALLOW", 0.0, List.of("IBAN not found"));
            }

            int direct = rs.getInt("direct_cnt");
            int neighbor = rs.getInt("neighbor_cnt");

            double score = direct * 1.0 + 0.5 * neighbor;
            String decision = decide(score);
            List<String> reasons = List.of("reports=" + direct, "neighbors=" + neighbor);

            return new IbanCheckResponse(iban, decision, score, reasons);
        });
    }

    private String decide(double score) {
        if (score >= 10.0) return "BLOCK";
        if (score >= 5.0) return "REVIEW";
        return "ALLOW";
    }
}
