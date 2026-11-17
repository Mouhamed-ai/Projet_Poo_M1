package ui;

import java.util.ArrayList;

import modele.Client;
import modele.Commande;
import modele.TypeClient;
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

    private void ajouter() {
        String code = Saisie.lireTexteNonVide("Code client");
        String nom = Saisie.lireTexteNonVide("Nom");
        String telephone = Saisie.lireTexteNonVide("Téléphone");
        String email = Saisie.lireTexteNonVide("Email");
        TypeClient type = lireType();
        clients.ajouterClient(new Client(code, nom, telephone, email, type));
        System.out.println("Client " + code + " ajouté.");
    }

    private void modifier() {
        String code = Saisie.lireTexteNonVide("Code du client à modifier");
        Client c = clients.obtenir(code);                                      // erreur si introuvable
        System.out.println("Valeurs actuelles : " + c);
        String nom = Saisie.lireTexteNonVide("Nouveau nom");
        String telephone = Saisie.lireTexteNonVide("Nouveau téléphone");
        String email = Saisie.lireTexteNonVide("Nouvel email");
        TypeClient type = lireType();
        clients.modifierClient(code, nom, telephone, email, type);
        System.out.println("Client modifié.");
    }

    private void supprimer() {
        String code = Saisie.lireTexteNonVide("Code du client à supprimer");
        clients.obtenir(code);
        if (!Saisie.lireOuiNon("Confirmer la suppression de " + code)) {
            System.out.println("Suppression annulée.");
            return;
        }
        clients.supprimerClient(code);
        System.out.println("Client " + code + " supprimé.");
    }

    private void lister() {
        afficherClients(clients.lister());
    }

    private void rechercher() {
        System.out.println("Critère : 1. code exact   2. fragment du nom");
        int choix = Saisie.lireChoix("Votre choix", 1, 2);
        String valeur = Saisie.lireTexteNonVide("Valeur recherchée");
        afficherClients(choix == 1 ? clients.rechercherParCode(valeur) : clients.rechercherParNom(valeur));
    }

    private void historique() {
        String code = Saisie.lireTexteNonVide("Code du client");
        ArrayList<Commande> liste = clients.historique(code);
        if (liste.isEmpty()) {
            System.out.println("Aucune commande pour ce client.");
            return;
        }
        System.out.println(String.format("%-8s %-12s %-9s %7s %22s", "Numéro", "Date", "Statut", "Lignes", "Total HT"));
        for (Commande c : liste) {
            System.out.println(String.format("%-8s %-12s %-9s %7d %22s", c.getNumero(), c.getDate(), c.getStatut(),
                    c.getLignes().size(), Affichage.montant(c.calculerTotalHT())));
        }
    }

    private TypeClient lireType() {
        int choix = Saisie.lireChoix("Type : 1 = Particulier, 2 = Entreprise", 1, 2);
        return (choix == 1) ? TypeClient.PARTICULIER : TypeClient.ENTREPRISE;
    }

    private void afficherClients(ArrayList<Client> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun client.");
            return;
        }
        System.out.println(String.format("%-6s %-24s %-12s %-12s %-32s", "Code", "Nom", "Type", "Téléphone", "Email"));
        for (Client c : liste) {
            System.out.println(String.format("%-6s %-24s %-12s %-12s %-32s", c.getCode(), c.getNom(), c.getType(),
                    c.getTelephone(), c.getEmail()));
        }
        System.out.println(liste.size() + " client(s).");
    }
}
