![Battleship logo](https://github.com/rwever-projects/Battleship/blob/master/images/battleshiptitle.png)
#### Author: Rudi Wever
## Java game with GUI interface.  Client/Server implementation.
#### Languages: Java

### Objective:
Implement a server/client java game application with GUI interface.



### GAME DESCRIPTION
This is a server/client based game of battleship. 
Two (2) players connect to server which manages game play.
The game is written in Java and contains classes for players, ships, clients and server. 
The game is a turned based game in which players take turns guessing at where an opponent's ships are located
by firing missiles (guesses) on a grid, which displays hits and misses on both player's boards. 
The object is to sink all of the other players ships. 
Each play has one each of the following ships: Carrier, Battleship, Destroyer, Submarine, Patrol Boat.

### OPERATION OF GAME
The game is started by initiating the server followed by 2 clients.
The initial game starts by asking the server address to be entered.
This is the location where the server is running.
If it is running on the same machine where the client is running, the default value of 'localhost' can be used.
If the server is running on a different machine, you have to ask your system administration for the server address.
For example: 192.168.10.64

After you are connected to the server, you can enter your name.
This will be displayed on your board as well as the other user's board.
Here you can position your ships on your board.
When one of the ships (i.e. Carrier) is selected, you can select the horizontal/vertical positioning of the ship.
To place the ship on your board, click on the top/left position where you want the ship to be placed.
If you are satisified with the ship's position, click 'Save'.  Otherwise click 'Cancel', and you can now reposition that
ship again.
This will continue for each ship, until the last ship has been saved.

Once all the ships have been saved, you are presented with your board which shows the locations of your ships
(this is where your opponent will be firing at), and your opponent's board which is the board that you fire at.
Underneath the opponent's board is a display of your opponent's ship's status.  During the game, the status of
your opponent's ship will update to indicate how much of the ship has been sunk.

After both players have finished configuring their ship's locations, one of the players (at random) will be assigned
a turn to play.  When the player has a turn assigned, he can fire at the opponent's board by clicking any location on
the opponent's board.

After a player fires, the selected location will indicate a 'hit' or 'miss'.  If the location was a 'hit', then the location
will turn red, and the type of ship that was hit will be updated to indicate that more of that ship has been sunk.
If the location was a 'miss', then the location will be greyed out. 
The other player's board will also be updated to show the location that was fired at, and whether it was a 'hit' or 
a 'miss'.  If the location was 'hit' his green button will change to red.  If the location was a 'miss' the location will
be greyed out to show that the location was fired at. 
 After a position is fired upon, it can no longer be selected and turn passes to the other player.

Play contiunes by alternating turns between players until one of the players has sunk all of the other player's ships.
The winning player will be displayed to both players. 
