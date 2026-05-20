package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements IClientRepository {

    // HENT ALLE AKTIVE CLIENTS
    @Override
    public List<Client> getAllActive() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM Clients WHERE IsDeleted = 0";

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

    // HENT ALLE SLETTEDE CLIENTS
    @Override
    public List<Client> getAllDeleted() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM Clients WHERE IsDeleted = 1";

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

    // DENNE BRUGES IKKE LÆNGERE (men skal være her pga. interface)
    @Override
    public List<Client> getAll() {
        return getAllActive();
    }

    // FIND CLIENT BY ID
    @Override
    public Optional<Client> findById(int id) {
        String sql = "SELECT * FROM Clients WHERE Id = ?";

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

    // INSERT
    @Override
    public boolean insert(Client c) {
        String sql = "INSERT INTO Clients (CompanyName) VALUES (?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getCompanyName());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // UPDATE
    @Override
    public boolean update(Client c) {
        String sql = "UPDATE Clients SET CompanyName = ? WHERE Id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getCompanyName());
            stmt.setInt(2, c.getId());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // HARD DELETE (BRUGES IKKE, MEN SKAL VÆRE HER)
    @Override
    public boolean delete(int id) {
        return false; // vi bruger softDelete i stedet
    }

    // SOFT DELETE
    @Override
    public boolean softDelete(int id) {
        String sql = "UPDATE Clients SET IsDeleted = 1 WHERE Id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // RESTORE
    @Override
    public boolean restore(int id) {
        String sql = "UPDATE Clients SET IsDeleted = 0 WHERE Id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // MAP ROW
    private Client mapRow(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("Id"),
                rs.getString("CompanyName")
        );
    }
}
