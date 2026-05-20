package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Profile;
import java.util.List;
import java.util.Optional;

public interface IProfileRepository {

    // HENT ALLE AKTIVE PROFILES
    List<Profile> getAllActive();

    // HENT ALLE SLETTEDE PROFILES
    List<Profile> getAllDeleted();

    // CRUD
    List<Profile> getAll();
    Optional<Profile> findById(int id);
    boolean insert(Profile profile);
    boolean update(Profile profile);
    boolean delete(int id);

    // SOFT DELETE
    boolean softDelete(int id);

    // RESTORE
    boolean restore(int id);

    // CLIENT ↔ PROFILE
    boolean assignProfileToClient(int clientId, int profileId);
    List<Profile> getProfilesForClient(int clientId);
}

