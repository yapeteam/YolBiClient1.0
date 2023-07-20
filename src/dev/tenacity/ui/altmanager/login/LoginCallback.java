package dev.tenacity.ui.altmanager.login;

public interface LoginCallback {
    void run(String username, String uuid, String access_token, boolean success);
}