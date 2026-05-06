package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Profile;
import java.util.List;
import java.util.Optional;

public interface IProfileRepository {

    // CRUD
    List<Profile> getAll();
    Optional<Profile> findById(int id);
    boolean insert(Profile profile);
    boolean update(Profile profile);
    boolean delete(int id);

    // CLIENT ↔ PROFILE
    boolean assignProfileToClient(int clientId, int profileId);
    List<Profile> getProfilesForClient(int clientId);
}

