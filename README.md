# Twenty Questions
A Minecraft plugin that implements the classic “Twenty Questions” game, where players take turns as the answerer or questioner, with randomized roles and interactive commands.

### Features
	•	Randomly selects an answerer to set the theme and answer.
	•	Allows the answerer to define a theme and a correct answer.
	•	Randomly assigns a questioner to ask yes/no questions or guess the answer.
	•	Includes a timer and turn-passing mechanics to keep the game moving.
	•	Provides hints after five consecutive irrelevant questions or incorrect guesses.
	•	Ends the game when the correct answer is guessed or after 20 questions.
 
### Commands
	•	/game start Starts a new game and randomly selects the answerer.
	•	/theme set  Sets the theme for the game (e.g., /theme set Rapper).
	•	/answer set  Sets the correct answer (e.g., /answer set Diddy).
	•	/question  Allows the questioner to ask a yes/no question.
	•	/guess  Allows the questioner to guess the answer.
	•	/pass Passes the questioner role to another player.
	•	/hint (Answerer only) Provides a hint after five irrelevant questions or wrong guesses.
 
### Game Flow
	1	Use /game start to begin. A random player is chosen as the answerer.
	2	The answerer sets a theme with /theme set and an answer with /answer set .
	3	A random questioner is selected to ask questions using /question or guess with /guess.
	4	If the questioner is inactive for 1 minute, their turn is passed to another player.
	5	The questioner can manually pass their turn with /pass.
	6	After 5 consecutive irrelevant questions or wrong guesses, the answerer may give a hint using /hint.
	7	The game ends when the correct answer is guessed or after 20 questions.
 
### Support
For help or bug reports, Join our [discord](https://discord.gg/xydjE7ym5W).
