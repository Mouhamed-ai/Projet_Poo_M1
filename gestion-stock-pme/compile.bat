@echo off
rem Compile le projet sous Windows. Resultat dans le dossier "out".
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt && echo Compilation OK
del sources.txt
