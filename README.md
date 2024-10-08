# User-Core

A recently signed customer wants to integrate a subset of GitHub’s data into their application. 
We have discussed their needs and they want an endpoint they can provide a username that will then return 
the data in JSON format as specified below:

```json
{
  "user_name": "octocat",
  "display_name": "The Octocat",
  "avatar": "https://avatars3.githubusercontent.com/u/583231?v=4",
  "geo_location": "San Francisco",
  "email": null,
  "url": "https://github.com/octocat ",
  "created_at": "2011-01-25 18:44:36",
  "repos": [
    {
      "name": "boysenberry-repo-1",
      "url": "https://github.com/octocat/boysenberry-repo-1"
    },
    {
      "name": "git-consortium",
      "url": "https://github.com/octocat/git-consortium"
    }
  ]
}
```

Getting Started: https://docs.github.com/en/rest/guides/getting-started-with-the-rest-api

Data Sources: 
* https://api.github.com/users/octocat
* https://api.github.com/users/octocat/repos

The users endpoint is located at https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-a-user

The repository endpoint is located at https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user

The example response above is the result of calling the API with the username “octocat”. The data is merged after calling the two APIs.

## Config

The application.properties file is stored in Git.

* src/main/resources/application.properties
    * Common configuration values


## Run the Application
Run the following command in a terminal window (or click on the link in IntelliJ):
```bash
mvn -pl api spring-boot:run
```

## Test
Run the following command in a terminal window (or click on the link in IntelliJ) to run all JUnit tests:
```bash
mvn clean verify
```

The current test environment utilizes SpringBootTest, JUnit and Mockito to achieve 100% test coverage.

## OpenApi 3.0 (Swagger)

Swagger/OpenApi provides the API documentation of the REST endpoints.  
Run the application and point a browser to http://localhost:8080/swagger-ui.html

## Actuators

The dev profile opens all actuators. The other environments only allow the health actuators.

Use http://localhost:8080/actuator with the dev profile to see all actuator links.

Use ["caches"](http://localhost:8080/actuator/caches) and 
["caches-cache"](http://localhost:8080/actuator/caches/users) to monitor the cache.  Use swagger to verify the call to UserService is cached.

```json
{
  "cacheManagers": {
    "cacheManager": {
      "caches": {
        "users": {
          "target": "java.util.concurrent.ConcurrentHashMap"
        }
      }
    }
  }
}
```
**Note** the caches do not show up until /v1/users/{username} is invoked.


## Architecture
This is a multi-module project so that other microservices can pull in the
* rest-model-user-core - the REST request/response domain objects that are converted to JSON
* rest-client-user-core - the client representation of this server

Plus the user-core-api also uses the libraries to implement and integration test the core api.
```xml
<dependency>
            <groupId>com.branch.external</groupId>
            <artifactId>rest-model-user-core</artifactId>
            <version>0.0.1-SNAPSHOT</version>
</dependency>
```
```xml
<dependency>
            <groupId>com.branch.external</groupId>
            <artifactId>rest-client-user-core</artifactId>
            <version>0.0.1-SNAPSHOT</version>
</dependency>
```

The user-core-api code is broken up into:
* client - the GitHub client used to invoke the GitHub REST endpoints
* configuration - the Swagger configuration
* controller - the RestControllers which process the URLs that interact with the application
* exception - the application specific Exceptions
* mapper - the code that converts a DTO to response objects.  The mappers enforce the Separation of Concerns.  A database entity or DTO is never returned to the user.
* model - the DTO (Data Transfer Objects) that talk to the GitHub API
* service - the business logic

### Update Multi-Module Versions
The version in all four poms must be the same value.  Instead of manually updating the poms, run
```bash
mvn build-helper:parse-version versions:set -DnewVersion=0.1.1-SNAPSHOT -DgenerateBackupPoms=false
```


## Decisions
1. Always enforce Separation of Concerns
2. Validate inputs
3. If the UserService had more than five operations then I would refactor the methods into handlers. 
The service invokes the various handlers.
This prevents merge hell when multiple people are working on the same service.
It also prevents a thousand line JUnit test.  The tests are broken out into handler specific JUnit test classes.
See RecipeServiceImpl.java in https://github.com/tederickson/wildfit-server/tree/main/src/main/java/com/wildfit/server/service
4. The code that talks to GitHub is a unique client.  This allows mocking out the calls to GitHub.  Otherwise CI/CD may exceed the rate limits.
5. Caching is enabled.  Use swagger to invoke /v1/users/octocat.  Verify the user name is only logged during the first invocation.
6. Domain Objects:
    * UserDigest - a POJO so that JSON properties map to Java attributes.  Also allows OpenAPI to provide example values.
    * Repo - a Record because the attributes naturally map to JSON and need no explanation.
7. Model Objects:
    * POJOs so that JSON properties map to Java attributes
    * The GitHub objects are huge, use `@JsonIgnoreProperties(ignoreUnknown = true)` to ignore the majority of properties
8. Exceptions:
    * Declare business exceptions that are transformed by ControllerAdvice into appropriate HTTP Status and log informative messages in one place.
    * Business exceptions extend RuntimeException because they will eventually be caught by ControllerAdvice
9. Mapper Objects
    * Enforce Separation of Concerns by mapping internal business objects to public domain objects
    * Static methods so that streams invoke method references and classes can invoke methods without creating a
      temporary variable
10. UserService - standard business methods that
    1. validate inputs
    2. execute business rules
    3. map result to public domain object
11. UserController - the GitHub API defaults pagination to page 1, size 30.
Any user with more than 30 repositories will not be able to see all repos.
Added pagination to `/v1/users/{username}` endpoint.
12. Convert the project to a multi-module project to illustrate reusable client and domain jars.
