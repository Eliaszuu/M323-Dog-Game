# Dog

## Gamestate
- Aktueller Spieler (int)
- Kartensets (List<Set<Card>>)
- Kugelpositionen (HashMap<player, position>)
- Anzahl Karten in dieser Runde (int)
- Rundenmodus (Karten Tauschen am Anfang oder Karten setzen)
- Evtl bei Ausbaustufe: Anzahl Spieler (4 oder 6)


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

## Programmiersprache / Libraries
Wir machen es mit Vanilla Java.
Für Immutability und Funktionalen Code nehmen wir die Library "Vavr".