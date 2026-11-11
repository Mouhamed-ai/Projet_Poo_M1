package modele;

/**
 * HÉRITAGE + POLYMORPHISME : classe abstraite commune aux deux profils.
 * "abstract" = on ne peut pas faire new Utilisateur(...) ; on crée un Gestionnaire ou un Vendeur.
 * Chaque sous-classe redéfinit getRole() : le code appelant n'a pas besoin de savoir
 * lequel des deux il manipule (c'est le polymorphisme).
 */
public abstract class Utilisateur {

    private final String login;       // identifiant de connexion
    private final String nomAffiche;  // nom montré dans les menus

    protected Utilisateur(String login, String nomAffiche) {
        this.login = Controles.texteNonVide(login, "Le login");
        this.nomAffiche = Controles.texteNonVide(nomAffiche, "Le nom affiché");
    }

    public String getLogin() { return login; }
    public String getNomAffiche() { return nomAffiche; }

    /** Chaque profil dit quel est son rôle. */
    public abstract String getRole();
}
