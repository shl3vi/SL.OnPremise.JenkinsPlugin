In this test a pom exists with surefire that has a "configuration" element which has "argLine" element with some property and
it doesn't chain old value by calling "${argLine}"".
The test should verify that the "argLine" element in being updated so it will include the call to "${argLine}"".