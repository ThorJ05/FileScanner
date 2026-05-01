package com.example.filescanner.BLL;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.DAL.IProfileRepository;

import java.util.List;

public class ProfileManager {

    private final IProfileRepository repo;

    public ProfileManager(IProfileRepository repo) {
        this.repo = repo;
    }

    //  PUBLIC BUSINESS METHODS

    public List<Profile> getAllProfiles() {
        return repo.getAll();
    }

    public Profile getProfileById(int id) {
        return repo.findById(id).orElse(null);
    }

    public boolean createProfile(Profile profile) {
        validate(profile);
        return repo.insert(profile);
    }

    public boolean updateProfile(Profile profile) {
        validate(profile);
        return repo.update(profile);
    }

    public boolean deleteProfile(int id) {
        return repo.delete(id);
    }

    public boolean assignProfileToUser(int profileId, String userId) {
        return repo.assignProfileToUser(profileId, userId);
    }

    public List<Profile> getProfilesForUser(String userId) {
        return repo.getProfilesForUser(userId);
    }

    //  PRIVATE VALIDATION LOGIC

    private void validate(Profile p) {
        if (p == null)
            throw new IllegalArgumentException("Profile cannot be null");

        if (p.getName() == null || p.getName().isBlank())
            throw new IllegalArgumentException("Profile name cannot be empty");

        if (p.getRotation() < 0 || p.getRotation() > 360)
            throw new IllegalArgumentException("Rotation must be between 0 and 360 degrees");

        if (p.getBrightness() < -255 || p.getBrightness() > 255)
            throw new IllegalArgumentException("Brightness must be between -255 and 255");

        if (p.getContrast() < 0.1f || p.getContrast() > 10f)
            throw new IllegalArgumentException("Contrast must be between 0.1 and 10");
    }
}
