package service;

import java.util.HashMap;

import modele.Gestionnaire;
import modele.Utilisateur;
import modele.Vendeur;

/**
 * F2 - Authentification simple (identifiants définis en dur, sujet section 2).
 * VERSION DE BASE FOURNIE pour que le squelette tourne ; Sokhna peut l'améliorer.
 * Collections : deux HashMap<String, ...> indexées par login.
 */
public class AuthService {

    private final HashMap<String, String> motsDePasse = new HashMap<>();      // login -> mot de passe
    private final HashMap<String, Utilisateur> utilisateurs = new HashMap<>(); // login -> utilisateur

    public AuthService() {
        enregistrer(new Gestionnaire("gestionnaire", "Gestionnaire"), "gest123");  // compte gestionnaire
        enregistrer(new Vendeur("vendeur", "Vendeur"), "vend123");                 // compte vendeur
    }

    private void enregistrer(Utilisateur utilisateur, String motDePasse) {
        utilisateurs.put(utilisateur.getLogin(), utilisateur);       // on retrouve l'utilisateur par son login
        motsDePasse.put(utilisateur.getLogin(), motDePasse);         // et son mot de passe aussi
    }

    /** Renvoie l'utilisateur si login ET mot de passe sont corrects, sinon null. */
    public Utilisateur authentifier(String login, String motDePasse) {
        String attendu = motsDePasse.get(login);                     // null si login inconnu
        if (attendu != null && attendu.equals(motDePasse)) {
            return utilisateurs.get(login);
        }
        return null;                                                 // refus : le menu affichera un message
    }
}
