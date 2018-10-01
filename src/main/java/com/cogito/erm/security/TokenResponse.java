package com.cogito.erm.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavankumarjoshi on 31/05/2017.
 */
public class TokenResponse {
    private String token;
    private List<String> roles = new ArrayList<>();

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
