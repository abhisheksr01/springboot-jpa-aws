Feature: Design and code a simple "Hello World" application that exposes the following
  HTTP-based APIs

  Scenario: Saves/updates the given user’s name and date of birth in the database.
    Given User provides the user provides date of birth
    """
      { "dateOfBirth": "1980-10-22" }
    """
    When The user makes a PUT request with name "abhishek" to save or update the details
    Then The API should return a response "204 No Content"
