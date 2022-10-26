package com.sampleapp.helloworld.e2e;

import com.sampleapp.helloworld.controller.Response;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinition extends SpringIntegration {
    private ResponseEntity responseEntity;
    private String userDetails;
    private String userName;
    private Exception exception;

    @Given("User provides date of birth")
    public void user_provides_date_of_birth(String requestString) throws JSONException {
        this.userDetails = requestString;
    }

    @When("The user makes a PUT request with name {string} to save or update the details")
    public void the_user_makes_a_put_request_with_name_to_save_or_update_the_details(String userName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        HttpEntity<String> requestEntity = new HttpEntity<String>(this.userDetails, headers);
        try {
            this.responseEntity = getRestTemplate().exchange(getDefaultURL() + "hello/" + userName, HttpMethod.PUT,
                    requestEntity, String.class);
        } catch (Exception exception) {
            this.exception = exception;
        }
    }

    @Then("The API should return a response with status code {int} and phrase {string}")
    public void the_api_should_return_a_response_with_status_code_and_phrase
            (int expectedStatusCode, String expectedPhrase) {
        assertEquals(expectedStatusCode, responseEntity.getStatusCodeValue());
        assertEquals(expectedPhrase, responseEntity.getStatusCode().getReasonPhrase());
    }

    @Given("User {string} birthday is today")
    public void user_birthday_is_today(String userName) {
        this.userName = userName;
        // Arranging test data with a year old date from current date in UTC
        String oneYearOldDate = LocalDate.now(ZoneId.of("UTC")).minusYears(1).toString();
        this.userDetails = "{ \"dateOfBirth\": \"" + oneYearOldDate + "\" }";
        the_user_makes_a_put_request_with_name_to_save_or_update_the_details(userName);
    }

    @When("The user makes a GET request")
    public void the_user_makes_a_get_request() {
        this.responseEntity = getRestTemplate().getForEntity(getDefaultURL() + "hello/" + this.userName, Response.class);
    }

    @Then("The API should return a status code {int} and message {string}")
    public void the_api_should_return_a_status_code_and_message(Integer expectedStatusCode, String expectedMessage) {
        Response response = (Response) this.responseEntity.getBody();
        assertEquals(expectedStatusCode, this.responseEntity.getStatusCodeValue());
        assertEquals(expectedMessage, ((Response) this.responseEntity.getBody()).getMessage());

    }

    @Given("User {string} birthday is in {int} days from current date")
    public void user_birthday_is_in_days_from_current_date(String userName, Integer birthDayInDays) {
        this.userName = userName;
        String oneYearOldDate = LocalDate.now(ZoneId.of("UTC")).minusYears(1).plusDays(birthDayInDays).toString();
        this.userDetails = "{ \"dateOfBirth\": \"" + oneYearOldDate + "\" }";
        the_user_makes_a_put_request_with_name_to_save_or_update_the_details(userName);
    }

    @Given("User did not provide date of birth")
    public void user_did_not_provide_date_of_birth(String requestString) {
        this.userDetails = requestString;
    }

    @Then("The API should return a response with status code {int} and body")
    public void the_api_should_return_a_response_with_status_code_and_body(Integer expectedStatusCode, String expectedError) {
        assertEquals(expectedStatusCode + " : " + expectedError, this.exception.getMessage());
    }
}
