package ui;

import service.CommandeService;
import service.ExportService;
import service.FactureService;

/** Menu principal du VENDEUR  -  Responsable : Moustapha (F4). */
public class MenuVendeur extends Menu {

    private final MenuCommandes menuCommandes;        // sous-menu d'Aminata
    private final MenuFacturation menuFacturation;    // sous-menu de Moustapha

    public MenuVendeur(CommandeService commandes, FactureService factures, ExportService export) {
        this.menuCommandes = new MenuCommandes(commandes);
        this.menuFacturation = new MenuFacturation(commandes, factures, export);
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("MENU VENDEUR");
            System.out.println("1. Commandes");
            System.out.println("2. Facturation");
            System.out.println("0. Déconnexion");
            switch (Saisie.lireChoix("Votre choix", 0, 2)) {
                case 1: menuCommandes.lancer(); break;
                case 2: menuFacturation.lancer(); break;
                default: continuer = false; break;
            }
        }
    }
}
