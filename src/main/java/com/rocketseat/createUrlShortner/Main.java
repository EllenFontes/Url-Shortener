package com.rocketseat.createUrlShortner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.builder().build();


    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {

        String inputString = (String) input.get("body");
        Map<String, String> inputMap;


        // Converte as informações no inputString em Map
        try {
            inputMap = objectMapper.readValue(inputString, Map.class);
        } catch (Exception exception) {
            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }

        String originalUrl = inputMap.get("originalUrl");
        String expirationTime = inputMap.get("expirationTime");
        long expirationTimeLong = Long.parseLong(expirationTime);
        String shortUrlCode = UUID.randomUUID().toString().substring(0,8);


        //Transformando as informações em objeto
        UrlData urlData = new UrlData(originalUrl, expirationTimeLong);

        try {
            //Transforma objeto em string
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("url-shortener-storage-rocketseat")
                    .key(shortUrlCode + ".json")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));

        } catch (Exception exception) {
            throw new RuntimeException("Error saving data to S3: " + exception.getMessage(), exception);
        }


        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;

    }
}