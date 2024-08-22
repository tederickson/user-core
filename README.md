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

## GitHub API Rate Limits
According to the "Getting Started" document the GitHub API has [rate limits](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28).
Creating a Github token allows more calls per hour.

Follow the instructions to generate a 
[personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens)
store the token in the application-dev.properties file.
Make certain you do not commit the change.  Treat the token like your bank password.

## Config

The application.properties file is stored in Git.  
The rest are usually local files to prevent leaking sensitive information.

* src/main/resources/application.properties
    * Common configuration values
* src/main/resources/application-dev.properties
    * Development configuration values

## Run the Application
1. Uncomment and provide the personal access token in src/main/resources/application-dev.properties
2. Run the following command in a terminal window:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Test

run `mvn clean verify -Dspring.profiles.active=dev` to run all JUnit tests.

JaCoCo creates the [test coverage reports](./target/site/jacoco/index.html)

The current test environment utilizes SpringBootTest, JUnit and Mockito to achieve 100% test coverage.

### IntelliJ Configuration

Edit the JUnit test configuration so that tests automatically add the active Spring profile:

1. Click on "Edit Configurations"
2. Click on "Edit configuration templates ..."
3. Chose JUnit
4. Add "spring.profiles.active=dev" to the environment variables

## OpenApi 3.0 (Swagger)

Swagger/OpenApi provides the API documentation of the REST endpoints.  
Run the application and point a browser to http://localhost:8080/swagger-ui.html

## Actuators

The dev profile opens all actuators. The other environments only allow the health actuators.

Use http://localhost:8080/actuator with the dev profile to see all actuator links.

Use "caches" and "caches-cache" to monitor the cache sizes.

`http://localhost:8080/actuator/caches`
```json
{"cacheManagers":{"cacheManager":{"caches":{"users":{"target":"java.util.concurrent.ConcurrentHashMap"}}}}}
```

## Security

There are multiple ways to handle making the application secure.

* Use [Spring Boot security](https://spring.io/guides/gs/securing-web)
* The UI could talk to Firebase to handle user authentication
* Use OAuth or SAML
* Grovel before Google and choose a different option

The only security concerns at this time is protecting the personal access token.

## Architecture
The code is broken up into:
* config - almost all the stuff needed to run the app
* model - the DTO (Data Transfer Objects) that talk to the GitHub API
* exception - the application specific Exceptions
* controller - the RestControllers which process the URLs that interact with the application
* domain - the REST request/response domain objects that are converted to JSON
* mapper - the code that converts a DTO to response objects.  The mappers enforce the Separation of Concerns.  
A database entity or DTO is never returned to the user.
* service - the business logic
* client - the client representation of this server.  Allows other microservices to invoke the client methods plus 
allows integration tests.

## Decisions
1. Always enforce Separation of Concerns
2. Validate inputs
3. If the UserService had more than five operations then I would refactor the methods into handlers. 
The service invokes the various handlers.
This prevents merge hell when multiple people are working on the same service.
It also prevents a thousand line JUnit test.  The tests are broken out into handler specific tests.
See RecipeServiceImpl.java in https://github.com/tederickson/wildfit-server/tree/main/src/main/java/com/wildfit/server/service
4. The code that talks to GitHub is a unique client.  This allows mocking out the calls to GitHub.  Otherwise CI/CD may exceed the rate limits.
5. Caching is enabled.  Use swagger to invoke /v1/users/octocat.  Verify the user name is only logged during the first invocation.