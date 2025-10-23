Feature: Managing items in checkout

  Scenario: Adding items to checkout (happy path)
    Given I create a new checkout
      And Checkout with ID 1 is empty
      And Item "Apple" is available to buy
      And Item "Banana" is available to buy
      And Item "Orange" is available to buy
      And I add item "Apple" with quantity 5 to checkout with ID 1
      And I add item "Banana" with quantity 9 to checkout with ID 1
      And I add item "Orange" with quantity 3 to checkout with ID 1
      And I add item "Apple" with quantity 2 to checkout with ID 1
    Then I have following items in my checkout with ID 1
      | Item    | Quantity |
      | Apple   | 7        |
      | Banana  | 9        |
      | Orange  | 3        |

  Scenario: Adding items to checkout (unhappy path)
    Given I create a new checkout
    And Checkout with ID 1 is empty
    And Item "Bed" is not available to buy
    Then Adding item "Bed" with quantity 5 is not allowed

  Scenario: Deleting items from checkout (happy path)
    Given I create a new checkout
    And Checkout with ID 1 is empty
    And Item "Apple" is available to buy
    And Item "Banana" is available to buy
    And Item "Orange" is available to buy
    And I add item "Apple" with quantity 5 to checkout with ID 1
    And I add item "Banana" with quantity 9 to checkout with ID 1
    And I add item "Orange" with quantity 3 to checkout with ID 1
    And I delete 5 items "Apple" from checkout with ID 1
    And I delete 9 items "Banana" from checkout with ID 1
    And I delete 3 items "Orange" from checkout with ID 1
    Then Checkout with ID 1 is empty
