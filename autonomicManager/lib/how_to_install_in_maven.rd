mvn install:install-file -Dfile=WPMConnector.jar -DgroupId=eu.cloudtm \
    -DartifactId=WPMConnector -Dversion=1.0.0 -Dpackaging=jar
    
mvn install:install-file -Dfile=SimulatorConnectorAM.jar -DgroupId=eu.cloudtm \
    -DartifactId=SimulatorConnector -Dversion=1.0.0 -Dpackaging=jar          

mvn install:install-file -Dfile=morphr-am.jar -DgroupId=eu.cloudtm \
    -DartifactId=MorphR -Dversion=1.0.0 -Dpackaging=jar
