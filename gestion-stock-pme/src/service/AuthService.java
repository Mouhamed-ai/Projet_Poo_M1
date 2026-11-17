package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import modele.Gestionnaire;
import modele.Utilisateur;
import modele.Vendeur;

/**
 * F2 - Authentification (comptes définis en dur, sujet section 2). VERSION AMÉLIORÉE (Sokhna).
 *
 * Améliorations par rapport à la version de base :
 *  - les mots de passe ne sont plus conservés en clair : on garde leur empreinte SHA-256 ;
 *  - le login est insensible à la casse et aux espaces autour ("  Vendeur " = "vendeur") ;
 *  - entrées null ou vides refusées proprement (jamais de NullPointerException) ;
 *  - ajout d'un compte par enregistrer() : refuse un login déjà pris.
 *
 * Collections : deux HashMap<String, ...> indexées par login normalisé (accès direct, O(1)).
 * La limite des 3 essais est gérée par Main.connexion(), la signature authentifier() est inchangée.
 */
public class AuthService {

    private final HashMap<String, String> empreintes = new HashMap<>();        // login -> SHA-256(mot de passe)
    private final HashMap<String, Utilisateur> utilisateurs = new HashMap<>(); // login -> utilisateur

    public AuthService() {
        enregistrer(new Gestionnaire("gestionnaire", "Gestionnaire"), "gest123");  // compte gestionnaire
        enregistrer(new Vendeur("vendeur", "Vendeur"), "vend123");                 // compte vendeur
    }

    /** Ajoute un compte. Refuse un login déjà utilisé ou un mot de passe vide. */
    public void enregistrer(Utilisateur utilisateur, String motDePasse) {
        if (utilisateur == null || motDePasse == null || motDePasse.isEmpty()) {
            throw new IllegalArgumentException("Compte invalide : utilisateur et mot de passe obligatoires.");
        }
        String cle = normaliser(utilisateur.getLogin());
        if (utilisateurs.containsKey(cle)) {
            throw new IllegalArgumentException("Login déjà utilisé : " + utilisateur.getLogin());
        }
        utilisateurs.put(cle, utilisateur);
        empreintes.put(cle, hacher(motDePasse));
    }

    /** Renvoie l'utilisateur si login ET mot de passe sont corrects, sinon null (le menu affiche le refus). */
    public Utilisateur authentifier(String login, String motDePasse) {
        if (login == null || motDePasse == null) {
            return null;
        }
        String cle = normaliser(login);
        String attendue = empreintes.get(cle);                       // null si login inconnu
        if (attendue != null && attendue.equals(hacher(motDePasse))) {
            return utilisateurs.get(cle);
        }
        return null;
    }

    /** Login sans espaces autour et en minuscules. */
    private String normaliser(String login) {
        return login.trim().toLowerCase();
    }

    /** Empreinte SHA-256 en hexadécimal (le mot de passe lui-même n'est jamais stocké). */
    private String hacher(String texte) {
        try {
            byte[] octets = MessageDigest.getInstance("SHA-256").digest(texte.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : octets) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);   // ne se produit pas en Java standard
        }
    }
}
