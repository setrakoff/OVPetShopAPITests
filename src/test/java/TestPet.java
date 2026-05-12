import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("pet")
    @Owner("olegs")
    public void testDeleteNonExistsPet() {
        Response response = step("Send delete query for not exists pet id", () ->
                given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .delete(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("response.getStatusCode expected 200", () -> assertEquals(200, response.getStatusCode(),
                "response.getStatusCode not matched with expected. Response: " + responseBody));

        step("Response body expected 'Pet deleted'", () -> assertEquals("Pet deleted", responseBody,
                "Error text not matched with expected. Response: " + responseBody));
    }
}
