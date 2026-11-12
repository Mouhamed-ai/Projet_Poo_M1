package ui;

import modele.ErreurMetier;

/**
 * Classe de base de tous les menus.
 * Son rôle clé : executer(...) = FILET DE SÉCURITÉ (sujet 4.2).
 * Quand une règle est violée, le service lance ErreurMetier ; ici on l'attrape, on affiche
 * un message clair et on RETOURNE AU MENU sans que le programme s'arrête.
 */
public abstract class Menu {

    /** Affiche le menu en boucle jusqu'au choix « Retour / Déconnexion ». */
    public abstract void lancer();

    /** Exécute une action en protégeant le programme contre les erreurs. */
    protected void executer(Runnable action) {
        try {
            action.run();                                              // on tente l'action
        } catch (ErreurMetier e) {                                     // règle de gestion non respectée
            System.out.println("[REFUSE] " + e.getMessage());
        } catch (UnsupportedOperationException e) {                    // fonction du squelette pas encore codée
            System.out.println("[A FAIRE] Fonctionnalité pas encore implémentée : " + e.getMessage());
        } catch (RuntimeException e) {                                 // bug inattendu : on ne plante pas, on le signale
            System.out.println("[ERREUR INATTENDUE] " + e.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }
}
