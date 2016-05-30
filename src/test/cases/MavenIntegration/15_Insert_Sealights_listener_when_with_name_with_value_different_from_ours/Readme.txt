In this test a pom exists with <name>listener<name> and with the <value>.
The value tag doesn't have our listener in the text content.
The test should verify that:
1. We append our listener with ', ' to the text inside value.