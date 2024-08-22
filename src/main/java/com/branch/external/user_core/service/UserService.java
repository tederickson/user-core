package com.branch.external.user_core.service;

import com.branch.external.user_core.client.GitHubClient;
import com.branch.external.user_core.domain.UserDigest;
import com.branch.external.user_core.exception.InvalidRequestException;
import com.branch.external.user_core.exception.NotFoundException;
import com.branch.external.user_core.mapper.UserDigestMapper;
import com.branch.external.user_core.model.GitHubRepository;
import com.branch.external.user_core.model.GitHubUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final GitHubClient gitHubClient;

    private static void validate(final String userName) throws InvalidRequestException {
        if (StringUtils.isBlank(userName)) {throw new InvalidRequestException("Missing username");}
    }

    public UserDigest getUserByName(final String restUserName) throws InvalidRequestException, NotFoundException {
        validate(restUserName);

        final String userName = restUserName.trim();

        GitHubUser gitHubUser;
        try {
            gitHubUser = gitHubClient.getUserByName(userName);
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new NotFoundException(String.format("User %s not found", userName));
        }

        List<GitHubRepository> repos = gitHubClient.getRepoByName(userName);

        return UserDigestMapper.map(gitHubUser, repos);
    }
}
