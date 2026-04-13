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
- die Kugelposition hat ja <player, position> und position ist klasse mit (field = integer / specialfield = String)
- falls z.b auf heimfeld ist sie nicht tauschbar und übergehbar
- falls kugelPosition == 0 ist die kugel noch zuhause
- danach dir runde mit 64 felder 
- und die zielfelder sind jeweils -1 bis -4

## Zeitablauf   
- 04.05.2026 steht der PoC
- 11.05.2026 wichtige restliche funktionen eingefügt / mit Teams
- 18.05.2026 ausbaustufe falls möglich GUI / finale abgabe / mit Teams

## PoC
- ohne Teams
- PoC mann kann die kugeln um das spielfeld herumbringen bis ins Ziel
- man kann das Spiel gewinnen
- ohne spezielle funktionen Zugformen

## Programmiersprache / Libraries
Wir machen es mit Vanilla Java.
Für Immutability und Funktionalen Code nehmen wir die Library "Vavr".

## mögliche spielzüge
- kartentausch (mit teammates) (kartensets)
- kugeltausch(kugelposition)
- feurige sieben(kugelposition)
- normal nach vorne(kuelposition)
- karten-Doppelfunktionen (z.b Ass, Raus gehen oder 1/11 fahren)(kugelposition)
- ins ziel gehen(kugelposition)