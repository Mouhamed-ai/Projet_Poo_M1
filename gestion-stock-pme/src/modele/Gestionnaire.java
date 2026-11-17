package modele;

/** Profil Gestionnaire : produits, stock, clients, indicateurs. */
public class Gestionnaire extends Utilisateur {
    public Gestionnaire(String login, String nomAffiche) {
        super(login, nomAffiche);   // on transmet à la classe mère
    }

    @Override
    public String getRole() { return "GESTIONNAIRE"; }
}
