In this test a pom exists with surefire inside the "pluginManagement" element.
The surefire has a "configuration" element which has "argLine" element with some property.
The "argLine" doesn't chain the old values using "${argLine"}
The test should verify that:
1. The surefire inside the "pluginManagement" is configured as expected (API, Listener  & "${argLine}" is being added to it).
2. SeaLights plugin is also added under "pluginManagement".
2. New "plugins" element is added under the build with the surefire and SeaLights' plugin.