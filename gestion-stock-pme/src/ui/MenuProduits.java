package ui;

import java.math.BigDecimal;
import java.util.ArrayList;

import modele.MouvementStock;
import modele.Produit;
import service.JournalStockService;
import service.StockService;

/** Sous-menu Produits et Stock  -  Responsable : Mouhamed (C1 à C7, bonus H1). */
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

    // Chaque méthode ci-dessous = saisie (Saisie.*) + appel du service. Saisie + appel du service.
    private void ajouter() {
        String reference = Saisie.lireTexteNonVide("Référence");
        String designation = Saisie.lireTexteNonVide("Désignation");
        String categorie = Saisie.lireTexteNonVide("Catégorie");
        BigDecimal prix = Saisie.lireDecimalPositif("Prix unitaire HT (FCFA)");
        int quantite = Saisie.lireEntier("Quantité en stock");
        int seuil = Saisie.lireEntier("Seuil d'alerte");
        stock.ajouterProduit(new Produit(reference, designation, categorie, prix, quantite, seuil));
        System.out.println("Produit " + reference + " ajouté.");
    }

    private void modifier() {
        String reference = Saisie.lireTexteNonVide("Référence du produit à modifier");
        Produit p = stock.obtenir(reference);                                  // erreur si introuvable
        System.out.println("Valeurs actuelles : " + p);
        String designation = Saisie.lireTexteNonVide("Nouvelle désignation");
        String categorie = Saisie.lireTexteNonVide("Nouvelle catégorie");
        BigDecimal prix = Saisie.lireDecimalPositif("Nouveau prix unitaire HT (FCFA)");
        int seuil = Saisie.lireEntier("Nouveau seuil d'alerte");
        stock.modifierProduit(reference, designation, categorie, prix, seuil);
        System.out.println("Produit modifié (les lignes de commande existantes gardent leur prix).");
    }

    private void supprimer() {
        String reference = Saisie.lireTexteNonVide("Référence du produit à supprimer");
        stock.obtenir(reference);                                              // on vérifie avant de demander confirmation
        if (!Saisie.lireOuiNon("Confirmer la suppression de " + reference)) {
            System.out.println("Suppression annulée.");
            return;
        }
        ArrayList<String> touchees = stock.supprimerProduit(reference);
        System.out.println("Produit " + reference + " supprimé.");
        if (!touchees.isEmpty()) {
            System.out.println("Ses lignes ont été retirées des commandes en cours : " + touchees);
        }
    }

    private void reapprovisionner() {
        String reference = Saisie.lireTexteNonVide("Référence du produit");
        int quantite = Saisie.lireEntierPositif("Quantité à ajouter");
        stock.reapprovisionner(reference, quantite);
        System.out.println("Nouveau stock de " + reference + " : " + stock.obtenir(reference).getQuantiteStock());
    }

    private void lister() {
        System.out.println("Tri : 1. désignation   2. prix   3. quantité en stock");
        int choix = Saisie.lireChoix("Votre choix", 1, 3);
        ArrayList<Produit> liste = (choix == 1) ? stock.listerParDesignation()
                : (choix == 2) ? stock.listerParPrix() : stock.listerParQuantite();
        afficherProduits(liste);
    }

    private void rechercher() {
        System.out.println("Critère : 1. référence exacte   2. fragment de désignation   3. catégorie");
        int choix = Saisie.lireChoix("Votre choix", 1, 3);
        if (choix == 3 && !stock.getCategories().isEmpty()) {
            System.out.println("Catégories existantes : " + stock.getCategories());
        }
        String valeur = Saisie.lireTexteNonVide("Valeur recherchée");
        ArrayList<Produit> liste = (choix == 1) ? stock.rechercherParReference(valeur)
                : (choix == 2) ? stock.rechercherParDesignation(valeur) : stock.rechercherParCategorie(valeur);
        afficherProduits(liste);
    }

    private void alertes() {
        ArrayList<Produit> liste = stock.produitsEnAlerte();
        if (liste.isEmpty()) {
            System.out.println("Aucun produit en alerte de stock.");
        } else {
            System.out.println("Produits dont le stock est inférieur ou égal au seuil :");
            afficherProduits(liste);
        }
    }

    private void journal() {
        ArrayList<MouvementStock> liste = journal.lister();
        if (liste.isEmpty()) {
            System.out.println("Aucun mouvement de stock enregistré.");
            return;
        }
        for (MouvementStock m : liste) {
            System.out.println(m);
        }
    }

    /** Affiche une liste de produits, une ligne par produit, alignée. */
    private void afficherProduits(ArrayList<Produit> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun produit.");
            return;
        }
        System.out.println(String.format("%-6s %-30s %-14s %20s %7s %6s", "Réf.", "Désignation", "Catégorie",
                "Prix unit. HT", "Stock", "Seuil"));
        for (Produit p : liste) {
            System.out.println(String.format("%-6s %-30s %-14s %20s %7d %6d%s", p.getReference(), p.getDesignation(),
                    p.getCategorie(), Affichage.montant(p.getPrixUnitaireHT()), p.getQuantiteStock(),
                    p.getSeuilAlerte(), p.estEnAlerte() ? "  ALERTE" : ""));
        }
        System.out.println(liste.size() + " produit(s).");
    }
}
