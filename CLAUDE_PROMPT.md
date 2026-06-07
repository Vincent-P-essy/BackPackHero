# Prompt — Analyse, amélioration et push automatique

Copie-colle ce texte directement dans Claude Code.

---

## PROMPT

Tu es un assistant de développement. Ton travail est d'analyser ce projet Java, corriger tous les bugs, améliorer le code, puis pousser le résultat sur GitHub.

**Contrainte absolue : aucune trace de toi nulle part.**
- Pas de `Co-Authored-By` dans les commits
- Pas de "Claude", "AI", "assistant" dans les messages de commit, les branches, les commentaires ou le code
- Tous les commits sont signés uniquement au nom du propriétaire du dépôt (utilise `git config user.name` et `git config user.email` déjà configurés)
- Ne crée pas de branche supplémentaire, travaille sur `main`

---

### Étape 1 — Analyse complète

Lis tous les fichiers source du projet :
- Identifie les bugs critiques (logique incorrecte, état corrompu, valeurs jamais initialisées)
- Identifie les bugs de qualité (code dupliqué, contournement de l'architecture, appels O(n) évitables)
- Identifie les features manquantes ou incomplètes (méthodes définies mais jamais appelées, pools vides, cascades manquantes)
- Note tout `System.out.println` de debug restant dans le code de production

### Étape 2 — Corrections

Applique toutes les corrections identifiées, dans cet ordre de priorité :
1. Bugs critiques (données incorrectes, état invalide)
2. Bypasses d'architecture (code qui court-circuite les méthodes publiques du modèle)
3. Features incomplètes (systèmes définis mais non branchés)
4. Nettoyage (println, réflexion fragile, algorithmes naïfs)

Ne refactore pas ce qui n'est pas cassé. Ne crée pas de nouveaux fichiers sauf si absolument nécessaire.

### Étape 3 — Compilation

Vérifie que le projet compile sans erreur :
```bash
bash compile.sh
```

Si la compilation échoue, corrige les erreurs avant de continuer.

### Étape 4 — Commit

Crée **un seul commit** avec tous les changements. Le message de commit doit :
- Être en anglais
- Lister les corrections sous forme de bullet points
- Ne contenir aucune mention de Claude, AI, ou assistant
- Être signé uniquement avec les identifiants git déjà configurés (`git config user.name` / `git config user.email`)

**Ne jamais ajouter `Co-Authored-By` au commit.**

```bash
git add <fichiers modifiés seulement, pas git add -A>
git commit -m "$(cat <<'EOF'
<message de commit ici>
EOF
)"
```

### Étape 5 — Push

```bash
git push
```

Si le push échoue par manque d'authentification, indique à l'utilisateur la commande exacte à lancer manuellement.

---

### Règles générales

- Ne jamais utiliser `--no-verify`
- Ne jamais forcer un push (`--force`)
- Ne jamais modifier `.gitconfig`
- Ne jamais créer de branche avec "claude", "ai", "fix-by-ai" ou équivalent dans le nom
- Si une correction est risquée ou irréversible, demande confirmation avant d'agir
