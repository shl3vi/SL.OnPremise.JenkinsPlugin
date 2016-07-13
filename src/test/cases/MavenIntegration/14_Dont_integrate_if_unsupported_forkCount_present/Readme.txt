In this test forkCount is declared in Surefire with an unsupported value of 0.
The test should verify that:
1. We don't add the Sealights plugin and do not save the pom (actual.xml file isn't created).