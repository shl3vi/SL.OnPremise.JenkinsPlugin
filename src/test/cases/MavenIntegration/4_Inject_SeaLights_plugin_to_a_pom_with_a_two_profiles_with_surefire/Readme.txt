In this test a pom exists with two profiles which contain the Maven SureFire plugin (one of them contains additional plugin).
The test should verify that each instance of SureFire gets a "configuration" element
with the listener. In addition, SeaLights plugin is added as a sibling of the SureFire plugin.