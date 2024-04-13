# Wordle Battles

A multiplayer Android version of the popular puzzle game *Wordle*, written in **Kotlin** with **Jetpack Compose**.

## Rules

Each user has to guess a valid **5-letters** word within **6 tries**. After each guess, the color of each tile will change to show how close the guess was to the word:

- **Green**, if the letter is in the right spot of the word;
- **Yellow**, if the letter is part of the word but not in the right spot;
- **Blacked-out**, if the letter is not part of the word.

A user wins the match if it guesses the word in **less time** than the opponent, and when their time is the same, their **attempts** are taken into account. The match ends in a tie if both time and attempts are the same for both the users.

## App features

- **Profile**: a user can customize its **username**, profile **photo** and a **country flag** based on its location.
- **Friends**: a user can **search** for other users referring to usernames shareable as invite-like messages, and then send **friend requests**. After adding a friend, a user can **shake** the phone to send **challenge requests** to play the game.
- **Statistics**: an external **API** keeps track of the number of match wins, ties and losses of each user.