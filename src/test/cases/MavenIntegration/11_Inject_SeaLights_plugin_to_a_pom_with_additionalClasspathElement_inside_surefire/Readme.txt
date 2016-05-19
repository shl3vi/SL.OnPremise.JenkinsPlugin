In this test a pom exists with surefire inside the "plugin" element.
The surefire has a "configuration" element which has "additionalClasspathElements".
The 'additionalClasspathElements' already have one element of 'additionalClasspathElement'.
The test should verify that:
1. We add the apiJar as the second 'additionalClasspathElement'.