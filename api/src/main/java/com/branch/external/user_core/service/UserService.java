package com.branch.external.user_core.service;

import com.branch.external.user_core.client.GitHubClient;
import com.branch.external.user_core.domain.UserDigest;
import com.branch.external.user_core.exception.InvalidRequestException;
import com.branch.external.user_core.exception.NotFoundException;
import com.branch.external.user_core.mapper.UserDigestMapper;
import com.branch.external.user_core.model.GitHubRepository;
import com.branch.external.user_core.model.GitHubUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final GitHubClient gitHubClient;

    private static void validate(final String userName, final int pageNumber, final int pageSize) {
        if (StringUtils.isBlank(userName)) {throw new InvalidRequestException("Missing username");}
        if (pageNumber < 1) {throw new InvalidRequestException("Invalid page number");}
        if (pageSize < 1) {throw new InvalidRequestException("Invalid page size");}
    }

    @Cacheable("users")
    public UserDigest getUserByName(final String restUserName, final int pageNumber, final int pageSize) {
        validate(restUserName, pageNumber, pageSize);
        log.info("restUserName = {}, pageNumber = {}, pageSize = {}", restUserName, pageNumber, pageSize);

        final String userName = restUserName.trim();

        GitHubUser gitHubUser;
        try {
            gitHubUser = gitHubClient.getUserByName(userName);
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new NotFoundException("User '%s' not found".formatted(userName));
        }

        List<GitHubRepository> repos = gitHubClient.getRepoByName(userName, pageNumber, pageSize);

        return UserDigestMapper.map(gitHubUser, repos);
    }
}
