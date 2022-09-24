package io.thrive.fs.api.common;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.thrive.fs.api.requests.Users;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class UsersMethods {
    private Users users;
    public UsersMethods(String baseUrl){
        users = new Users(baseUrl);
    }

    /**
     *
     * @param referCode id of the user who created the invitation link
     * @param fullName
     * @param email
     * @param phoneNumber
     * @param countryId
     * @param StateId
     * @param city
     */
    public void usersRegister(
            String referCode,
            String fullName,
            String email,
            String phoneNumber,
            int countryId,
            int StateId,
            String city){
        Response response = users.postUsersRegister(referCode, fullName, email, phoneNumber, countryId, StateId, city);
        response.then()
                .assertThat().statusCode(HttpStatus.SC_CREATED) // 201
                .body(matchesJsonSchemaInClasspath("users.register.response.post.201.schema.json"))
                .body("message", Matchers.equalTo("The user has successfully registered"));
    }

    public JSONObject usersRegister400(
            String referCode,
            String fullName,
            String email,
            String phoneNumber,
            int countryId,
            int StateId,
            String city){
        Response response = users.postUsersRegister(referCode, fullName, email, phoneNumber, countryId, StateId, city);
        response.then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST) // 400
                .body(matchesJsonSchemaInClasspath("users.register.response.post.400.schema.json"));
        return response.getBody().as(JSONObject.class);
    }

    public void usersSetPassword(String token, String password){
        Response response = users.patchUsersSetPassword(token, password);
        response.then()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON)
                .body("message", Matchers.equalTo("Password is set successfully"));
    }

    public void usersSetPassword401(String token, String password){
        Response response = users.patchUsersSetPassword(token, password);
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED) // 401
                .contentType(ContentType.JSON)
                .body("message", Matchers.equalTo("Invalid token"));
    }


    public List<JSONObject> usersPending(String token){
        Response response = users.getUsersPending(token);
        response.then()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON);
        return response.getBody().jsonPath().getList("$", JSONObject.class);
    }

    /**
     *
     * @param adminToken
     * @param isReferral false - returns a list of all non-deleted users,
     *                  true - returns only a list of non-deleted users with the referral id
     * @return
     *<pre>{@code
     *[
     *   {
     *     "id": 0,
     *     "createdAt": "2022-09-12T09:39:39.853Z",
     *     "updatedAt": "2022-09-12T09:39:39.853Z",
     *     "deletedAt": "2022-09-12T09:39:39.853Z",
     *     "confirmedAt": "2022-09-12T09:39:39.853Z",
     *     "fullName": "string",
     *     "nickname": "string",
     *     "email": "string",
     *     "phoneNumber": "string",
     *     "cpf": "string",
     *     "profession": "string",
     *     "birthDate": "2022-09-12T09:39:39.853Z",
     *     "about": "string",
     *     "zip": "string",
     *     "countryId": 0,
     *     "stateId": 0,
     *     "city": "string",
     *     "district": "string",
     *     "street": "string",
     *     "house": "string",
     *     "apartment": "string",
     *     "avatarImageId": 0,
     *     "isLocked": true,
     *     "level": 0,
     *     "levelTitle": "STRIKER_I",
     *     "intStars": 0,
     *     "starThirds": 0
     *   }
     *]
     *}</pre>
     */
    public List<JSONObject> usersAll(String adminToken, boolean isReferral){
        Response response = users.getUsersAll(adminToken, isReferral);
        response.then()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON);;
        List<JSONObject> lstResponse = response
                .getBody()
                .jsonPath()
                .getList("$", JSONObject.class);
        return lstResponse;
    }

    /**
     *
     * @param token
     * @param userId
     */
    public void usersApprove(String token, long userId){
        Response response = users.patchUsersApprove(token, userId);
        response.then()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON)
                .body("message", Matchers.equalTo("User registration has been approved"));
    }
}
