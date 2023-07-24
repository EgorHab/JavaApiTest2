import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserConnectTest {
    static String password;
    static String username;
    static String hash;
    static String shoppingListItemId;
    static String apiKey = "f3e41429b9574b5ba90adbdd9c6a38f4";

    ResponseSpecification responseSpecification;
    RequestSpecification requestSpecification;
    RequestBodyJson requestBodyJson = new RequestBodyJson();
    ResponseBodyJson responseBodyJson;

    ObjectMapper mapper = new ObjectMapper();



    @BeforeEach
    void beforeTestResponseSpecification() {

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(3000L))
                .build();
        RestAssured.responseSpecification = responseSpecification;
    }

    @BeforeEach
    void beforeTestRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", apiKey)
                .log(LogDetail.HEADERS)
                .build();
        RestAssured.requestSpecification = requestSpecification;

    }


    @Test
    void testA() {
        Response response = given()
                .body("{\n" +
                        "    \"username\": \"frl27\",\n" +
                        "    \"firstName\": \"Egor\",\n" +
                        "    \"lastName\": \"Egorov\",\n" +
                        "    \"email\": \"belogradoff@mail.ru\"\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/users/connect");


        JsonPath jsonPath = response.jsonPath();
        username = jsonPath.getString("username");
        password = jsonPath.getString("spoonacularPassword");
        hash = jsonPath.getString("hash");
        assertThat(jsonPath.get("status"), equalTo("success"));


//        System.out.println(username);
//        System.out.println(password);
//        System.out.println(hash);
    }

    @Test
    void testB() {
        given()
                .pathParam("username", username)
                .pathParam("start-date", "2023-07-24")
                .pathParam("end-date", "2023-07-29")
                .queryParam("hash", hash)
                .when()
                .post("https://api.spoonacular.com/mealplanner/{username}/shopping-list/{start-date}/{end-date}/");

    }


    @Test
    void testC() throws IOException {
        Response response = given()
                .pathParam("username", username)
                .queryParam("hash", hash)
                .body(requestBodyJson)
//                ("{\n" +
//                        "\t\"item\": \"1 package baking powder\",\n" +
//                        "\t\"aisle\": \"Baking\",\n" +
//                        "\t\"parse\": true\n" +
//                        "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/{username}/shopping-list/items")
                .prettyPeek();


        String json = response.getBody().asString();
        StringReader reader = new StringReader(json);

        responseBodyJson = mapper.readValue(reader, ResponseBodyJson.class);


        JsonPath jsonPath = response.jsonPath();
        shoppingListItemId = jsonPath.getString("id");

        assertThat(jsonPath.get("id"), notNullValue());

        String responseJSON = "responseJSON.txt";
        String filePath = "./" + responseJSON;


        try (var inputStream = response.getBody().asInputStream()) {


            Files.copy(inputStream, Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Это id: " + shoppingListItemId);
        System.out.println("Это значение id из объекта после десериализации: " + responseBodyJson.getId()  + " и значение pantryItem: "+ responseBodyJson.getPantryItem());
    }


    @Test
    void testD() {
        given()
                .queryParam("hash", hash)
                .pathParam("id", shoppingListItemId)
                .pathParam("username", username)
                .when()
                .delete("https://api.spoonacular.com/mealplanner/{username}/shopping-list/items/{id}");



    }
}
