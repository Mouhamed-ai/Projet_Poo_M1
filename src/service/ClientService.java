package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;

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
    public void ajouterClient(Client client) {  
        if (client == null) {
            throw new ErreurMetier("Le client ne peut pas être null.");
        }
        String code = client.getCode();
        if (code == null || code.trim().isEmpty()) {
            throw new ErreurMetier("Le code client est obligatoire.");
        } 
        if (clients.containsKey(code)) {
            throw new ErreurMetier("Ce code client existe déjà : " + code);
        }
        clients.put(code, client); }

    /** D2 - Modifier nom, téléphone, email, type. Le CODE ne change pas. */
    public void modifierClient(String code, String nom, String telephone, String email, TypeClient type) {
       Client client = obtenir(code);

        client.setNom(nom);
        client.setTelephone(telephone);
        client.setEmail(email);
        client.setType(type);
    }

    /** D3 - Refuser si le client a une commande en cours ou validée : commandeService.clientADesCommandesActives(code). */
    public void supprimerClient(String code) { 
        Client client = obtenir(code);

        if (commandeService != null &&
                commandeService.clientADesCommandesActives(code)) {
            throw new ErreurMetier(
                "Impossible de supprimer ce client : il possède des commandes actives."
            );
        }

        clients.remove(code);
     }

    /** Renvoie le client ou lance ErreurMetier("Client introuvable : ..."). Utilisé par CommandeService. */
    public Client obtenir(String code) {
    Client client = clients.get(code);

    if (client == null) {
        throw new ErreurMetier("Client introuvable : " + code);
    }

    return client;
   }

    /** D4 - Tous les clients (copie triée par nom, par exemple). */
    public ArrayList<Client> lister() {
        ArrayList<Client> resultat = new ArrayList<>(clients.values());

        resultat.sort(
            Comparator.comparing(Client::getNom,
                String.CASE_INSENSITIVE_ORDER)
        );

        return resultat;
     }

    /** D4 - Recherche par code exact. Liste vide si introuvable. */
    public ArrayList<Client> rechercherParCode(String code) { 
        ArrayList<Client> resultat = new ArrayList<>();

        if (clients.containsKey(code)) {
            resultat.add(clients.get(code));
        }

        return resultat;

     }

    /** D4 - Recherche par fragment du nom (insensible à la casse). */
    public ArrayList<Client> rechercherParNom(String fragment) { 
         ArrayList<Client> resultat = new ArrayList<>();

        if (fragment == null) {
            return resultat;
        }

        for (Client client : clients.values()) {
            if (client.getNom() != null &&
                client.getNom().toLowerCase(java.util.Locale.ROOT)
                    .contains(fragment.toLowerCase(java.util.Locale.ROOT))) {

                resultat.add(client);
            }
        }

        resultat.sort(
            Comparator.comparing(Client::getNom,
                String.CASE_INSENSITIVE_ORDER)
        );

        return resultat;
    }

    /** D5 - Historique : se base sur commandeService.commandesDuClient(code). Liste vide possible. */
    public ArrayList<Commande> historique(String code) { 
        obtenir(code);

        if (commandeService == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(commandeService.commandesDuClient(code)); }
}
