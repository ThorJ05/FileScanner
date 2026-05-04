package com.example.filescanner.BLL;

import com.example.filescanner.BEE.Client;
import com.example.filescanner.DAL.IClientRepository;

import java.util.List;

public class ClientManager {

    private final IClientRepository repo;

    public ClientManager(IClientRepository repo) {
        this.repo = repo;
    }


    public List<Client> getAllClients() {
        return repo.getAll();
    }

    public Client getClientById(int id) {
        return repo.findById(id).orElse(null);
    }

    public boolean createClient(Client client) {
        validate(client);
        return repo.insert(client);
    }

    public boolean updateClient(Client client) {
        validate(client);
        return repo.update(client);
    }

    public boolean deleteClient(int id) {
        return repo.delete(id);
    }



    private void validate(Client c) {
        if (c == null)
            throw new IllegalArgumentException("Client cannot be null");

        if (c.getCompanyName() == null || c.getCompanyName().isBlank())
            throw new IllegalArgumentException("Client name cannot be empty");
    }
}
