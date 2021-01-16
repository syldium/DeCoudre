# Thimble
A kind of Minecraft plugin for the thimble (dé à coudre) mini game.

## Usage

### Arena setup

After installing the plugin, you just need to create an arena where players can compete against each other.
Connect to your server and execute the following commands (`<arena>` is to be replaced by the name of your choice):
- `/th arena create <arena>` to create an arena
- `/th arena setSpawn <arena>` to define the location where players appear when they join the arena
- `/th arena setJump <arena>` to define where players will jump from

### Playing

Type `/th join <arena>` to join an arena. The game will start automatically when there are enough players.

### Signs

Every player who has the relevant permissions can create clickable signs as an alternative to player commands.
Place a sign and write on the first line *[Thimble]* or *[DéÀCoudre]* (case-insensitive). On the next line:

- the name of an arena to join
- *block* to op the block selection inventory
- *leave* to leave any arena

Note: once the sign is created, its content is no longer used, you can replace it with commands such as `/setblock` and keep the original behavior!

### PlaceholderAPI

This plugin automatically registers placeholders if PlaceholderAPI is installed.
Currently, only the leaderboard is accessible with the syntax: `%thimble_lb_<table>[_<pos>][_name]%` where `table` is `wins`, `losses`, `jumps` or `thimbles` and `pos` a number from 0 to 9.
Examples:
- `%thimble_lb_wins%`
- `%thimble_lb_losses_3_name%`
- `%thimble_lb_jumps_6_name%`
- `%thimble_lb_thimbles_9%`

### Permissions

- `thimble.commands.arena.*`: `/th arena create`, `/th arena setSpawn`...
- `thimble.commands.player.*`: access to commands for players (`/th block`, `/th join` and `/th leave`)
- `thimble.sign.place`: to place clickable signs
- `thimble.commands.stats.*`: access to statistics

Full list in the [plugin.yml](bukkit/src/main/resources/plugin.yml) file

## Building

This project uses Gradle. Use `./gradlew shadowJar` to get a .jar file (under Windows use `./gradle.bat` instead of `./gradlew`).
