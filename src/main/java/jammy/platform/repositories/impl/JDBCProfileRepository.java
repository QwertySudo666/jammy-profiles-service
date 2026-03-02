package jammy.platform.repositories.impl;

import io.agroal.api.AgroalDataSource;
import jakarta.inject.Singleton;
import jammy.platform.mappers.RowMapper;
import jammy.platform.models.Profile;
import jammy.platform.repositories.BaseRepository;
import jammy.platform.repositories.ProfileRepository;
import jammy.platform.responses.PagedResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Singleton
public class JDBCProfileRepository extends BaseRepository implements ProfileRepository {

  public JDBCProfileRepository(AgroalDataSource dataSource) {
    super(dataSource);
  }

  private final RowMapper<Profile> profileMapper =
      rs -> Profile.builder().id(rs.getObject("id", UUID.class)).name(rs.getString("name")).build();

  @Override
  public Profile create(Profile profile) {
    String sql = "INSERT INTO profiles(id, name) VALUES (?, ?) RETURNING id, name";
    // Since create needs a specific 'RETURNING' logic, we keep the try-block here
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setObject(1, profile.getId());
      ps.setString(2, profile.getName());
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return profileMapper.map(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void update(Profile profile) {
    String sql = "UPDATE profiles SET name = ? WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, profile.getName());
      ps.setObject(2, profile.getId());
      int rows = ps.executeUpdate();
      if (rows == 0) throw new NoSuchElementException("Profile not found");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PagedResponse<Profile> findAll(int page, int size) {
    // COUNT(*) OVER() gives us the total count of the unfiltered query
    // in every single row of the result set.
    String sql =
        "SELECT id, name, COUNT(*) OVER() as total_count FROM profiles ORDER BY name LIMIT ? OFFSET ?";
    int offset = page * size;

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, size);
      ps.setInt(2, offset);

      try (ResultSet rs = ps.executeQuery()) {
        List<Profile> profiles = new ArrayList<>();
        long totalCount = 0;

        while (rs.next()) {
          if (totalCount == 0) totalCount = rs.getLong("total_count");
          profiles.add(profileMapper.map(rs));
        }

        return new PagedResponse<>(profiles, totalCount, page, size);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Profile findById(UUID profileId) {
    List<Profile> results =
        query("SELECT id, name FROM profiles WHERE id = ?", profileMapper, profileId);
    return results.isEmpty() ? null : results.get(0);
  }
}
