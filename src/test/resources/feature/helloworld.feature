Feature: Design and code a simple "Hello World" application that exposes the following
  HTTP-based APIs

  Scenario: Saves/updates the given user’s name and date of birth in the database.
    Given User provides date of birth
    """
      { "dateOfBirth": "1980-10-22" }
    """
    When The user makes a PUT request with name "abhishek" to save or update the details
    Then The API should return a response with status code "204 NO_CONTENT"

  Scenario: Returns hello birthday message for the given user
    Given User "Singh" birthday is today
    When The user makes a GET request
    # The N in the number of days will be replaced in the step definition dynamically
    Then The API should return a status code 200 and message "Hello, Singh! Happy Birthday!"

  Scenario: Users birthday is in N days and API returns your birthday is in N days message
    Given User "rAjput" birthday is in 20 days from current date
    When The user makes a GET request
    # The N in the number of days will be replaced in the step definition dynamically
    Then The API should return a status code 200 and message "Hello, rAjput! Your birthday is in 20 day(s)"

  Scenario: The given user do not provide dateOfBirth in the request
    Given User did not provide date of birth
    """
      { "jobDate": "2022-10-22" }
    """
    When The user makes a PUT request with name "abhishek" to save or update the details
    Then The API should return a response
    """
    400 Bad Request on PUT request for "http://localhost:8081/hello/abhishek": "{"dateOfBirth":"must not be null"}"
    """
