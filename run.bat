@echo off
rem Lance l'application sous Windows (apres compile.bat). chcp 65001 = accents corrects.
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -cp out Main
