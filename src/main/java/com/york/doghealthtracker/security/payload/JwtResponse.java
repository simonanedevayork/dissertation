package com.york.doghealthtracker.security.payload;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    private String participantId;

    public JwtResponse(String token, String username, List<String> roles, String participantId) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.participantId = participantId;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
}
