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
public class GitHubUser {
    private String login;       // "octocat"
    private String name;        // "The Octocat"
    private String location;    // "San Francisco"
    private String email;

    @JsonProperty("avatar_url")
    private String avatar;      // "https://avatars.githubusercontent.com/u/583231?v=4"

    @JsonProperty("html_url")
    private String htmlUrl;     // "https://github.com/octocat"

    @JsonProperty("created_at")
    private String createdAt;   // "2011-01-25T18:44:36Z"
}
