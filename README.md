# Stratopolis

This is a Java implementation of the boardgame [Stratopolis](https://www.boardgamegeek.com/boardgame/125022/stratopolis), published by Gigamic in 2012. I wrote it as the final project for the [COMP1140 course in Australian National University, 2016](https://web.archive.org/web/20180514193201/https://programsandcourses.anu.edu.au/2016/course/COMP1140). The assignment started with a code template, upon which I coded the rest.

Coded in Java and JavaFX.

### Game instructions

For general rules of Stratopolis, please search online.

For my implementation specifically, Green always moves first. There are two AI players available:

* `RandomPlayer` plays randomly;
* `OneLookaheadPlayer` plays the minimax strategy with one step lookahead.

Example screenshot: ![gui_screenshot](presentation/images/gui_screenshot.png)

### File structure

* [`original_description.md`](original_description.md): The original `README.md`. A detailed description of the game Stratopolis and the assignment requirements.
*  `game.jar`: The game. Requires the [Java 8 Runtime Environment](https://java.com/en/download/).

* [`src`](src): Folder of the Java source code.
* [`presentation`](presentation): Folder containing the final presentation slides and the LaTeX files used to generate it.