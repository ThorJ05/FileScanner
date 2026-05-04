package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Client;
import com.example.filescanner.BEE.Profile;

import java.util.List;
import java.util.Optional;

public interface IClientRepository {

    List<Client> getAll();

    Optional<Client> findById(int id);

    boolean insert(Client client);

    boolean update(Client client);

    boolean delete(int id);
}