package com.branch.external.user_core.client;

import com.branch.external.user_core.model.GitHubRepository;
import com.branch.external.user_core.model.GitHubUser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GitHubClient {
    private final RestClient restClient;

    public GitHubClient() {
        restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

    public GitHubUser getUserByName(final String userName) {
        return restClient.get()
                .uri("/users/{username}", userName)
                .retrieve()
                .body(GitHubUser.class);
    }

    public List<GitHubRepository> getRepoByName(final String userName) {
        return restClient.get()
                .uri("/users/{username}/repos", userName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
