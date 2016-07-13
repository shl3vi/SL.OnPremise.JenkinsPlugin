In this test inside Surefire unsupported combination of forkMode 'perthread' without threadCount present.
Because threadCount is '0' by default and its unsupported, this all configuration is unsupported.
The test should verify that:
1. We don't add the Sealights plugin and do not save the pom (actual.xml file isn't created).