=====================================================================
 PROJET POO JAVA - GESTION DE STOCK ET DE FACTURATION D'UNE PME
 Master 1 MIAGE - 2025/2026
=====================================================================

1. MEMBRES DU GROUPE
   - Aminata Sambe                     (code etudiant : .............)
   - Pape Mamadou Sall                 (code etudiant : .............)
   - Sokhna Mbacké Diakhaté            (code etudiant : .............)
   - Mouhammadou Moustapha Sambe       (code etudiant : .............)

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

6. JUSTIFICATION DES COLLECTIONS  (A COMPLETER par chaque responsable de module)
   | Collection                          | Ou                | Pourquoi ce choix |
   | HashMap<String, Produit>            | StockService      | ...               |
   | TreeSet<String>                     | StockService      | ...               |
   | ArrayList<Produit>                  | StockService      | ...               |
   | HashMap<String, Client>             | ClientService     | ...               |
   | HashMap<String, Commande>           | CommandeService   | ...               |
   | HashMap<String, ArrayList<Commande>>| CommandeService   | ...               |
   | ArrayList<LigneCommande>            | Commande          | ...               |
   | HashMap<String, String/Utilisateur> | AuthService       | ...               |
   | Iterator                            | CommandeService   | suppression d'une ligne pendant le parcours |
   | (bonus) ArrayList<MouvementStock>   | JournalStockService | ...             |
   | (bonus) TreeMap<String, BigDecimal> | TableauDeBordService| ...             |

7. DECISIONS DE CONCEPTION
   - Montants en BigDecimal, arrondi a 2 decimales (HALF_UP).
   - Facture : total HT -> remise 5 % (ENTREPRISE et HT > 500 000 FCFA) -> HT apres remise
     -> TVA 18 % sur le HT apres remise -> total TTC.
   - Le prix de chaque ligne est fige au moment de l'ajout dans la commande.
   - Suppression d'un produit / client refusee s'il figure dans une commande EN_COURS ou VALIDEE.
   - Validation d'une commande en deux passes : verification de tous les stocks, puis decrementation.
   - Chiffre d'affaires (bonus) = HT apres remise des commandes VALIDEES, par mois de la commande.

8. FONCTIONNALITES BONUS REALISEES  (cocher)
   [ ] Journal des mouvements de stock   [ ] Export de facture   [ ] Tableau de bord   [ ] Sauvegarde CSV

9. VIDEO DE DEMONSTRATION
   Incluse dans l'archive : ........   ou lien : ........................
