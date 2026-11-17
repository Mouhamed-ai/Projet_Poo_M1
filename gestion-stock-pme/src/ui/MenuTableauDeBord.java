package ui;

import java.math.BigDecimal;
import java.util.Map;

import service.TableauDeBordService;

/** (BONUS H3) Sous-menu Tableau de bord  -  Responsable : Mouhamed. */
public class MenuTableauDeBord extends Menu {

    private final TableauDeBordService tableau;

    public MenuTableauDeBord(TableauDeBordService tableau) {
        this.tableau = tableau;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("TABLEAU DE BORD (bonus)");
            System.out.println("1. Chiffre d'affaires total");
            System.out.println("2. Chiffre d'affaires par mois");
            System.out.println("3. Top 5 des produits vendus");
            System.out.println("4. Meilleurs clients");
            System.out.println("0. Retour");
            switch (Saisie.lireChoix("Votre choix", 0, 4)) {
                case 1: executer(this::caTotal); break;
                case 2: executer(this::caParMois); break;
                case 3: executer(this::top5); break;
                case 4: executer(this::meilleursClients); break;
                default: continuer = false; break;
            }
        }
    }

    private void caTotal() {
        System.out.println("Chiffre d'affaires total (HT après remise, commandes validées) : "
                + Affichage.montant(tableau.chiffreAffairesTotal()));
    }

    private void caParMois() {
        if (tableau.chiffreAffairesParMois().isEmpty()) {
            System.out.println("Aucune commande validée.");
        }
        for (Map.Entry<String, BigDecimal> e : tableau.chiffreAffairesParMois().entrySet()) {
            System.out.println(String.format("%-8s %22s", e.getKey(), Affichage.montant(e.getValue())));
        }
    }

    private void top5() {
        int rang = 1;
        for (Map.Entry<String, Integer> e : tableau.top5Produits()) {
            System.out.println(rang++ + ". " + e.getKey() + " : " + e.getValue() + " unité(s) vendue(s)");
        }
        if (rang == 1) {
            System.out.println("Aucune vente.");
        }
    }

    private void meilleursClients() {
        int rang = 1;
        for (Map.Entry<String, BigDecimal> e : tableau.meilleursClients(5)) {
            System.out.println(rang++ + ". " + e.getKey() + " : " + Affichage.montant(e.getValue()));
        }
        if (rang == 1) {
            System.out.println("Aucune vente.");
        }
    }
}
