call mvn clean install -Dmaven.test.skip=true -Pdownload-jars
copy /y "target\sealights-jenkins.hpi" "C:\Program Files (x86)\Jenkins\plugins\sealights-jenkins.hpi"