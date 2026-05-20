package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Client;
import java.util.List;
import java.util.Optional;

public interface IClientRepository {

    // ACTIVE clients
    List<Client> getAllActive();

    // DELETED clients
    List<Client> getAllDeleted();


    List<Client> getAll();

    Optional<Client> findById(int id);

    boolean insert(Client client);

    boolean update(Client client);


    boolean delete(int id);

    // Soft delete
    boolean softDelete(int id);

    // Restore
    boolean restore(int id);
}
