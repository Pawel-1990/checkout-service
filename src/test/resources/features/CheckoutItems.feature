Feature: Managing items in checkout

  Scenario: Adding items to checkout
    Given I create a new checkout
    And I check if item "Apple" is available to buy
    And I add item "Apple" with quantity 5