In this test a pom exists with jMeter plugin and with 'jMeterProcessJVMSettings' element exists.
Inside 'jMeterProcessJVMSettings' we have an element other than 'arguments'.
The test should verify that:
1. We create the 'arguments' element inside 'jMeterProcessJVMSettings' along side the other element.
2. We append the sealights arguments inside the 'arguments' element.