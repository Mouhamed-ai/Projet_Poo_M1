package modele;

import java.math.BigDecimal;

/**
 * Petites vérifications réutilisables par les classes du modèle.
 * Centraliser évite de recopier les mêmes "if" dans dix classes (principe DRY :
 * Don't Repeat Yourself).
 */
public final class Controles {

    private Controles() { } // classe utilitaire : on interdit d'en créer des objets

    /** Refuse null ou vide ; renvoie le texte sans espaces autour. */
    public static String texteNonVide(String valeur, String nomChamp) {
        if (valeur == null || valeur.trim().isEmpty()) {            // null OU rempli d'espaces
            throw new ErreurMetier(nomChamp + " ne peut pas être vide."); // opération refusée
        }
        return valeur.trim();                                        // texte nettoyé
    }

    /** Règle 2 : le prix unitaire doit être STRICTEMENT positif. */
    public static BigDecimal prixStrictementPositif(BigDecimal prix) {
        if (prix == null || prix.signum() <= 0) {                    // signum() : -1, 0 ou 1
            throw new ErreurMetier("Le prix unitaire doit être strictement positif.");
        }
        return prix;
    }

    /** Refuse un entier négatif (0 accepté). */
    public static int entierPositifOuNul(int valeur, String nomChamp) {
        if (valeur < 0) {
            throw new ErreurMetier(nomChamp + " ne peut pas être négatif(ve).");
        }
        return valeur;
    }

    /** Refuse 0 et les négatifs. */
    public static int entierStrictementPositif(int valeur, String nomChamp) {
        if (valeur <= 0) {
            throw new ErreurMetier(nomChamp + " doit être strictement positif(ve).");
        }
        return valeur;
    }
}
