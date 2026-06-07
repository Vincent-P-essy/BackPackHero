# BackPack Hero

Projet de fin de semestre S5 — implémentation en Java du jeu **Backpack Hero**.

Un roguelike dans lequel l'inventaire est un puzzle : les objets ont des formes en 2D et doivent être placés dans un sac à dos de taille limitée. Chaque tour de combat, le héros utilise les objets équipés pour attaquer, se défendre ou lancer des sorts.

---

## Fonctionnalités

- **Sac à dos puzzle** — grille 5×7, placement et rotation des objets (0°, 90°, 180°, 270°)
- **Système de combat tour par tour** — énergie, mana, effets de statut (poison, régénération, force, faiblesse)
- **Donjon procédural** — étages générés aléatoirement avec salles ennemis, trésors, marchands et soigneurs
- **5 ennemis** — SmallRatWolf, RatWolf, FrogSorcerer, LivingShadow, BeeQueen (boss)
- **10+ équipements** — épée, bouclier, baguette magique, fléchettes, armure, pierres runiques, malédictions…
- **Progression** — niveau, XP, bonus de stats au level-up (+5 PV max, +1 énergie max tous les 3 niveaux)
- **Hall of Fame** — enregistrement des scores
- **Double interface** — console (texte) et graphique (zen-lib)

---

## Compilation et lancement

### Prérequis
- Java 17 ou supérieur
- La librairie `zen-6.0.jar` est incluse dans `lib/`

### Compiler
```bash
bash compile.sh
```

### Lancer
```bash
# Interface graphique
java -cp ".:lib/zen-6.0.jar:bin" MainGui

# Interface console
java -cp bin Main
```

---

## Contrôles (interface graphique)

| Action | Touche / Clic |
|--------|---------------|
| Se déplacer | Flèches directionnelles |
| Sélectionner un objet | Clic sur l'objet dans le sac |
| Utiliser un objet | Clic sur l'ennemi cible |
| Attaque de base (1 dmg) | Clic direct sur un ennemi |
| Fin de tour | `E` |
| Se soigner | `H` (dans une salle soigneur) |
| Quitter une salle | `Espace` |
| Quitter le jeu | `Q` |

---

## Structure du projet

```
src/
├── Main.java               # Point d'entrée console
├── MainGui.java            # Point d'entrée GUI
├── controller/
│   ├── GameController.java         # Contrôleur console
│   └── SimpleGameController.java   # Contrôleur GUI
├── model/
│   ├── Hero.java           # Héros (PV, énergie, mana, niveau)
│   ├── Backpack.java       # Grille du sac à dos
│   ├── Cell.java           # Interface scellée (Blocked, Free, Occupied)
│   ├── GameData.java       # État global de la partie
│   ├── HallOfFame.java     # Classement des scores
│   ├── combat/             # Système de combat et ennemis
│   ├── dungeon/            # Génération procédurale du donjon
│   └── equipment/          # Objets et leurs effets
└── view/
    ├── GameView.java           # Interface de vue
    ├── ConsoleView.java        # Vue texte
    └── SimpleGameView.java     # Vue graphique
```

---

## Architecture

Le projet suit le patron **MVC** :

- **Model** — toute la logique de jeu, indépendante de l'affichage
- **View** — affichage console ou graphique, passif (aucune logique)
- **Controller** — reçoit les entrées utilisateur, délègue au modèle

Points notables :
- `Cell` est une `sealed interface` avec trois `record` imbriqués (`Blocked`, `Free`, `Occupied`)
- `PlacedEquipment` est un `record` immuable
- `Equipment` est une classe abstraite avec rotation de forme matricielle
- La génération de donjon est procédurale (Phase 3) avec branches secondaires

---

## Auteur

Vincent Plessy — Licence Informatique S5
