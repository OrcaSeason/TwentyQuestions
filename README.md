# Twenty Questions
**Twenty Questions** is a Spigot plugin that brings the classic guessing game to Minecraft servers. Players collaborate to guess an answer through yes-or-no questions, with one player acting as the "questioner" who sets the topic and answer. The plugin supports multi-player gameplay, turn delegation, hints, and a scoreboard to track progress, with full multilingual support (English and Korean).

## Features
- **Interactive Gameplay**: Start a game with `/game start`, where a random player becomes the questioner to set a topic and answer.
- **Question and Answer System**: Players ask yes-or-no questions or guess the answer, with a limit of 20 questions per game.
- **Turn Delegation**: Players can delegate their turn to others using a GUI, with confirmation prompts.
- **Hint System**: After five consecutive wrong guesses, the questioner can provide a hint to help players.
- **Multilingual Support**: Messages available in English (`messages_en.yml`) and Korean (`messages_ko.yml`), configurable via `config.yml`.
- **Scoreboard Integration**: Displays the current questioner, topic, and remaining questions.
- **Timeout Mechanism**: Automatically skips inactive players after 60 seconds.
- **Robust Error Handling**: Prevents invalid actions (e.g., setting topics out of turn) with clear feedback.

## Requirements
- **Minecraft Version**: 1.8.8 (tested with Spigot 1.8.8)
- **Java Version**: Java 8 or higher
- **Dependencies**: None (standalone plugin)

## Configuration
### config.yml
```yaml
language: "ko" # Options: "ko" for Korean, "en" for English
```

### Language Files
- `messages_en.yml`: English messages
- `messages_ko.yml`: Korean messages
- Customize messages by editing these files in `plugins/TwentyQuestions/`.

## Commands
| Command | Description | Example |
|---------|-------------|---------|
| `/game start` | Starts a new game | `/game start` |
| `/topic set <topic>` | Sets the game topic (questioner only) | `/topic set Animals` |
| `/answer set <answer>` | Sets the answer (questioner only) | `/answer set Tiger` |
| `/answer guess <guess>` | Guesses the answer | `/answer guess Lion` |
| `/delegate` | Opens GUI to delegate turn | `/delegate` |
| `/delegate accept` | Accepts a turn delegation | `/delegate accept` |
| `/delegate deny` | Denies a turn delegation | `/delegate deny` |
| `/hint provide` | Provides a hint (questioner only) | `/hint provide` |
| `/hint deny` | Rejects giving a hint (questioner only) | `/hint deny` |

**Note**: Commands support Korean aliases (e.g., `/게임 시작`, `/주제 설정`, `/정답 추측`).

## Gameplay
1. **Start**: A player runs `/game start`, assigning a random questioner.
2. **Setup**: The questioner sets a topic (`/topic set <topic>`) and answer (`/answer set <answer>`).
3. **Play**: Players take turns asking yes-or-no questions or guessing (`/answer guess <guess>`).
4. **Turns**: Turns rotate, with timeouts for inactive players. Use `/delegate` to pass your turn.
5. **Hints**: After five wrong guesses, the questioner may offer a hint.
6. **End**: The game ends when the answer is guessed (players win) or 20 questions are used (questioner wins).
 
### Support
For help or bug reports, Join our [discord](https://discord.gg/xydjE7ym5W).
