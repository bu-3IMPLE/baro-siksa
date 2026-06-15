package com.team3imple.barosiksa.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KakaoGeocodingService {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 주소 문자열로 위도/경도를 조회한다.
     * @return [latitude, longitude]
     */
    public BigDecimal[] geocode(String address) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);
        headers.set("KA", "sdk/1.0 os/java origin/barosiksa");

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            if (documents.isEmpty()) {
                throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
            }
            JsonNode first = documents.get(0);
            BigDecimal lat = new BigDecimal(first.path("y").asText());
            BigDecimal lng = new BigDecimal(first.path("x").asText());
            return new BigDecimal[]{lat, lng};
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
        }
    }
}
