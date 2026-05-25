Problemstellung erklären.
Wie schaut unsere Infrastruktur aus:
Parser um Abhängigkeiten aufzuzeigen.
Was sind zirkuläre Referenzen?
Maven.
Demo-Durchlauf mit kleinem Projekt evtl dann auch ausführen.
Warum unsere Infrastruktur?
                                                                                                                                       
  1. Das Problem der "Aufmerksamkeit" (Quality vs. Quantity)                                                                             
  Wenn du Gemini 10 Dateien gleichzeitig gibst, muss die KI ihre "Attention" auf alle Dateien verteilen.                                 
   * CLI-Direkt: Die KI neigt dazu, Code zu kürzen (// ... rest of code remains same), wichtige Details zu vergessen oder Logik zu       
     vereinfachen, um im Token-Limit zu bleiben.                                                                                         
   * Infrastruktur: Jede Datei bekommt ihren eigenen, isolierten Prompt. Dadurch hat die KI für jede Klasse die volle Aufmerksamkeit und 
     liefert deutlich präziseren Code.                                                                                                   
                                                                                                                                         
  2. Die "Reihenfolge" (Dependency Awareness)                                                                                            
   * CLI-Direkt: Gemini liest die Dateien oft in alphabetischer Reihenfolge. Wenn A.java von B.java abhängt, aber A zuerst migriert      
     wird, kennt die KI die neue Struktur von B noch nicht und halluziniert falsche Methodenaufrufe.                                     
   * Infrastruktur: Unser analyze_dependencies.py baut einen Graphen. Wir migrieren "von unten nach oben". Wenn die KI bei der letzten   
     (komplexesten) Datei ankommt, sind alle Abhängigkeiten bereits erfolgreich nach JavaFX konvertiert.                                 
                                                                                                                                         
  3. Der "Self-Healing" Loop (Phase 3)                                                                                                   
  Das ist der größte Vorteil. Ein einfacher CLI-Aufruf gibt dir Code zurück. Ob der kompiliert, weißt du erst hinterher.                 
   * CLI-Direkt: Wenn es Fehler gibt, musst du sie manuell finden und korrigieren.                                                       
   * Infrastruktur: Unser Skript versucht zu kompilieren. Wenn mvn compile scheitert, nimmt das Skript die Fehlermeldung und den Code,   
     schickt ihn zurück an Gemini und sagt: "Du hast hier einen Fehler gemacht, reparier das!". Das passiert so lange (bis zu 3-mal),    
     bis es wirklich funktioniert. Das kann ein einzelner CLI-Befehl nicht.                                                              
                                                                                                                                         
  4. Fortsetzbarkeit (Resume-Funktion)                                                                                                   
   * CLI-Direkt: Wenn der Befehl nach 15 von 20 Dateien abbricht (Internet weg, Quota-Limit), fängst du von vorne an.                    
   * Infrastruktur: Unser Skript speichert den Fortschritt in der plan.json. Wenn es abbricht, startet es exakt bei der Datei, die       
     gefehlt hat.