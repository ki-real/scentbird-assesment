## Tic-Tac-Toe (3x3 grid)

An instance of the application is an imitation of player in Tic-Tac-Toe game.

Current implementation was developed with the following assumptions:

* The strategy of the game doesn't matter. The cell for the next turn is chosen randomly, but subject to the rules of
  the game.
* A single instance of the application can only play one game at a time.
* A game state is stored in a memory of the application instance and will be lost on application restart.
* The opposing application instance is the same application and has no intention of breaking any rules.

### Prerequisites

The following software must be installed on a machine in order to build and run the application.

* Maven 3.9.9
* Java 21.0.5

### Build & Run

#### To build an executable file of the application:

1. Open a terminal
2. Navigate to the root directory of the project
3. Execute the command
   ```shell
   mvn clean install
   ```

#### To run an instance of the application

1. Build an executable file of the application (see the previous section)
2. Open a terminal
3. Navigate to the root directory of the project
4. Navigate to the directory `target` using the following command:
   ```shell
   cd ./target
   ```
5. Use the following command to run a new instance of the application:
   ```shell
   java -jar tic-tac-toe.jar
   ```

### Configuration

Use `application.yaml` file in `src/main/resources/application.yaml` directory of the project to make all necessary
configurations. In order to have any changes in the `application.yaml` file took place, you have to build a new instance
of the application.

#### Configuration parameters description:

1. `server.port` – port on which an instance of the application will be listening for incoming requests
2. `game.rules.time-to-move` – delay an instance will make before making its move. The format of the property is
   a `java.time.Duration` notation.
3. `game.opponent.host` – opposing applications host
4. `game.opponent.port` – opposing applications port
5. `game.opponent.ws-path` – opposing applications web-socket path
6. `game.reconnect.attempts` – number of reconnections attempts to make
7. `game.reconnect.cooldown` – cooldown between reconnections attempts. The format of the property is a
   `java.time.Duration` notation.
