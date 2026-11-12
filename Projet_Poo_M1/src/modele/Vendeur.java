package modele;

/** Profil Vendeur : commandes et facturation. */
public class Vendeur extends Utilisateur {
    public Vendeur(String login, String nomAffiche) {
        super(login, nomAffiche);
    }

    @Override
    public String getRole() { return "VENDEUR"; }
}
