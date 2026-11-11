package ui;

import service.ATraiter;
import service.TableauDeBordService;

/** (BONUS H3) Sous-menu Tableau de bord  -  Responsable : Pape Mamadou. */
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
                case 1: executer(() -> { throw ATraiter.pour("H3", "tableau.chiffreAffairesTotal()"); }); break;
                case 2: executer(() -> { throw ATraiter.pour("H3", "tableau.chiffreAffairesParMois()"); }); break;
                case 3: executer(() -> { throw ATraiter.pour("H3", "tableau.top5Produits()"); }); break;
                case 4: executer(() -> { throw ATraiter.pour("H3", "tableau.meilleursClients(5)"); }); break;
                default: continuer = false; break;
            }
        }
    }
}
