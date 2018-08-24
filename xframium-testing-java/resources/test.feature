Feature: Call Gherkin Functions
 
Scenario: Call method one
	Given I call method one
	Then I trace {CLICK} on {WebHomePage.toggleValue}
	Then I use {CLICK} on {WebHomePage.toggleValue}
	
Scenario: Call two methods
	Given I call method one
	Then I call method two with shouldBeTestData

	
Scenario Outline: Call two methods with data
	Given I call method one
	Then I call method two with <testData>
	
Examples: 
	| testData  | name  |
	| dataOne   | one   |
	| dataTwo   | two   |
	| dataThree | three |