package service;

/**
 * Outil du SQUELETTE : fabrique l'exception "fonction pas encore écrite".
 * Chaque méthode non implémentée fait :  throw ATraiter.pour("C1", "ajouter un produit");
 * -> le programme COMPILE et SE LANCE dès le premier jour ; le menu affiche
 *    "[A FAIRE] ..." au lieu de planter. Quand vous codez une méthode, vous
 *    remplacez simplement ce throw par le vrai code.
 */
public final class ATraiter {
    private ATraiter() { }

    public static UnsupportedOperationException pour(String idTache, String description) {
        return new UnsupportedOperationException("[" + idTache + "] " + description);
    }
}
