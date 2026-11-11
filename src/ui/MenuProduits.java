package ui;

import service.ATraiter;
import service.JournalStockService;
import service.StockService;

/** Sous-menu Produits et Stock  -  Responsable : Pape Mamadou (C1 à C7, bonus H1). */
public class MenuProduits extends Menu {

    private final StockService stock;
    private final JournalStockService journal;   // bonus H1

    public MenuProduits(StockService stock, JournalStockService journal) {
        this.stock = stock;
        this.journal = journal;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("PRODUITS ET STOCK");
            System.out.println("1. Ajouter un produit");
            System.out.println("2. Modifier un produit");
            System.out.println("3. Supprimer un produit");
            System.out.println("4. Réapprovisionner un produit");
            System.out.println("5. Lister les produits (tri désignation / prix / quantité)");
            System.out.println("6. Rechercher un produit (référence / désignation / catégorie)");
            System.out.println("7. Produits en alerte de stock");
            System.out.println("8. Journal des mouvements de stock (bonus)");
            System.out.println("0. Retour");
            switch (Saisie.lireChoix("Votre choix", 0, 8)) {
                case 1: executer(this::ajouter); break;
                case 2: executer(this::modifier); break;
                case 3: executer(this::supprimer); break;
                case 4: executer(this::reapprovisionner); break;
                case 5: executer(this::lister); break;
                case 6: executer(this::rechercher); break;
                case 7: executer(this::alertes); break;
                case 8: executer(this::journal); break;
                default: continuer = false; break;     // 0 : on sort de la boucle
            }
        }
    }

    // Chaque méthode ci-dessous = saisie (Saisie.*) + appel du service. À écrire par Pape Mamadou.
    private void ajouter() { throw ATraiter.pour("C1", "saisir un produit puis stock.ajouterProduit(...)"); }
    private void modifier() { throw ATraiter.pour("C2", "saisir les nouvelles valeurs puis stock.modifierProduit(...)"); }
    private void supprimer() { throw ATraiter.pour("C3", "stock.supprimerProduit(...)"); }
    private void reapprovisionner() { throw ATraiter.pour("C4", "stock.reapprovisionner(...)"); }
    private void lister() { throw ATraiter.pour("C5", "choisir le tri puis afficher la liste"); }
    private void rechercher() { throw ATraiter.pour("C6", "choisir le critère puis afficher les résultats"); }
    private void alertes() { throw ATraiter.pour("C7", "afficher stock.produitsEnAlerte()"); }
    private void journal() { throw ATraiter.pour("H1", "afficher journal.lister()"); }
}
