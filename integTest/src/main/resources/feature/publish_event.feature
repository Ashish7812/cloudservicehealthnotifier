Feature: This feature file contains all the scenarios related to publishing events
	
		Scenario: The custom event is fired to the managed stack.
			Given The custom myhealth event is fired with event payload "events-process-failed.json"
			When The event is polled for "SUCCEEDED"
			Then The event is failed with error message "An error occured while processing the event. Cause: An error occured while describing the instance tags. Cause: Invalid instance id."