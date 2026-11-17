package modele;

/**
 * Exception lancée quand une RÈGLE DE GESTION n'est pas respectée
 * (doublon, stock insuffisant, élément introuvable, champ invalide...).
 *
 * Principe de tout le projet :
 *   - les SERVICES lancent  throw new ErreurMetier("message clair");
 *   - les MENUS l'attrapent (voir ui.Menu.executer) : le message s'affiche et
 *     l'application revient au menu SANS s'arrêter (exigence du sujet, section 4.2).
 *
 * On hérite de RuntimeException pour ne pas devoir écrire "throws" partout.
 */
public class ErreurMetier extends RuntimeException {
    private static final long serialVersionUID = 1L; // identifiant de version (exigé par Java pour les exceptions)

    /** @param message texte lisible affiché à l'utilisateur */
    public ErreurMetier(String message) {
        super(message); // on transmet le message à la classe mère
    }
}
