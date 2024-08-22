package com.branch.external.user_core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class UserDigest {
    @JsonProperty("user_name")
    @Schema(example = "octocat")
    private String userName;

    @JsonProperty("display_name")
    @Schema(example = "The Octocat")
    private String displayName;

    @Schema(example = "https://avatars.githubusercontent.com/u/583231?v=4")
    private String avatar;

    @JsonProperty("geo_location")
    @Schema(example = "San Francisco")
    private String geoLocation;

    private String email;

    @Schema(example = "https://github.com/octocat")
    private String url;

    @JsonProperty("created_at")
    @Schema(example = "2011-01-25 18:44:36")
    private String createdAt;

    private List<Repo> repos;
}
