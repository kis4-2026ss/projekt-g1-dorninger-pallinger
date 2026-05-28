# Evaluierung der Java-Migration (Swing zu JavaFX)

| Kriterium | 0 Punkte (Nicht erfüllt) | 1 Punkt (Teilweise erfüllt) | 2 Punkte (Vollständig erfüllt) |
| :--- | :--- | :--- | :--- |
| **Build-Stabilität (Kompilierbarkeit)** | Das migrierte Projekt ist nicht kompilierbar. Schwerwiegende Syntax- oder Abhängigkeitsfehler. | Projekt kompilierbar nach geringfügigen manuellen Korrekturen (< 5 Änderungen, z.B. Importe). | "Zero-Touch" Build: Das Projekt kompiliert sofort und fehlerfrei nach dem Migrations-Durchlauf. |
| **Funktionale Korrektheit (Feature Parity)** | Anwendung startet nicht oder stürzt bei Kern-Interaktionen (z.B. Button-Klick) ab. | Die GUI ist sichtbar, aber Logikfehler oder fehlende Event-Handler verhindern die volle Nutzung. | Volle Funktionsgleichheit zum Original-Swing-Projekt (Eingaben, Berechnungen, Zustandswechsel). |
| **Code-Qualität & Modernisierung** | Mechanische Übersetzung (Swing-Logik 1:1 in JavaFX gepresst). Keine Nutzung von JavaFX-Paradigmen. | Einsatz grundlegender JavaFX-Features (z.B. Properties), aber noch enge Kopplung von UI und Logik. | Volle Nutzung moderner Paradigmen (FXML zur UI-Trennung, Controller-Pattern, Lambdas, CSS). |
| **Automatisierungsgrad (Effizienz)** | Migration erfolgte rein manuell durch Interaktion mit der KI ("Chat-Interface"). | Teilautomatisierung: Skript steuert KI an, benötigt aber manuelle Analyse-Vorarbeit oder Plan-Korrektur. | "End-to-End" Automatisierung: Analyse, Plan-Erstellung, Transformation und Validierung erfolgen autonom. |

---

## Messwerte der Testläufe

| Projekt-ID | Ansatz | Build | Funktion | Qualität | Autom. | **Gesamt** |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Sample_1 (Simple) | Manual/Paired | 2 | 2 | 2 | 1 | 7 |
| Sample_1 (Simple) | Automatisiert | 2| 2| 1| 2| 7|
| paint (Complex) | Automatisiert | 2| 2| 1| 2| 6|
| checkmate-master (Complex) | Automatisiert |2 | 0| 0| 2| 4|


  1. SampleProject1 (Simple)                                                                                                                                                                                                                                                                                  
   * Build-Stabilität: 2 Punkte. Das Projekt weist einen "Zero-Touch" Build auf und kompiliert sofort fehlerfrei.                                                                                                                                                                                             
   * Funktionale Korrektheit: 2 Punkte. Alle Kern-Interaktionen wie das Hinzufügen und Löschen von Aufgaben funktionieren einwandfrei.                                                                                                                                                                        
   * Code-Qualität & Modernisierung: 1 Punkt. Es werden zwar Lambdas und ObservableList genutzt, jedoch fehlt die Trennung durch FXML und das Data-Binding wird durch manuelle refreshList()-Aufrufe umgangen.                                                                                                
   * Automatisierungsgrad: 2 Punkte. Die gesamte Kette von Analyse bis Validierung erfolgte ohne manuelle Eingriffe.                                                                                                                                                                                          
                                                                                                                                                                                                                                                                                                              
  2. paint (Complex)                                                                                                                                                                                                                                                                                          
   * Build-Stabilität: 2 Punkte. Alle Abhängigkeiten (inkl. javafx-swing) wurden korrekt in die pom.xml eingetragen.                                                                                                                                                                                          
   * Funktionale Korrektheit: 2 Punkte. Die Zeichenlogik und die Undo-Historie sind konsistent zur Swing-Vorlage.                                                                                                                                                                                             
   * Code-Qualität & Modernisierung: 1 Punkt. Die Verwendung von SwingFXUtils für den Export erhält eine unnötige Kopplung an AWT-Klassen. Redundante Methoden für jede Farbe (red(), blue()) verhindern eine moderne, property-basierte Farbauswahl.                                                         
   * Automatisierungsgrad: 2 Punkte. Das Projekt wurde autonom durch das Skript transformiert und verifiziert.                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                              
  3. Checkmate-master (Complex)                                                                                                                                                                                                                                                                               
   * Build-Stabilität: 2 Punkte. Die Klassenstruktur wurde technisch korrekt nach JavaFX transformiert und ist kompilierbar.                                                                                                                                                                                  
   * Funktionale Korrektheit: 0 Punkte. Die Anwendung ist unbrauchbar, da das Spielfeld aufgrund fehlender Ressourcen-Initialisierung nicht erscheint.                                                                                                                                                        
   * Code-Qualität & Modernisierung: 0 Punkte. Das GridPane kollabiert auf 0x0 Pixel, weil den Square-Elementen keine setPrefSize zugewiesen wurde. Hartkodierte file:images/-Pfade führen zu NullPointerExceptions beim Laden der Spielfiguren.                                                              
   * Automatisierungsgrad: 2 Punkte. Die Transformation war zwar "End-to-End" autonom, scheiterte aber an der konzeptionellen Anpassung des Layout-Verhaltens.      