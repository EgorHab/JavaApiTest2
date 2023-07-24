import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

;

public class ApiTest {
    static String apiKey = "f3e41429b9574b5ba90adbdd9c6a38f4";
    ResponseSpecification responseSpecification;
    RequestSpecification requestSpecification;
    @BeforeEach
    void beforeTestResponseSpecification() {
        responseSpecification = new ResponseSpecBuilder()
                .expectResponseTime(Matchers.lessThan(3500L))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

//        RestAssured.responseSpecification = responseSpecification;

    }

    @BeforeEach
    void beforeTestRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey)
                .log(LogDetail.PARAMS)
                .build();

//        RestAssured.requestSpecification = requestSpecification;
    }


    @Test
    void complexSearchNumberCheckPositiveTest() {

        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("number", "99")
                .queryParam("cuisine", "italian")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .body()
                .jsonPath();
        assertThat(response.get("number"), CoreMatchers.<Object>equalTo(99));
    }

    @Test
    void complexSearchStatusCodePositiveTest() {
        given()
                .spec(requestSpecification)
                .queryParam("diet", "vegeterian")
                .queryParam("number", "100")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
                .then()
                .spec(responseSpecification);

    }


    @Test
    void complexSearchDietTotalResultsPositiveTest() {
        JsonPath response = given()
                .spec(requestSpecification)
                .queryParam("diet", "vegetarian")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
//                .prettyPeek()
                .body()

                .jsonPath();


        assertThat(response.get("totalResults"), CoreMatchers.<Object>equalTo(2129));
    }

    @Test
    void complexSearchHasStringPositiveTest() {
        JsonPath response = given()
                .spec(requestSpecification)
                .queryParam("query", "pasta")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
//                .prettyPeek()
                .body()
                .jsonPath();

        assertThat(response.get("offset"), equalTo(0));
    }


    @Test
    void complexSearchResponseTimePositiveTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
//                .prettyPeek()
                .then()
                .spec(responseSpecification);





    }

    @Test
    void cuisineStatusOk() {
        given()
                .spec(requestSpecification)
                .body("{\n" +
                        "    \"title\": \"Spaghetti Carbonara\",\n" +
                        "    \"servings\": 2,\n" +
                        "    \"ingredients\": [\n" +
                        "        \"1 lb spaghetti\",\n" +
                        "        \"3.5 oz pancetta\",\n" +
                        "        \"2 Tbsps olive oil\",\n" +
                        "        \"1  egg\",\n" +
                        "        \"0.5 cup parmesan cheese\"\n" +
                        "    ],\n" +
                        "    \"instructions\": \"Bring a large pot of water to a boil and season generously with salt. Add the pasta to the water once boiling and cook until al dente. Reserve 2 cups of cooking water and drain the pasta. \"\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/recipes/analyze")
//                .prettyPeek()
                .then()
                .spec(responseSpecification);
    }

    @Test
    void cuisineCheckIdPositiveTest() {
        JsonPath response = given()
                .spec(requestSpecification)
                .queryParam("language", "de")
                .queryParam("includeTaste", "true")
                .body("{\n" +
                        "    \"title\": \"Spaghetti Carbonara\",\n" +
                        "    \"servings\": 2,\n" +
                        "    \"ingredients\": [\n" +
                        "        \"1 lb spaghetti\",\n" +
                        "        \"3.5 oz pancetta\",\n" +
                        "        \"2 Tbsps olive oil\",\n" +
                        "        \"1  egg\",\n" +
                        "        \"0.5 cup parmesan cheese\"\n" +
                        "    ],\n" +
                        "    \"instructions\": \"Bring a large pot of water to a boil and season generously with salt. Add the pasta to the water once boiling and cook until al dente. Reserve 2 cups of cooking water and drain the pasta. \"\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/recipes/analyze")
//                .prettyPeek()
                .body()
                .jsonPath();
        assertThat(response.get("originalId"), equalTo(null));
    }


    @Test
    void cuisineTestNotAuthorized() {
        given()
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek()
                .then()
                .statusCode(401);
    }


    @Test
    void cuisineTestNotAuthorizedString() {
        Response response = given()
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek()
                ;
        String responseBody = response.getBody().asString();


        assertThat(responseBody, containsString("not authorized"));
    }


    @Test
    void cuisineSuccessPostRequest() {
        Response response = given()
                .spec(requestSpecification)
                .queryParam("language", "de")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek()
                ;

        assertThat(response.getStatusCode(), anyOf(is(200), is(201), is(202)));

    }


}
