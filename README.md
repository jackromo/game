This is a 2d platform game.

The commits shown here contain only source code.
The game requires a 'resources' file for all images, sounds and levels; it has not been included here, for privacy reasons.
If you want a copy of the game, email me at sharrackor@gmail.com to request a JAR executable of the game.

The game allows for the following:
    -Player, enemy, attack and wall entities
    -Collision detection and hitboxes
    -Levels which can be built with Tiled Map Editor and parsed by game
    -Image sequences for walking, jumping, punching, etc.
    -Health and attacking
    -Working, moving camera to follow player.

The game first creates a Game class for the window, which creates a canvas to be drawn to.
A game session is then started, which updates and renders in two separate threads managed by the game canvas.
The session updates each player, manages collisions and updates the camera.

Planned features:
    -AI class to contain all possible enemy AI's, which can be selected from when creating enemies in Tiled
    -Sound managers for sound effects and game music
    -Image sequences for enemies, which can be selected from when creating enemies in Tiled
    -Pause session and menu session
    -Multiple levels and boss battles
    -Cutscenes and dialogue boxes (cutscene session and dialogue session?)