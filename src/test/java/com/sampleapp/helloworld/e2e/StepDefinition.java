package com.sampleapp.helloworld.e2e;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinition extends SpringIntegration {
    private ResponseEntity responseEntity;
    private int exceptionStatusCode;
    private String exceptionMessage;
    private String userDetails;

    @Given("User provides the user provides date of birth")
    public void user_provides_the_user_provides_date_of_birth(String docString) throws JSONException {
        JSONObject jsonObject = new JSONObject(docString);
        this.userDetails = docString;
    }

    @When("The user makes a PUT request with name {string} to save or update the details")
    public void the_user_makes_a_put_request_with_name_to_save_or_update_the_details(String userName) {
        // Write code here that turns the phrase above into concrete actions
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        HttpEntity<String> requestEntity = new HttpEntity<String>(this.userDetails, headers);
        this.responseEntity = restTemplate.exchange(getDefaultURL() + "hello/" + userName, HttpMethod.PUT,
                requestEntity, String.class);
    }

    @Then("The API should return a response {string}")
    public void the_api_should_return_a_response(String string) {
        // Write code here that turns the phrase above into concrete actions
        assertEquals(204, responseEntity.getStatusCodeValue());
    }
}
