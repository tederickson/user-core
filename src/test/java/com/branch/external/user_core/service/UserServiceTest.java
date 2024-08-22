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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private static final String USER_NAME = "octocat";
    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 20;


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

        assertThrows(NotFoundException.class, () -> userService.getUserByName(USER_NAME, PAGE_NUMBER, PAGE_SIZE));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getUserByName_invalidName(final String userName) {
        assertThrows(InvalidRequestException.class, () -> userService.getUserByName(userName, PAGE_NUMBER, PAGE_SIZE));
    }

    @Test
    void getUserByName_invalidPageNumber() {
        assertThrows(InvalidRequestException.class, () -> userService.getUserByName(USER_NAME, 0, PAGE_SIZE));
    }

    @Test
    void getUserByName_invalidPageSize() {
        assertThrows(InvalidRequestException.class, () -> userService.getUserByName(USER_NAME, PAGE_NUMBER, 0));
    }

    @Test
    void verifyGitHubCalls() {
        GitHubUser gitHubUser = new GitHubUser();

        String userName = "bob";
        gitHubUser.setLogin(userName);
        gitHubUser.setCreatedAt("2024-08-21T18:44:36Z");
        gitHubUser.setHtmlUrl("https://github.com/bob");

        when(gitHubClient.getUserByName(anyString())).thenReturn(gitHubUser);
        when(gitHubClient.getRepoByName(userName, PAGE_NUMBER, PAGE_SIZE)).thenReturn(null);

        UserDigest userDigest = userService.getUserByName(userName, PAGE_NUMBER, PAGE_SIZE);

        assertThat(userDigest.getRepos(), is(nullValue()));

        assertThat(userDigest.getUserName(), is(userName));
        assertThat(userDigest.getDisplayName(), is(nullValue()));
        assertThat(userDigest.getAvatar(), is(nullValue()));
        assertThat(userDigest.getGeoLocation(), is(nullValue()));
        assertThat(userDigest.getEmail(), is(nullValue()));
        assertThat(userDigest.getUrl(), is("https://github.com/bob"));
        assertThat(userDigest.getCreatedAt(), is("2024-08-21 18:44:36"));
    }
}