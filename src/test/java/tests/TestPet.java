package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @Test
    @Feature("pet")
    @Owner("olegs")
    public void testUpdateNonExistsPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");

        Response response = step("Send update query with not exists pet id, name", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet")
                );

        String responseBody = response.getBody().asString();

        step("response.getStatusCode expected 404", () -> assertEquals(404, response.getStatusCode(),
                "response.getStatusCode not matched with expected. Response: " + responseBody));
        step("Response body expected 'Pet not found'", () -> assertEquals("Pet not found", responseBody,
                "Error text not matched with expected. Response: " + responseBody));
    }

    @ParameterizedTest(name = "Add pet with status: {2}")
    @CsvSource({
            "200, Buddy, available",
            "201, Daddy, pending",
            "202, Paddy, sold",
    })
    @Feature("pet")
    @Owner("olegs")
    public void testAddNewPet(int id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);

        Response response = step("Send add query with new pet name", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet")
        );

        String responseBody = response.getBody().asString();
        Pet createdPet = response.as(Pet.class);

        step("response.getStatusCode expected 200", () -> assertEquals(200, response.getStatusCode(),
                "response.getStatusCode not matched with expected. Response: " + responseBody));
        step("Response pet parameters are correct", () -> {
            assertEquals(pet.getId(), createdPet.getId(), "Id not matched with expected");
            assertEquals(pet.getName(), createdPet.getName(), "Name not matched with expected");
            assertEquals(pet.getStatus(), createdPet.getStatus(), "Status not matched with expected");
        });
    }
}
