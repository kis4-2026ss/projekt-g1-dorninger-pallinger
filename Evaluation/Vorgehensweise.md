1. 24.05 - Infrastruktur anlegen

Gemini gefragt das er uns die Infrastruktur aufbaut.
Skript zum Einlesen der Klassen-Abhängigkeiten (analyze_dependencies) erstellt anhand dieser wird der migration_plan erstellt.
Ein weiteres Skript das JSON abarbeitet und Gemini für jede Datei abruft.
Skript das versucht zu kompilieren und bei Fehler Gemini automatisch um Fix bittet.

Generieren lassen:
Skripts generieren lassen.
Naja -> nicht flexibel (nur ein Projekt möglich, AGENT_STATE.md wurde nicht überschrieben)
Und wieso überhaupt .md?
Markdown VT und NT beleuchten.
Für Json entschieden (Leichter für Skripte zu verarbeiten)
-> Nicht variable einsetzbar (passt nur für ein Projekt > AGENT_STATE überschrieben)

Danach war es ja immer noch nicht automatisch.
migrate.py erstellt > aufgerufen.

oh wir haben kein java > Fehler.

maven installiert und java (darauf eingehen was ist maven und wofür braucht man das?)
-> Auch werden Bibliotheken einfach ins pom.xml geschrieben

Oh wir könnens ja nicht selber testen > wegen Devcontainer (Novnc, xvfb, fluxbox, x11vnc verwenden)

Mit mvn javafx:run möglich in eigenem IntelliJ möglich.

Noch auf einen Fehler gestoßen: Bereits kompiliertes File wird nicht gelöscht.
Und das Tracking in der agent_state.json ging nicht..

So nun kam mir noch was komisch vor.
Die Abhängigkeiten werden ja nur manuell geprüft aber nicht mittels llm, das wäre ja noch gut.
Wurde hinzugefügt.
Genauso wie besseres Logging.

Danach noch um eine Resume Logik ergänzt.

Danach noch um Abhängigkeitsanalyse (echter Parser) verbessert.
Paket-Struktur wird nachgebaut.
Bei Fehler: Datei wird mitgeschickt (spart Tokens)
Zirkuläre Referenz (Analyse via Parser > nur bei zirk. Referenz LLM fragen)

Spannend beim Infrastruktur Aufbau war zu sehen:
Passt eigenes GEMINI.MD an usw.

Falls wir Probleme haben bei größeren Projekten:
1. Kontext Weitergabe (Klasse B abhängig von A -> Signatur von A mitgeben)

