## Features

#### Game Logic

- Basic game logic over 10x10 field
- 5 symbols in a row makes a winner (diagonals also count)
- You can not make two moves in a row
- Players may join as they wish, number of them is unlimited

#### Lobby UI

- New Game button
- Table displaying all games and containing several columns:
  - Players that ever made a move
  - Last move timestamp
  - Current status
  - Join button which opens a board page
- Table is updated in real-time

#### GameField UI

- Central top prompt hints the user about current game status and whether they can make a move
- Players prompt on the left side indicates which symbol is assigned to player
- Symbols are just numbers: 1, 2, 3 etc
- All data is updated in real-time
- Available cells are highlighted on hover when you are allowed to move

#### Authentication

- Password is just ignored in current implementation 
- Username is considered sufficient to log in and start playing

## Possible future fixes
Some things I did not have time to do so here are some thoughts:
- Make 2 separate js bundles for board page and lobby page respectively
- Add error handling. I believe some cases are uncovered
- Fix stylesheets so that the pages would be more adaptive
- Clean up UI-side code which is a bit messy imo
- Add winner property into finished game descriptions and prompts
- Add integration tests and add more unit tests
- Add logging
