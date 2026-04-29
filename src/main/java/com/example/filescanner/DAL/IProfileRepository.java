
package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Profile;

import java.util.List;
import java.util.Optional;

public interface IProfileRepository {

    List<Profile> getAll();
    Optional<Profile> findById(int id);
    boolean insert(Profile profile);
    boolean update(Profile profile);
    boolean delete(int id);

    boolean assignProfileToUser(int profileId, String userId);
    List<Profile> getProfilesForUser(String userId);
}
