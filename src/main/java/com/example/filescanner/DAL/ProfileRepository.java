package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Profile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileRepository implements IProfileRepository {

    @Override
    public List<Profile> getAll() {
        List<Profile> list = new ArrayList<>();
        String sql = "SELECT * FROM Profiles";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Optional<Profile> findById(int id) {
        String sql = "SELECT * FROM Profiles WHERE ProfileId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean insert(Profile p) {
        String sql = """
                INSERT INTO Profiles (Name, Rotation, Brightness, Contrast, SplitOnBarcode, ExportFormat)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getRotation());
            stmt.setFloat(3, p.getBrightness());
            stmt.setFloat(4, p.getContrast());
            stmt.setBoolean(5, p.isSplitOnBarcode());
            stmt.setString(6, p.getExportFormat());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Profile p) {
        String sql = """
                UPDATE Profiles
                SET Name = ?, Rotation = ?, Brightness = ?, Contrast = ?, SplitOnBarcode = ?, ExportFormat = ?
                WHERE ProfileId = ?
                """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setInt(2, p.getRotation());
            stmt.setFloat(3, p.getBrightness());
            stmt.setFloat(4, p.getContrast());
            stmt.setBoolean(5, p.isSplitOnBarcode());
            stmt.setString(6, p.getExportFormat());
            stmt.setInt(7, p.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Profiles WHERE ProfileId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean assignProfileToUser(int profileId, String userId) {
        String sql = "INSERT INTO UserProfiles (UserId, ProfileId) VALUES (?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setInt(2, profileId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Profile> getProfilesForUser(String userId) {
        List<Profile> list = new ArrayList<>();

        String sql = """
                SELECT p.*
                FROM Profiles p
                JOIN UserProfiles up ON p.ProfileId = up.ProfileId
                WHERE up.UserId = ?
                """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ---------------------------
    //  PRIVATE MAPPER
    // ---------------------------

    private Profile mapRow(ResultSet rs) throws SQLException {
        return new Profile(
                rs.getInt("ProfileId"),
                rs.getString("Name"),
                rs.getInt("Rotation"),
                rs.getFloat("Brightness"),
                rs.getFloat("Contrast"),
                rs.getBoolean("SplitOnBarcode"),
                rs.getString("ExportFormat")
        );
    }
}
