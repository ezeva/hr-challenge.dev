package challenge.tests;

import challenge.models.UserIdListModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static challenge.specs.UserIdByGenderSpec.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserIdByGenderTest {
    static Stream<String> genders() {
        return Stream.of("male", "female", "any");
    }

    @ParameterizedTest
    @ValueSource(strings = {"male", "female", "any"})
    public void testGetUsersByGender(String gender) {
        UserIdListModel response = step("Send request with gender = '{0}'", () -> {
            return given(userIdByGenderRequestSpec)
                    .queryParam("gender", "male")
                    .when()
                    .get()
                    .then()
                    .spec(userIdByGenderResponseSpec)
                    .extract().as(UserIdListModel.class);
        });
        System.out.println(response.toString());
        step("Check response", () ->{
            assertAll( () -> assertEquals(0, response.getErrorCode(), "'errorCode' should be 0"),
                    () -> assertTrue(response.isSuccess(),"'success' should be true"),
                    () -> assertNull(response.getErrorMessage(), "'errorMessage' should be null"),
                    () -> assertNotNull(response.getIdList(),"'idList' should be not null")
                     );
        });
    }


    @Test
    public void testGet400ByOtherGender() {
        given(userIdByGenderRequestSpec)
               .queryParam("gender", "a")
        .when()
                .get()
        .then()
                .spec(otherGender400Spec)
                .body("isSuccess", equalTo(false))
                .body("errorCode", equalTo(400))
                .body("errorMessage", not(empty()))
                .body("idList", is(empty()));
    }

}