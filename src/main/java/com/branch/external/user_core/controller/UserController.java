package com.branch.external.user_core.controller;

import com.branch.external.user_core.domain.UserDigest;
import com.branch.external.user_core.exception.InvalidRequestException;
import com.branch.external.user_core.exception.NotFoundException;
import com.branch.external.user_core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API")
@RequestMapping("v1/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get User ")
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Get user"), //
            @ApiResponse(responseCode = "400", description = "Invalid user name"), //
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping(value = "/{username}", produces = "application/json")
    @ResponseStatus(code = HttpStatus.OK)
    public UserDigest getUserByName(@PathVariable("username") final String userName)
            throws InvalidRequestException, NotFoundException {
        return userService.getUserByName(userName);
    }
}
