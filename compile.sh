#!/bin/bash

# Script de compilation pour Backpack Hero
# Compile la version console et l'interface graphique

set -e

echo "========================================="
echo "  Compilation de Backpack Hero"
echo "========================================="
echo ""

# Creer le repertoire bin s'il n'existe pas
mkdir -p bin

# Compiler la version console
echo "[1/2] Compilation de la version console..."
javac -d bin -cp "src" src/Main.java
echo "     Version console compilee avec succes"
echo ""

# Compiler l'interface graphique
echo "[2/2] Compilation de l'interface graphique..."
javac -d bin -cp ".:lib/zen-6.0.jar:src" src/MainGui.java src/controller/*.java src/view/*.java
echo "     Interface graphique compilee avec succes"
echo ""

echo "========================================="
echo "  Compilation terminee avec succes"
echo "========================================="
echo ""
echo "Pour executer:"
echo "  Version console: java -cp bin Main"
echo "  Interface graphique: java -cp \".:lib/zen-6.0.jar:bin\" MainGui"
echo ""
