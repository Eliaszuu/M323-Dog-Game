# Dog

## Gamestate
- Aktueller Spieler (currentPlayer -> int)
- Kartensets(cardSets -> List<Set<Card>>)


## Spielablauf
- Default-brett einrichten
- neues kartenset
- karten verteilen auf spieler
- jeder kann karte in der reihe auspielen welche verschiedene funktionen hat
- neue runde neue karten
- wenn alle kugeln im Ziel sind gewinner gefunden

## Spielfeld (spielkugelpositionen)
- spieler entity hat 4 balls
- die balls haben die attribute(position = integer / specialfield = String)
- falls kugelPosition == 0 ist die kugel noch zuhause
- 