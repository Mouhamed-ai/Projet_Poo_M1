# Plan de test – Gestion de stock et facturation d'une PME

Master 1 MIAGE – Projet POO Java (Collections). Tests réalisés sur le jeu de démonstration chargé au démarrage
(10 produits P001–P010, 5 clients C001–C005, CMD001 et CMD002 VALIDEES, CMD003 EN_COURS).
Comptes : `gestionnaire / gest123` et `vendeur / vend123`.

## 1. Comment exécuter

| Méthode | Commande (depuis le dossier du projet) | Intérêt |
|---|---|---|
| Manuelle | `compile.bat` puis `run.bat` (Windows) ou `sh compile.sh` puis `sh run.sh` | suivre les tests des sections 3 à 5 à la main |
| Scénario rejoué | `java -Dfile.encoding=UTF-8 -cp out Main < tests/scenario_vendeur.txt` | rejoue les saisies d'un profil et compare à la colonne « Résultat attendu » |
| Règles métier | `javac -encoding UTF-8 -cp out -d out tests/VerifRegles.java` puis `java -cp out VerifRegles` | 25 contrôles automatiques, doit afficher « 0 echec(s) » |

Scénarios fournis : `tests/scenario_vendeur.txt` (sections 3 et 4), `tests/scenario_gestionnaire.txt` (section 5),
`tests/scenario_connexion.txt` (T01–T03). Après le scénario vendeur, le dossier `factures_test/` contient `facture_CMD001.txt` ;
après le scénario gestionnaire, `sauvegarde_test/` contient 4 fichiers CSV (à supprimer avant le rendu).
Remarque : dans les scénarios, les dates des commandes créées (CMD004) sont celles du jour d'exécution.

## 2. Connexion

| N° | Action | Résultat attendu |
|---|---|---|
| T01 | Se connecter `vendeur / vend123` | « Bienvenue Vendeur », menu Commandes / Facturation |
| T02 | Se connecter `gestionnaire / gest123` | « Bienvenue Gestionnaire », menu Produits / Clients / Tableau de bord / Sauvegarde |
| T03 | 3 mots de passe faux de suite | `[REFUSE] ... (essai 1/3)`, `2/3`, `3/3`, puis « Trop d'échecs de connexion » |
| T04 | Saisir une lettre à un choix de menu ou une quantité | message d'erreur, la saisie est redemandée, pas de plantage |

## 3. Commandes (profil vendeur : menu 1)

| N° | Action | Règle | Résultat attendu |
|---|---|---|---|
| C01 | 7 → 1 : lister toutes les commandes | E8 | CMD001, CMD002, CMD003 ; totaux HT 937 500, 51 000, 305 000 FCFA ; « 3 commande(s). » |
| C02 | 7 → 2 → 2 : filtrer VALIDEE | E8 | CMD001 et CMD002 seulement |
| C03 | 7 → 3 → CMD001 : détail | E8 | 2 lignes (P005 ×2, P007 ×5), Total HT 937 500,00 FCFA |
| C04 | 1 : créer une commande pour `C999` | R1 | `[REFUSE] Client introuvable : C999` |
| C05 | 1 : créer pour `C002` | E1 | « Commande CMD004 créée pour Fatou Ndiaye (EN_COURS) » (numéro suivant) |
| C06 | 2 : ajouter 6 × P010 à CMD004 | R3 | `[REFUSE] Stock insuffisant pour P010 : demandé 6, disponible 4.` |
| C07 | 2 : ajouter 10 × P001, puis 5 × P001 | R4 | une seule ligne P001 de 15 unités (fusion), Total HT 52 500,00 |
| C08 | 2 : ajouter 300 × P001 | R3 | `[REFUSE] Stock insuffisant pour P001 : demandé 315, disponible 190.` (quantité totale contrôlée) |
| C09 | 3 : passer la ligne P001 à 20 | E3 | « Quantité modifiée. » |
| C10 | 2 : ajouter 10 × P002, puis 4 : retirer P002 | E3 | ligne ajoutée (Total 110 000), puis « Ligne retirée. » |
| C11 | 5 : valider CMD004 | E4 | « validée : le stock a été mis à jour » ; stock P001 : 190 → 170 |
| C12 | 2, 3 ou 4 sur CMD004 validée | R5 | `[REFUSE] La commande CMD004 est VALIDEE : elle n'est plus modifiable.` (3 fois) |
| C13 | 6 : annuler CMD004 (confirmer `o`) | E5 | « Commande CMD004 annulée. » ; stock P001 revient à 190 |
| C14 | 6 : annuler de nouveau CMD004 | R5 | `[REFUSE] La commande CMD004 est déjà annulée.` |
| C15 | 5 : valider CMD004 annulée | R5 | `[REFUSE] ... ANNULEE : seule une commande EN_COURS peut être validée.` |
| C16 | 5 : valider une commande vide | R3 | `[REFUSE]` : au moins une ligne est exigée |
| C17 | Valider une commande dont le stock a baissé entre-temps | R3 | refus, **aucun** stock décrémenté, la commande reste EN_COURS (contrôle `VerifRegles`) |
| C18 | 5 : valider CMD003 (P006 ×1, P010 ×1) | E4 | validée ; stock P006 : 8 → 7, P010 : 4 → 3 |

