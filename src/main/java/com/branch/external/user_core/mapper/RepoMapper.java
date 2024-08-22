package com.branch.external.user_core.mapper;

import com.branch.external.user_core.domain.Repo;
import com.branch.external.user_core.model.GitHubRepository;

public class RepoMapper {

    private RepoMapper() {}

    public static Repo map(final GitHubRepository repository) {
        return new Repo(repository.getName(), repository.getHtmlUrl());
    }
}
