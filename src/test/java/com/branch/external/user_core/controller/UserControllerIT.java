package com.branch.external.user_core.controller;

import com.branch.external.user_core.client.CoreUserClient;
import com.branch.external.user_core.domain.UserDigest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {
    @LocalServerPort
    private int port;

    private CoreUserClient client;

    @BeforeEach
    void setUp() {
        client = new CoreUserClient("localhost", port);
    }

    @Test
    void getUserByName_missingUserName() {
        var exception = assertThrows(HttpClientErrorException.BadRequest.class, () -> client.getUserByName("   "));
        assertThat(exception.getMessage(), containsString("Missing username"));
    }

    @Test
    void getUserByName_userNotFound() {
        var exception = assertThrows(HttpClientErrorException.NotFound.class, () -> client.getUserByName("The Octocat"));
        assertThat(exception.getMessage(), containsString("User The Octocat not found"));
    }

    @Test
    void getUserByName() {
        UserDigest userDigest = client.getUserByName("Octocat");

        assertThat(userDigest.getUserName(), is("octocat"));
        assertThat(userDigest.getDisplayName(), is("The Octocat"));
        assertThat(userDigest.getAvatar(), is("https://avatars.githubusercontent.com/u/583231?v=4"));
        assertThat(userDigest.getGeoLocation(), is("San Francisco"));
        assertThat(userDigest.getEmail(), is(nullValue()));
        assertThat(userDigest.getUrl(), is("https://github.com/octocat"));
        assertThat(userDigest.getCreatedAt(), is("2011-01-25 18:44:36"));

        assertThat(userDigest.getRepos(), hasSize(8));

        for (var repo : userDigest.getRepos()) {
            switch (repo.name()) {
                case "boysenberry-repo-1" ->
                        assertThat(repo.url(), is("https://github.com/octocat/boysenberry-repo-1"));
                case "git-consortium" -> assertThat(repo.url(), is("https://github.com/octocat/git-consortium"));
                case "hello-worId" -> assertThat(repo.url(), is("https://github.com/octocat/hello-worId"));
            }
        }
    }
}