## 4. Facturation (profil vendeur : menu 2)

| N° | Action | Résultat attendu |
|---|---|---|
| F01 | 1 → CMD004 (annulée) | `[REFUSE] Une facture ne peut être éditée que pour une commande validée (statut actuel : ANNULEE).` |
| F02 | 1 → CMD002 (particulier, HT 51 000) | pas de remise ; TVA 9 180,00 ; TTC exact 60 180,00 ; arrondi 0,00 ; **TOTAL TTC 60 180,00 FCFA** |
| F03 | 1 → CMD003 (entreprise, HT 305 000 ≤ 500 000) | pas de remise ; TVA 54 900,00 ; TTC 359 900,00 ; arrondi 0,00 |
| F04 | 1 → CMD001 (entreprise, HT 937 500 > 500 000) | remise 5 % = -46 875,00 ; HT après remise 890 625,00 ; TVA 160 312,50 ; TTC exact 1 050 937,50 ; arrondi +2,50 ; **TOTAL TTC 1 050 940,00 FCFA** |
| F05 | Seuil de remise (`VerifRegles`) | HT = 500 000 exactement : aucune remise (TTC 590 000) ; HT = 500 001 : remise 25 000,05 ; TVA 85 500,17 ; TTC exact 560 501,12 ; arrondi -1,12 ; TTC 560 500 |
| F06 | 2 → CMD001, dossier `factures_test` (bonus H2) | « Facture exportée : …/facture_CMD001.txt » ; le fichier contient la même facture qu'à l'écran |
| F07 | Prix figé : modifier le prix d'un produit puis rééditer une facture | les lignes de commande gardent le prix d'origine |

## 5. Produits, clients, bonus (profil gestionnaire)

