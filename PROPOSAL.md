# AI-Assisted Java Legacy Migration

# Goal of the Project

## High-Level Goal

Das Ziel ist die Entwicklung eines KI gestützten Workflows zur Migration von Java-Legacy-Anwendungen (Swing) auf moderne JavaFX-Architektur.
Fokus liegt auf einem Prozess der kleine Arbeitschritte durchführt um ggf. das Quoten-Limit zu umgehen.

## Validation of the Project

2 Repositories raus suchen: Ein simpleres mit wenig Klassen, ein größeres Projekt mit > 30 Klassen.

Kompilierbarkeit: Migriertes Projekt muss ohne Fehler kompilieren.

Funktionalität: GUI Elemente müssen korrekt übersetzt sein.

Modernisierungsgrad: 
Einsatz von Java 20+ Features (fxml, lambdas etc.)
Veraltete API soll ersetzt werden.

# System / Workflow to be Developed

## Overview
1: Automatisierte Analyse

    Input: Scan des gesamten Quellcode-Ordners (Signaturen & Klassen).

    CLI-Task: Gemini analysiert Abhängigkeiten via CLI.

    Output: migration_order.json (Der Taskplan).

    Achtet auf Reihenfolge > zuerst Basis-Klassen dann komplexe Klassen.

2: Transformation der Klassen

    Prozess: Skript arbeitet JSON-Liste Datei für Datei ab mittels Gemini CLI um.

    Output: Gemini generierter Code im Zielverzeichnis.

3: Error-Handling

    Trigger: Skript startet nach der Migration mvn compile.

    Error-Handling: Bei Fehler wird dieser an Gemini gesendet.

    Fix: KI überprüft und fixt erneut.

# Projekt-Plan
Paired-Programing
11.05: Repositories finden
18.05: Research und Entwicklung System Prompt und der Infrastruktur
25.05: Ausführung und Validierung des Projekts
29.05: Vorbereitung auf Präsentation

# Repository Ideen:
https://github.com/haxxorsid/swing-paint-application

https://github.com/HouariZegai/Calculator/tree/master/src/main/java/com/houarizegai/calculator
