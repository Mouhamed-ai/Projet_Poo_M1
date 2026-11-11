package ui;

import service.ATraiter;
import service.ClientService;
import service.JournalStockService;
import service.PersistanceService;
import service.StockService;
import service.TableauDeBordService;

/** Menu principal du GESTIONNAIRE  -  Responsable : Sokhna (F3). Il délègue aux sous-menus. */
public class MenuGestionnaire extends Menu {

    private final MenuProduits menuProduits;          // sous-menu de Pape Mamadou
    private final MenuClients menuClients;            // sous-menu de Sokhna
    private final MenuTableauDeBord menuTableau;      // sous-menu bonus de Pape Mamadou
    private final PersistanceService persistance;     // bonus H4

    public MenuGestionnaire(StockService stock, ClientService clients, JournalStockService journal,
                            TableauDeBordService tableau, PersistanceService persistance) {
        this.menuProduits = new MenuProduits(stock, journal);
        this.menuClients = new MenuClients(clients);
        this.menuTableau = new MenuTableauDeBord(tableau);
        this.persistance = persistance;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("MENU GESTIONNAIRE");
            System.out.println("1. Produits et stock");
            System.out.println("2. Clients");
            System.out.println("3. Tableau de bord (bonus)");
            System.out.println("4. Sauvegarder les données (bonus)");
            System.out.println("5. Recharger les données (bonus)");
            System.out.println("0. Déconnexion");
            switch (Saisie.lireChoix("Votre choix", 0, 5)) {
                case 1: menuProduits.lancer(); break;
                case 2: menuClients.lancer(); break;
                case 3: menuTableau.lancer(); break;
                case 4: executer(this::sauvegarder); break;
                case 5: executer(this::recharger); break;
                default: continuer = false; break;
            }
        }
    }

    private void sauvegarder() {
        String dossier = Saisie.lireTexteNonVide("Dossier de sauvegarde");
        persistance.sauvegarder(dossier);                       // lance "A FAIRE" tant que H4 n'est pas codé
        System.out.println("Sauvegarde terminée.");
    }

    private void recharger() {
        String dossier = Saisie.lireTexteNonVide("Dossier à relire");
        persistance.charger(dossier);
        System.out.println("Données rechargées.");
    }
}
