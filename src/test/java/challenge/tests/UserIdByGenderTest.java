package challenge.tests;

import challenge.models.UserIdListModel;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static challenge.specs.UserIdByGenderSpec.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class UserIdByGenderTest {
    static Stream<String> genders() {
        return Stream.of("male", "female", "any");
    }

    @ParameterizedTest
    @ValueSource(strings = {"male", "female", "any"})
    public void testGetUsersByGender(String gender) {
        UserIdListModel response = step("Send request with gender = '" + gender + "'", () -> {
            return given(userIdByGenderRequestSpec)
                    .queryParam("gender", gender)
                    .when()
                    .get()
                    .then()
                    .spec(userIdByGenderResponseSpec)
                    .extract().as(UserIdListModel.class);
        });
        step("Check response", () ->{
            assertAll( () -> assertEquals(0, response.getErrorCode(), "'errorCode' should be 0"),
                    () -> assertTrue(response.isSuccess(),"'success' should be true"),
                    () -> assertNull(response.getErrorMessage(), "'errorMessage' should be null"),
                    //() -> assertTrue(response.getIdList().length != 0,"'idList' should not be empty")
                    () -> assertFalse(response.getIdList().isEmpty(),"'idList' should not be empty")
                     );
        });
    }


    @ParameterizedTest
    @ValueSource(strings = {"a", ""})
    public void testGet400ByOtherGender(String filter) {
        Response response = step("Send request with gender = '" + filter + "'", () -> {
            return given(userIdByGenderRequestSpec)
                    .queryParam("gender", filter)
                    .when()
                    .get()
                    .then()
                    .spec(otherGender400Spec)
                    .extract().response();
        });

        step("Check response", () ->{
            JsonPath jsonPath = response.jsonPath();
            assertAll( () -> assertEquals(Integer.valueOf(400),jsonPath.get("errorCode"), "'errorCode' should be 400"),
                    () -> assertTrue(jsonPath.get("isSuccess"),"'success' should be true"),
                    () -> assertNotNull(jsonPath.get("errorMessage"), "'errorMessage' should not be null"),
                    () -> assertTrue(jsonPath.getList("idList").isEmpty(),"'idList' should be empty")
            );

    });
    }
    @Test
    public void testGetUsersByAnyContainsOnlyUsersIdMaleAndFemale() {
        Response maleResponse = step("Get response for gender = 'male'", () -> {
        return given(userIdByGenderRequestSpec)
                .queryParam("gender", "male")
                .when()
                .get()
                .then()
                .spec(userIdByGenderResponseSpec)
                .extract().response();});
        Response femaleResponse = step("Get response for gender = 'female'", () -> {
            return given(userIdByGenderRequestSpec)
                    .queryParam("gender", "female")
                    .when()
                    .get()
                    .then()
                    .spec(userIdByGenderResponseSpec)
                    .extract().response();});
        Response anyResponse = step("Get response for gender = 'any'", () -> {
            return given(userIdByGenderRequestSpec)
                    .queryParam("gender", "any")
                    .when()
                    .get()
                    .then()
                    .spec(userIdByGenderResponseSpec)
                    .extract().response();});
        step("Check anyIdList = maleIdList + femaleIdList", () ->{

            List<Integer> maleIdList = maleResponse.jsonPath().get("idList");
            List<Integer> femaleIdList = femaleResponse.jsonPath().get("idList");
            maleIdList.addAll(femaleIdList);
            Collections.sort(maleIdList);
            List<Integer> anyIdList = anyResponse.jsonPath().get("idList");
            assertTrue(maleIdList.stream().distinct().collect(Collectors.toList()).equals(anyIdList), "anyIdList equals sum maleIdList and femaleIdList");
            //assertTrue(anyIdList.containsAll(maleIdList.stream().distinct().collect(Collectors.toList())), "anyIdList contains all elements of maleIdList and femaleIdList");


        });

    }

}