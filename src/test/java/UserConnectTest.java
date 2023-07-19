import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
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

    @Test
    void testA() {
        Response response = given()
                .queryParam("apiKey", apiKey)
                .body("{\n" +
                        "    \"username\": \"frl27\",\n" +
                        "    \"firstName\": \"Egor\",\n" +
                        "    \"lastName\": \"Egorov\",\n" +
                        "    \"email\": \"belogradoff@mail.ru\"\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/users/connect")
                .prettyPeek();


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
                .pathParam("start-date", "2023-07-19")
                .pathParam("end-date", "2023-07-23")
                .queryParam("hash", hash)
                .queryParam("apiKey", apiKey)
                .when()
                .post("https://api.spoonacular.com/mealplanner/{username}/shopping-list/{start-date}/{end-date}/")
                .prettyPeek()
                .then()
                .statusCode(200);

    }


    @Test
    void testC() throws IOException {
        Response response = given()
                .pathParam("username", username)
                .queryParam("hash", hash)
                .queryParam("apiKey", apiKey)
                .body("{\n" +
                        "\t\"item\": \"1 package baking powder\",\n" +
                        "\t\"aisle\": \"Baking\",\n" +
                        "\t\"parse\": true\n" +
                        "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/{username}/shopping-list/items")
                .prettyPeek();
        JsonPath jsonPath = response.jsonPath();
        shoppingListItemId = jsonPath.getString("id");

        assertThat(jsonPath.get("id"), notNullValue());

        String responseJSON = "responseJSON.txt";
        String filePath ="./" + responseJSON;


        try (var inputStream = response.getBody().asInputStream()) {


            Files.copy(inputStream, Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("Это id: " + shoppingListItemId);
    }


    @Test
    void testD() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("hash", hash)
                .pathParam("id", shoppingListItemId)
                .pathParam("username", username)
                .when()
                .delete("https://api.spoonacular.com/mealplanner/{username}/shopping-list/items/{id}")
                .prettyPeek()
                .then()
                .statusCode(200);

    }
}
