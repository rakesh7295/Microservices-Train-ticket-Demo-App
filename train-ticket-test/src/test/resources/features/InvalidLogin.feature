Feature: User tries to login with invalid credentials

Scenario: User tries to login
Given User opens Login Page
When User enters invalid username and password
Then User should not be logged in 

