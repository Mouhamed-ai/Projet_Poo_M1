=====================================================================
 PROJET POO JAVA - GESTION DE STOCK ET DE FACTURATION D'UNE PME
 Master 1 MIAGE - 2025/2026
=====================================================================

1. MEMBRES DU GROUPE
   - Aminata Sambe                     (code etudiant : ............)
   - Pape Mamadou Sall                 (code etudiant : ............)
   - Sokhna Mbacké Diakhaté            (code etudiant : ............)
   - Mouhammadou Moustapha Sambe       (code etudiant : ............)

2. COMPILATION ET EXECUTION (Java 8 ou superieur, aucune bibliotheque externe)
   Windows :        compile.bat   puis   run.bat
   Linux / macOS :  sh compile.sh puis   sh run.sh
   A la main :      javac -encoding UTF-8 -d out <tous les .java de src>
                    java -Dfile.encoding=UTF-8 -cp out Main

3. IDENTIFIANTS DE CONNEXION
   Gestionnaire : gestionnaire / gest123   (produits, stock, clients, tableau de bord)
   Vendeur      : vendeur / vend123        (commandes, facturation)

4. JEU DE DEMONSTRATION (charge au demarrage)
   10 produits, 5 clients, 3 commandes (CMD001 et CMD002 validees, CMD003 en cours).

5. ARCHITECTURE
   modele/   classes metier (Produit, Client, Commande, LigneCommande, Facture, ...)
   service/  regles de gestion et collections (StockService, ClientService, ...)
   ui/       menus console (Scanner, via la classe Saisie)
   donnees/  jeu de demonstration

6. JUSTIFICATION DES COLLECTIONS
   | Collection                           | Ou                   | Pourquoi ce choix |
   | HashMap<String, Produit>             | StockService         | acces direct par reference (O(1)), unicite des references |
   | TreeSet<String>                      | StockService         | categories sans doublon, triees alphabetiquement |
   | ArrayList<Produit>                   | StockService         | listes triees/filtrees (Comparator + Collections.sort), alertes de stock |
   | HashMap<String, Client>              | ClientService        | acces par code client, unicite du code |
   | HashMap<String, Commande>            | CommandeService      | acces direct par numero de commande (CMD001...) |
   | HashMap<String, ArrayList<Commande>> | CommandeService      | historique d'un client : code client -> ses commandes, sans parcourir toutes les commandes |
   | ArrayList<LigneCommande>             | Commande             | ordre d'ajout conserve, parcours pour les totaux, retrait par Iterator |
   | HashMap<String, Utilisateur/String>  | AuthService          | recherche du compte par identifiant a la connexion |
   | Iterator                             | CommandeService      | suppression d'une ligne / d'un produit pendant le parcours (sans ConcurrentModificationException) |
   | (bonus) ArrayList<MouvementStock>    | JournalStockService  | historique chronologique (ordre d'insertion) |
   | (bonus) TreeMap<String, BigDecimal>  | TableauDeBordService | CA par mois (cle AAAA-MM) automatiquement trie par date |

7. DECISIONS DE CONCEPTION
   - Montants en BigDecimal, arrondi a 2 decimales (HALF_UP) pour les calculs intermediaires.
   - Facture : total HT -> remise 5 % (ENTREPRISE et HT > 500 000 FCFA) -> HT apres remise
     -> TVA 18 % sur le HT apres remise -> TTC exact -> ligne "Arrondi" -> total TTC a payer.
   - Le TTC a payer est arrondi au multiple de 5 F le plus proche (milieu vers le haut), car le franc
     se paie en multiples de 5 ou 10. Exemple : TTC exact 1 050 937,50 -> arrondi +2,50 -> 1 050 940.
   - Stock controle des l'ajout ou la modification d'une ligne (quantite totale <= stock), puis re-controle
     a la validation.
   - Le prix de chaque ligne est fige au moment de l'ajout dans la commande.
   - Suppression d'un produit : refusee s'il figure dans une commande VALIDEE ; autorisee s'il figure dans
     des commandes EN_COURS (ses lignes sont retirees et les commandes touchees sont affichees).
   - Suppression d'un client : refusee s'il a une commande EN_COURS ou VALIDEE.
   - Validation d'une commande en deux passes : verification de tous les stocks, puis decrementation.
   - Modification du type d'un client : autorisee ; elle n'affecte que les NOUVELLES factures calculees.
   - Sauvegarde CSV : 4 fichiers (produits, clients, commandes, lignes), separateur ';', lecture par StringTokenizer.
   - Chiffre d'affaires (bonus) = HT apres remise des commandes VALIDEES, par mois de la commande.

8. FONCTIONNALITES BONUS REALISEES  (cocher)
   [x] Journal des mouvements de stock   [x] Export de facture   [x] Tableau de bord   [x] Sauvegarde CSV

9. TESTS
   docs/PLAN_DE_TEST.md : plan de test complet (connexion, commandes, facturation, produits, clients, bonus).
   tests/ : scenarios de saisie rejouables (java ... Main < tests/scenario_vendeur.txt) et VerifRegles.java (25 controles automatiques).

10. VIDEO DE DEMONSTRATION
   Incluse dans l'archive : ........   ou lien : ........................
