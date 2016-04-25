In this test a pom exists with two profiles: 'profile-with-surefire-2.14' (contains surefire 2.14) and 'profile-without-surefire'.
The test should verify that:
1) 'profile-with-surefire-2.14' keeps the same version of SureFire
2) 'profile-without-surefire' should be modified to include SureFire 2.19.
3) Both instances of SureFire should have a "configuration" element with the listener.
4) Both profiles should have an instance of SeaLights plugin as a sibling of the SureFire plugin.