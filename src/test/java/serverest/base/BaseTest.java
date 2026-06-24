package serverest.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import serverest.util.TokenHolder;

import static io.restassured.RestAssured.given;

public abstract class BaseTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://serverest.dev";
    }

    protected RequestSpecification request() {
        return given()
                .log().all();
    }

    protected RequestSpecification requestJson() {
        return given()
                .contentType("application/json")
                .log().all();
    }

    protected RequestSpecification requestAuth() {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TokenHolder.token)
                .log().all();
    }

    protected RequestSpecification requestNoAuth() {
        return given()
                .contentType("application/json")
                .log().all();
    }
}