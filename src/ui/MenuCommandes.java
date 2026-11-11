package ui;

import service.ATraiter;
import service.CommandeService;

/** Sous-menu Commandes  -  Responsable : Aminata (E1 à E5 ; consultation E8 avec Moustapha). */
public class MenuCommandes extends Menu {

    private final CommandeService commandes;

    public MenuCommandes(CommandeService commandes) {
        this.commandes = commandes;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("COMMANDES");
            System.out.println("1. Créer une commande");
            System.out.println("2. Ajouter une ligne (produit, quantité)");
            System.out.println("3. Modifier la quantité d'une ligne");
            System.out.println("4. Retirer une ligne");
            System.out.println("5. Valider une commande");
            System.out.println("6. Annuler une commande");
            System.out.println("7. Lister / consulter les commandes");
            System.out.println("0. Retour");
            switch (Saisie.lireChoix("Votre choix", 0, 7)) {
                case 1: executer(this::creer); break;
                case 2: executer(this::ajouterLigne); break;
                case 3: executer(this::modifierQuantite); break;
                case 4: executer(this::retirerLigne); break;
                case 5: executer(this::valider); break;
                case 6: executer(this::annuler); break;
                case 7: executer(this::consulter); break;
                default: continuer = false; break;
            }
        }
    }

    private void creer() { throw ATraiter.pour("E1", "commandes.creerCommande(codeClient)"); }
    private void ajouterLigne() { throw ATraiter.pour("E2", "commandes.ajouterLigne(...)"); }
    private void modifierQuantite() { throw ATraiter.pour("E3", "commandes.modifierQuantite(...)"); }
    private void retirerLigne() { throw ATraiter.pour("E3", "commandes.retirerLigne(...)"); }
    private void valider() { throw ATraiter.pour("E4", "commandes.valider(...)"); }
    private void annuler() { throw ATraiter.pour("E5", "commandes.annuler(...)"); }
    private void consulter() { throw ATraiter.pour("E8", "lister toutes / par statut / une commande par numéro"); }
}
