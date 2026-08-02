// Returned after successful login/register: the JWT the frontend stores
// and attaches as "Authorization: Bearer <token>" on subsequent requests.

package com.helix.account.dto;

public class AuthResponse {

    private String token;
    private String fullName;
    private String email;

    public AuthResponse(String token, String fullName, String email) {
        this.token = token;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}