package ui;

import service.ATraiter;
import service.ClientService;

/** Sous-menu Clients  -  Responsable : Sokhna (D1 à D5). */
public class MenuClients extends Menu {

    private final ClientService clients;

    public MenuClients(ClientService clients) {
        this.clients = clients;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("CLIENTS");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Modifier un client");
            System.out.println("3. Supprimer un client");
            System.out.println("4. Lister les clients");
            System.out.println("5. Rechercher un client (code / nom)");
            System.out.println("6. Historique des commandes d'un client");
            System.out.println("0. Retour");
            switch (Saisie.lireChoix("Votre choix", 0, 6)) {
                case 1: executer(this::ajouter); break;
                case 2: executer(this::modifier); break;
                case 3: executer(this::supprimer); break;
                case 4: executer(this::lister); break;
                case 5: executer(this::rechercher); break;
                case 6: executer(this::historique); break;
                default: continuer = false; break;
            }
        }
    }

    private void ajouter() { throw ATraiter.pour("D1", "saisir un client puis clients.ajouterClient(...)"); }
    private void modifier() { throw ATraiter.pour("D2", "clients.modifierClient(...)"); }
    private void supprimer() { throw ATraiter.pour("D3", "clients.supprimerClient(...)"); }
    private void lister() { throw ATraiter.pour("D4", "afficher clients.lister()"); }
    private void rechercher() { throw ATraiter.pour("D4", "rechercher par code ou par nom"); }
    private void historique() { throw ATraiter.pour("D5", "afficher clients.historique(code)"); }
}
