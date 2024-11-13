package com.chat.app.payload.request;


public class AuthRequestWithUsername {

    private String username;
    private String password;
//    private PasswordEncoder passwordEncoder;

//    public void setPassword(String password) {
//        this.password = passwordEncoder.encode(password);
//    }
    public AuthRequestWithUsername(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

