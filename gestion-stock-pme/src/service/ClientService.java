package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import modele.Client;
import modele.Commande;
import modele.ErreurMetier;
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
            throw new ErreurMetier("Le client est obligatoire.");
        }
        if (clients.containsKey(client.getCode())) {                       // règle 1 : code unique
            throw new ErreurMetier("Le code client " + client.getCode() + " existe déjà.");
        }
        clients.put(client.getCode(), client);
    }

    /** D2 - Modifier nom, téléphone, email, type. Le CODE ne change pas. */
    public void modifierClient(String code, String nom, String telephone, String email, TypeClient type) {
        Client c = obtenir(code);
        c.setNom(nom);                 // chaque setter contrôle sa valeur (email avec @, etc.)
        c.setTelephone(telephone);
        c.setEmail(email);
        c.setType(type);
    }

    /** D3 - Refuser si le client a une commande en cours ou validée (règle 6 et décision du groupe). */
    public void supprimerClient(String code) {
        obtenir(code);                                                     // erreur si introuvable
        if (commandeService != null && commandeService.clientADesCommandesActives(code)) {
            throw new ErreurMetier("Suppression refusée : le client " + code
                    + " a des commandes en cours ou validées.");
        }
        clients.remove(code);
    }

    /** Renvoie le client ou lance ErreurMetier("Client introuvable : ..."). Utilisé par CommandeService. */
    public Client obtenir(String code) {
        Client c = (code == null) ? null : clients.get(code.trim());
        if (c == null) {
            throw new ErreurMetier("Client introuvable : " + code);
        }
        return c;
    }

    /** D4 - Tous les clients (copie triée par nom). */
    public ArrayList<Client> lister() {
        ArrayList<Client> liste = new ArrayList<>(clients.values());
        Collections.sort(liste, new Comparator<Client>() {
            @Override
            public int compare(Client a, Client b) {
                return a.getNom().compareToIgnoreCase(b.getNom());
            }
        });
        return liste;
    }

    /** D4 - Recherche par code exact. Liste vide si introuvable. */
    public ArrayList<Client> rechercherParCode(String code) {
        ArrayList<Client> resultat = new ArrayList<>();
        Client c = (code == null) ? null : clients.get(code.trim());
        if (c != null) {
            resultat.add(c);
        }
        return resultat;
    }

    /** D4 - Recherche par fragment du nom (insensible à la casse). */
    public ArrayList<Client> rechercherParNom(String fragment) {
        ArrayList<Client> resultat = new ArrayList<>();
        String motif = (fragment == null) ? "" : fragment.trim().toLowerCase();
        for (Client c : lister()) {
            if (c.getNom().toLowerCase().contains(motif)) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    /** D5 - Historique : se base sur commandeService.commandesDuClient(code). Liste vide possible. */
    public ArrayList<Commande> historique(String code) {
        obtenir(code);                                                     // client inconnu : erreur claire
        return commandeService.commandesDuClient(code);
    }

    /** Vide les clients (utilisé seulement par le rechargement d'une sauvegarde, bonus H4). */
    public void vider() {
        clients.clear();
    }
}
