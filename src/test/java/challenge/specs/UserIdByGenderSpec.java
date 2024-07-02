package challenge.specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserIdByGenderSpec {
    public static RequestSpecification userIdByGenderRequestSpec = with()
            .log().uri()
            .log().method()
            .log().body()
            /* part below can be moved to TestBase class that contains @beforeAll with method setUp
            (public static void)
            .filter(new AllureRestAssured())
            .baseUri("https://hr-challenge.dev.tapyou.com")
            and etc.
             */
            .filter(new AllureRestAssured())
            .baseUri("https://hr-challenge.dev.tapyou.com")
            .basePath("/api/test/users");

    public static ResponseSpecification userIdByGenderResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/success-users-id-list-response-schema.json"))
            .build();

    public static ResponseSpecification otherGender400Spec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(400)
            .expectBody("errorMessage", notNullValue())
            .build();
}


