package com.branch.external.user_core.service;

import com.branch.external.user_core.client.GitHubClient;
import com.branch.external.user_core.domain.UserDigest;
import com.branch.external.user_core.exception.InvalidRequestException;
import com.branch.external.user_core.exception.NotFoundException;
import com.branch.external.user_core.model.GitHubUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private static final String USER_NAME = "octocat";

    private UserService userService;
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        gitHubClient = mock(GitHubClient.class);
        userService = new UserService(gitHubClient);
    }

    @Test
    void getUserByName_userNotFound() {
        HttpClientErrorException notFound = mock(HttpClientErrorException.NotFound.class);
        when(gitHubClient.getUserByName(USER_NAME)).thenThrow(notFound);

        assertThrows(NotFoundException.class, () -> userService.getUserByName(USER_NAME));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getUserByName_invalidName(final String userName) {
        assertThrows(InvalidRequestException.class, () -> userService.getUserByName(userName));
    }

    @Test
    void verifyGitHubCalls() throws NotFoundException, InvalidRequestException {
        GitHubUser gitHubUser = new GitHubUser();

        gitHubUser.setLogin("bob");
        gitHubUser.setCreatedAt("2024-08-21T18:44:36Z");
        gitHubUser.setHtmlUrl("https://github.com/bob");

        when(gitHubClient.getUserByName(anyString())).thenReturn(gitHubUser);

        UserDigest userDigest = userService.getUserByName("bob");

        verify(gitHubClient).getRepoByName(anyString());
        assertThat(userDigest.getRepos(), hasSize(0));

        assertThat(userDigest.getUserName(), is("bob"));
        assertThat(userDigest.getDisplayName(), is(nullValue()));
        assertThat(userDigest.getAvatar(), is(nullValue()));
        assertThat(userDigest.getGeoLocation(), is(nullValue()));
        assertThat(userDigest.getEmail(), is(nullValue()));
        assertThat(userDigest.getUrl(), is("https://github.com/bob"));
        assertThat(userDigest.getCreatedAt(), is("2024-08-21 18:44:36"));
    }
}