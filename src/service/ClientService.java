package service;

import java.util.ArrayList;
import java.util.HashMap;

import modele.Client;
import modele.Commande;
import modele.TypeClient;

/**
 * MODULE CLIENTS  -  Responsable : Sokhna (tâches D1 à D5).
 *
 * Collection : HashMap<String, Client> clients  (accès direct par code, unicité de la clé : règle 1).
 */
public class ClientService {

    private final HashMap<String, Client> clients = new HashMap<>();   // code -> client
    private CommandeService commandeService;                           // branché par Main (règle 6)

    public void setCommandeService(CommandeService commandeService) { this.commandeService = commandeService; }

    /** D1 - Refuser un code déjà utilisé (containsKey). */
    public void ajouterClient(Client client) { throw ATraiter.pour("D1", "ClientService.ajouterClient"); }

    /** D2 - Modifier nom, téléphone, email, type. Le CODE ne change pas. */
    public void modifierClient(String code, String nom, String telephone, String email, TypeClient type) {
        throw ATraiter.pour("D2", "ClientService.modifierClient");
    }

    /** D3 - Refuser si le client a une commande en cours ou validée : commandeService.clientADesCommandesActives(code). */
    public void supprimerClient(String code) { throw ATraiter.pour("D3", "ClientService.supprimerClient"); }

    /** Renvoie le client ou lance ErreurMetier("Client introuvable : ..."). Utilisé par CommandeService. */
    public Client obtenir(String code) { throw ATraiter.pour("D1", "ClientService.obtenir"); }

    /** D4 - Tous les clients (copie triée par nom, par exemple). */
    public ArrayList<Client> lister() { throw ATraiter.pour("D4", "ClientService.lister"); }

    /** D4 - Recherche par code exact. Liste vide si introuvable. */
    public ArrayList<Client> rechercherParCode(String code) { throw ATraiter.pour("D4", "recherche par code"); }

    /** D4 - Recherche par fragment du nom (insensible à la casse). */
    public ArrayList<Client> rechercherParNom(String fragment) { throw ATraiter.pour("D4", "recherche par nom"); }

    /** D5 - Historique : se base sur commandeService.commandesDuClient(code). Liste vide possible. */
    public ArrayList<Commande> historique(String code) { throw ATraiter.pour("D5", "historique des commandes"); }
}
