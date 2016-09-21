#Wurmrest
REST API for the Wurm Unlimited server.

This process runs as a stand-alone REST server, acting as a proxy between clients and the Wurm 
Unlimited RMI interface. It exposes a subset of the interface calls as REST endpoints, segregated
 into areas of functionality rather than having all at the top level.

#Building
Wurmrest requires an installed version of Wurm Unlimited or the Wurm Unlimited Dedicated Server. 
From this installation the following files will need to be installed as 
maven dependencies:
- common.jar
- server.jar
- lib/controlsfx-8.20.8.jar
- lib/jtwitter.jar
- lib/mail.jar

For convenience, on Windows you can use the `mvninstall.bat` script, found in the `scripts`
directory of this repository and executed as follows:

`mvninstall.bat <WU build id> [<steam directory>]`

eg.:

`mvninstall.bat 1266498 "c:\Program Files (x86)\Steam"`

Wurmrest can then be built and packaged as an uberjar:

`mvn package`

#Running
Once built, the server may be started with the following:

`java -jar wurmrest-0.1-SNAPSHOT.jar server [config.yml]`

where `config.yml` is an optional argument that specifies the location of a configuration file.
#Configuration
Configuration is done through a YAML file, which is specified on the command line on startup. It 
is of the following form:
```yaml
# Configuration entry containing the details of the RMI service to which this server will attempt
# to connect
rmi:
  # Name of the host on which the RMI service is running
  hostName: localhost
  # Port on which the RMI service listens
  port: 7220
  # Name of the web interface entry in the RMI service
  objectName: WebInterface
  # Password to use when making RMI calls
  password: yourpassword
```

# API
The api is auto-generated from the source, and is documented at
[https://taufiqkh.github.io/wurmrest](https://taufiqkh.github.io/wurmrest).