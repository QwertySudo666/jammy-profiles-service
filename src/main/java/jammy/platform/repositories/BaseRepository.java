package jammy.platform.repositories;

import io.agroal.api.AgroalDataSource;
import jammy.platform.mappers.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRepository {
    protected final AgroalDataSource dataSource;

    protected BaseRepository(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Helper for SELECT queries (List)
    protected <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
