package com.branch.external.user_core.client;

import com.branch.external.user_core.domain.UserDigest;
import org.springframework.web.client.RestClient;

public class CoreUserClient {
    private final RestClient restClient;

    public CoreUserClient(String server, int port) {
        restClient = RestClient.builder()
                .baseUrl("http://" + server + ":" + port)
                .build();
    }

    public UserDigest getUserByName(final String userName) {
        return restClient.get()
                .uri("/v1/users/{username}", userName)
                .retrieve()
                .body(UserDigest.class);
    }
}
