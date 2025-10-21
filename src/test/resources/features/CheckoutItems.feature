Feature: Managing items in checkout

  Scenario: Adding items to checkout (happy path)
    Given I create a new checkout
      And Checkout is empty
      And Item "Apple" is available to buy
      And Item "Banana" is available to buy
      And Item "Orange" is available to buy
      And I add item "Apple" with quantity 5
      And I add item "Banana" with quantity 9
      And I add item "Orange" with quantity 3
      And I add item "Apple" with quantity 2
    Then I have following items in my table
      | Item    | Quantity |
      | Apple   | 7        |
      | Banana  | 9        |
      | Orange  | 3        |

  Scenario: Adding items to checkout (unhappy path)
    Given I create a new checkout
    And Checkout is empty
    And Item "Bed" is not available to buy
    Then Adding item "Bed" with quantity 5 is not allowed