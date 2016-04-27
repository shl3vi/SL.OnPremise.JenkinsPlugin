In this test a pom exists with surefire that has a "configuration" element which has "argLine" element with some property and
it does chain old value by calling "${argLine}"".
The test should verify that the "argLine" element remains excatly the same.