| N° | Action | Règle | Résultat attendu |
|---|---|---|---|
| P01 | 1 → 7 : produits en alerte (état initial) | C7 | P010 (4 ≤ 4) et P008 (5 ≤ 8), marqués ALERTE, triés par stock croissant |
| P02 | 1 → 1 : ajouter P011 « Webcam HD », Accessoires, 25 000, 15, seuil 5 | C1 | « Produit P011 ajouté. » |
| P03 | Ajouter de nouveau P011 | R1 | `[REFUSE] La référence P011 existe déjà.` |
| P04 | Ajouter un produit avec un stock de -5 | R2 | `[REFUSE] La quantité en stock ne peut pas être négatif(ve).` |
| P05 | 1 → 2 : modifier P011 (désignation, prix 27 500) | C2 | « Produit modifié » ; la liste affiche Webcam Full HD, 27 500 |
| P06 | 1 → 4 : réapprovisionner P008 de 10 | C4 | « Nouveau stock de P008 : 15 » ; P008 sort des alertes |
| P07 | Réapprovisionner P999 | R1 | `[REFUSE] Produit introuvable : P999` |
| P08 | 1 → 5 : lister par désignation / prix / quantité | C5 | trois ordres différents : A→Z, prix croissant (P003 1 800 en tête), stock croissant (P010 en tête) |
| P09 | 1 → 6 : recherche référence `P005`, fragment `pouces`, catégorie `Informatique` | C6 | 1 produit ; 2 produits (P005, P010) ; 3 produits (P005, P006, P010) |
| P10 | 1 → 3 : supprimer P005 (confirmer) | R6 | `[REFUSE] Suppression refusée : le produit P005 figure dans une commande validée.` |
| P11 | 1 → 3 : supprimer P006 (confirmer), présent dans CMD003 EN_COURS | R6 | « Produit P006 supprimé. Ses lignes ont été retirées des commandes en cours : [CMD003] » |
| P12 | 1 → 8 : journal des mouvements (bonus H1) | H1 | sorties de CMD001 et CMD002, entrée « Réapprovisionnement » P008 +10 |
| K01 | 2 → 1 : ajouter C006 (type Entreprise) | D1 | « Client C006 ajouté. » |
| K02 | Ajouter de nouveau C006 | R1 | `[REFUSE] Le code client C006 existe déjà.` |
| K03 | Ajouter un client avec l'email `abc` | R7 | `[REFUSE] L'email doit contenir le caractère '@'.` |
| K04 | 2 → 4 : lister | D4 | clients triés par nom |
| K05 | 2 → 5 : recherche code `C003`, nom `ndiaye` | D5 | Sénégal Tech SARL ; Fatou Ndiaye |
| K06 | 2 → 6 : historique de C003 | D5 | CMD001, VALIDEE, 2 lignes, 937 500,00 |
| K07 | 2 → 3 : supprimer C003 | R6 | `[REFUSE] Suppression refusée : le client C003 a des commandes en cours ou validées.` |
| K08 | 2 → 3 : supprimer C006, puis C005 (sans commande) | D3 | « Client C006 supprimé. », « Client C005 supprimé. » |
| D01 | 3 → 1 : chiffre d'affaires total (bonus H3) | H3 | 941 625,00 FCFA (HT après remise des commandes validées) |
| D02 | 3 → 2 : chiffre d'affaires par mois | H3 | 2026-10 : 890 625,00 ; 2026-11 : 51 000,00 |
| D03 | 3 → 3 : top 5 produits ; 3 → 4 : meilleurs clients | H3 | P001 en tête avec 10 unités ; C003 : 890 625,00 |
| S01 | 4 : sauvegarder dans `sauvegarde_test` (bonus H4) | H4 | « Sauvegarde terminée. » ; 4 fichiers CSV créés |
| S02 | Réapprovisionner P001 de 50 (stock 240), puis 5 : recharger `sauvegarde_test` | H4 | « Données rechargées. » ; P001 revient à 190 |
| S03 | 5 : recharger un dossier inexistant | H4 | `[REFUSE] Fichier de sauvegarde introuvable : dossier_inexistant/produits.csv` |

## 6. Règles métier contrôlées par `VerifRegles` (25 contrôles)

Références en double, prix nul, stock négatif, client en double ; remise à 500 000 / 500 001 ; arrondi du TTC ; facture refusée
pour une commande non validée ; fusion des lignes d'un même produit ; contrôle du stock à l'ajout (quantité totale) ;
validation en deux passes (rien n'est décrémenté si une ligne manque) ; modification refusée après validation ;
restitution du stock à l'annulation ; double annulation refusée ; suppression d'un produit d'une commande validée refusée.

## 7. Bilan

Tous les tests ci-dessus ont été exécutés sur la version rendue (Java 8, `javac --release 8`) : résultats conformes aux colonnes
« Résultat attendu ». Cas connu : la console Windows peut afficher des caractères accentués incorrects ; `run.bat` lance
`chcp 65001` et `-Dfile.encoding=UTF-8` pour l'éviter.
