#!/bin/sh
# Compile le projet (Linux / macOS / Git Bash). Résultat dans le dossier "out".
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java") && echo "Compilation OK"
