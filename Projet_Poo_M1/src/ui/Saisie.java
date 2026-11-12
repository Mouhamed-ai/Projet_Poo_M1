package ui;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * F1 - Lecture SÉCURISÉE au clavier (fournie, utilisée par TOUS les menus).
 * Principe : on lit TOUJOURS avec nextLine() (une ligne entière) puis on convertit nous-mêmes.
 * Pourquoi ? nextInt() laisse un "retour à la ligne" dans le tampon et fausse la lecture suivante,
 * et il plante si l'utilisateur tape du texte.
 * Un seul Scanner pour tout le programme : en créer plusieurs sur System.in cause des bugs.
 */
public final class Saisie {

    private static final Scanner SCANNER = new Scanner(System.in);   // l'unique Scanner

    private Saisie() { }

    /** Lit une ligne (peut être vide). */
    public static String lireTexte(String message) {
        System.out.print(message + " : ");                 // invite affichée sans retour à la ligne
        if (!SCANNER.hasNextLine()) {                      // fin de l'entrée (ex. Ctrl+D) : on quitte proprement
            System.out.println("\nFin de la saisie. Au revoir.");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();                  // trim() : enlève les espaces autour
    }

    /** Répète la question tant que la réponse est vide. */
    public static String lireTexteNonVide(String message) {
        String texte = lireTexte(message);
        while (texte.isEmpty()) {                          // boucle de validation
            System.out.println("  Saisie obligatoire, recommencez.");
            texte = lireTexte(message);
        }
        return texte;
    }

    /** Répète la question tant que ce n'est pas un entier valide (test T14). */
    public static int lireEntier(String message) {
        while (true) {
            String texte = lireTexte(message);
            try {
                return Integer.parseInt(texte);            // conversion : lance NumberFormatException si invalide
            } catch (NumberFormatException e) {
                System.out.println("  '" + texte + "' n'est pas un nombre entier, recommencez.");
            }
        }
    }

    /** Entier strictement positif (quantités). */
    public static int lireEntierPositif(String message) {
        int valeur = lireEntier(message);
        while (valeur <= 0) {
            System.out.println("  La valeur doit être strictement positive.");
            valeur = lireEntier(message);
        }
        return valeur;
    }

    /** Entier compris entre min et max (choix de menu). */
    public static int lireChoix(String message, int min, int max) {
        int choix = lireEntier(message);
        while (choix < min || choix > max) {
            System.out.println("  Choix invalide : entrez un nombre entre " + min + " et " + max + ".");
            choix = lireEntier(message);
        }
        return choix;
    }

    /** Décimal strictement positif (prix). Accepte la virgule ou le point. */
    public static BigDecimal lireDecimalPositif(String message) {
        while (true) {
            String texte = lireTexte(message).replace(',', '.');   // "3,5" -> "3.5"
            try {
                BigDecimal valeur = new BigDecimal(texte);
                if (valeur.signum() > 0) {
                    return valeur;
                }
                System.out.println("  La valeur doit être strictement positive.");
            } catch (NumberFormatException e) {
                System.out.println("  '" + texte + "' n'est pas un nombre valide, recommencez.");
            }
        }
    }

    /** Question oui/non. */
    public static boolean lireOuiNon(String message) {
        while (true) {
            String r = lireTexte(message + " (o/n)").toLowerCase();
            if (r.equals("o") || r.equals("oui")) { return true; }
            if (r.equals("n") || r.equals("non")) { return false; }
            System.out.println("  Répondez par o ou n.");
        }
    }

    /** Pause pour laisser lire un résultat. */
    public static void pause() {
        lireTexte("Appuyez sur Entrée pour continuer");
    }
}
