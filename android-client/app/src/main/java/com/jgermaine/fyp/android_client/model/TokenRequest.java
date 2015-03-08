package com.jgermaine.fyp.android_client.model;

import android.util.Base64;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

/**
 * Created by jason on 08/03/15.
 */
public class TokenRequest {

    private String username;
    private String password;
    private String grant_type;
    private String scope;
    private String client_id;
    private String client_secret;

    public TokenRequest() {
        this.grant_type = "password";
        this.scope = "read write";
        this.client_id = "android-client";
        this.client_secret = "council-alert-android-secret";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public MultiValueMap getRequestBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grant_type);
        body.add("client_id", client_id);
        body.add("client_secret", client_secret);
        body.add("scope", scope);
        return body;
    }

    public HttpHeaders getRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        HttpAuthentication authHeader = new HttpBasicAuthentication(client_id, client_secret);
        headers.setAuthorization(authHeader);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String getB64Auth() {
        String toHash = client_id + ":" + client_secret;
        return "Basic " + Base64.encodeToString(toHash.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
    }
}
