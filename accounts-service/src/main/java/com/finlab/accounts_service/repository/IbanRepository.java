package com.finlab.accounts_service.repository;

import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IbanRepository {

    private final JdbcTemplate jdbc;

    public IbanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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

        return jdbc.query(sql, ps -> ps.setString(1, iban), rs -> {
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

    public void insertReport(String iban, String reason, String reporterHash) {
        Integer ibanId = jdbc.query("""
            SELECT id FROM iban_accounts WHERE iban = ?
        """, ps -> ps.setString(1, iban), rs -> rs.next() ? rs.getInt("id") : null);

        if (ibanId == null) {
            jdbc.update("INSERT INTO iban_accounts (iban) VALUES (?)", iban);
            ibanId = jdbc.queryForObject("SELECT id FROM iban_accounts WHERE iban = ?", Integer.class, iban);
        }

        jdbc.update("""
            INSERT INTO iban_reports (iban_id, reporter_hash, reason)
            VALUES (?, ?, ?)
        """, ibanId, reporterHash, reason);
    }

    public void insertEdge(String sourceIban, String targetIban) {
        Integer sourceId = ensureIbanExists(sourceIban);
        Integer targetId = ensureIbanExists(targetIban);

        jdbc.update("""
          INSERT INTO iban_edges (source_iban_id, target_iban_id)
          VALUES (?, ?)
          ON CONFLICT DO NOTHING
        """, sourceId, targetId);

        jdbc.update("""
          INSERT INTO iban_edges (source_iban_id, target_iban_id)
          VALUES (?, ?)
          ON CONFLICT DO NOTHING
        """, targetId, sourceId);
    }

    private Integer ensureIbanExists(String iban) {
        Integer id = jdbc.query("""
            SELECT id FROM iban_accounts WHERE iban = ?
        """, ps -> ps.setString(1, iban), rs -> rs.next() ? rs.getInt("id") : null);

        if (id == null) {
            jdbc.update("INSERT INTO iban_accounts (iban) VALUES (?)", iban);
            id = jdbc.queryForObject("SELECT id FROM iban_accounts WHERE iban = ?", Integer.class, iban);
        }
        return id;
    }

    private String decide(double score) {
        if (score >= 10.0) return "BLOCK";
        if (score >= 5.0) return "REVIEW";
        return "ALLOW";
    }
}
