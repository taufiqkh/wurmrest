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
Details of each set of API calls is below. Each entry outlines the method, path and the form of 
the results that is returned. The use of `:variableName` in the path indicates that a variable is
 expected to be substituted. For example, when requesting the Bank balance of player 
 `Oliver`, the required path is:
 
`/bank/Oliver/money`

On failure of the Wurm interface call, the interface returns the following, where `message` is a 
descriptive message that may vary depending on what error has been encountered:

**Status Code:** 503

**Response:**
```json
{
	"code": 503,
	"message": "Could not create stub for web interface"
}
```

On success, unless otherwise specified each call will return status code 200.

##Server Admin
Provides general server administration capabilities

###Announce
Broadcasts an announcement to all players on the server

**Request:** `POST /server/broadcast`
```
{ message: <String message to be broadcast> }
```
**Response:**
```
{ value: true }
```

###Shutdown
Initiates server shutdown

**Request:** `POST /server/shutdown`

```
{
  initiator: <String initiator of the command>,
  timer: <Number seconds before shutdown>,
  reason: <String reason for the shutdown, broadcast to all players>
}
```

**Response:**
```
{ value: true }
```

##Bank
This group of calls relates to bank information.

###Balance
Retrieves the balance for the player with the name indicated by `:playerName`. On sucess, returns
 a response as follows:

**Request:** `GET /bank/:playerName/money`

**Response:**
```
{ balance: <number> }
```

###Money Transaction
Applies a transaction to the player's bank account, updating it with the supplied transaction. A 
positive amount adds to the player's balance, while a negative subtracts.

**Request:** `POST /bank/:playerName/money`
```
{
  amount: <number>,
  details: <String description of transaction>
}
```

**Response:**
```
{
  value: <String result of transaction processing>
}
```