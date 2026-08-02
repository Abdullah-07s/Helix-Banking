package com.helix.account.service;

import com.helix.account.dto.ChangePasswordRequest;
import com.helix.account.dto.UpdateProfileRequest;
import com.helix.account.dto.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse getProfile(String email);

    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);

    void changePassword(String email, ChangePasswordRequest request);
}