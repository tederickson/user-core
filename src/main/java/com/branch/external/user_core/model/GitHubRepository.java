package com.branch.external.user_core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepository {
    private String name;        // "boysenberry-repo-1"

    @JsonProperty("html_url")
    private String htmlUrl;     // "https://github.com/octocat/boysenberry-repo-1"
}
