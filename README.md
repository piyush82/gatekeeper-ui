# gatekeeper-ui
simple admin user interface to improve usability

# dependencies
* gatekeeper-driver for java
https://github.com/piyush82/gatekeeper-drivers/tree/master/java
You will need to register the jar file into your local maven repositories. Use the command below (with maven 3.0.5 or higher)
  mvn install:install-file -Dfile=/Your-own-path-prefix-here/gatekeeper-driver/target/gatekeeper-0.1-jar-with-dependencies.jar -DgroupId=ch.cyclops -DartifactId=gatekeeper -Dversion=0.1 -Dpackaging=jar
# screenshots
## Overview Page
![alt text](https://raw.githubusercontent.com/piyush82/gatekeeper-ui/master/images/dashboard.png "Dashboard Overview Page")
## Overview Services
![alt text](https://raw.githubusercontent.com/piyush82/gatekeeper-ui/master/images/overview-services.png "Services Overview Page")
