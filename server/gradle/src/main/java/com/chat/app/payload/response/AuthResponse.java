package com.chat.app.payload.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Integer statusCode;
    @JsonIgnore
    private HttpHeaders headers;
}
