package com.branch.external.user_core.mapper;

import com.branch.external.user_core.domain.UserDigest;
import com.branch.external.user_core.model.GitHubRepository;
import com.branch.external.user_core.model.GitHubUser;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserDigestMapper {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    private UserDigestMapper() {}

    public static UserDigest map(final GitHubUser gitHubUser, final List<GitHubRepository> repositories) {
        var builder = UserDigest.builder()
                .withUserName(gitHubUser.getLogin())
                .withEmail(gitHubUser.getEmail())
                .withUrl(gitHubUser.getHtmlUrl())
                .withDisplayName(gitHubUser.getName())
                .withAvatar(gitHubUser.getAvatar())
                .withGeoLocation(gitHubUser.getLocation());

        // Convert "2011-01-25T18:44:36Z" to "2011-01-25 18:44:36"
        Instant dateInstant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(gitHubUser.getCreatedAt()));
        builder.withCreatedAt(formatter.format(dateInstant));

        if (repositories != null) {
            builder.withRepos(repositories.stream().map(RepoMapper::map).toList());
        }

        return builder.build();
    }
}
