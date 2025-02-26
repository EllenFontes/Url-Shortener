package com.rocketseat.createUrlShortner;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UrlData {
    private String originalUrl;
    private long expirationTime;

}
