package ui;

import service.ATraiter;
import service.CommandeService;
import java.util.List;
import modele.Commande;
import modele.ErreurMetier;
import modele.LigneCommande;
import modele.StatutCommande;

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
     /** Consultation : toutes les commandes, filtre par statut, détail d'une commande. */
    private void consulter() {
        System.out.println("1. Toutes les commandes");
        System.out.println("2. Commandes par statut");
        System.out.println("3. Détail d'une commande");
        int choix = Saisie.lireEntierPositif("Votre choix");
        if (choix == 1) {
            afficherListe(commandes.listerToutes(), "Aucune commande enregistrée.");
        } else if (choix == 2) {
            System.out.println("1. EN_COURS   2. VALIDEE   3. ANNULEE");
            int s = Saisie.lireEntierPositif("Statut");
            StatutCommande statut;
            if (s == 1) {
                statut = StatutCommande.EN_COURS;
            } else if (s == 2) {
                statut = StatutCommande.VALIDEE;
            } else if (s == 3) {
                statut = StatutCommande.ANNULEE;
            } else {
                throw new ErreurMetier("Statut invalide : choisir 1, 2 ou 3.");
            }
            afficherListe(commandes.listerParStatut(statut), "Aucune commande au statut " + statut + ".");
        } else if (choix == 3) {
            String numero = Saisie.lireTexteNonVide("Numéro de commande").trim().toUpperCase();
            afficherDetail(commandes.obtenir(numero));
        } else {
            throw new ErreurMetier("Choix invalide : choisir 1, 2 ou 3.");
        }
        Saisie.pause();
    }

    /** Date au format jj/mm/aaaa. */
    private String dateFr(Commande c) {
        return c.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** Une ligne par commande : numéro, date, client, statut, nombre de lignes, total HT. */
    private void afficherListe(List<Commande> liste, String messageVide) {
        if (liste.isEmpty()) {
            System.out.println(messageVide);
            return;
        }
        System.out.println(String.format("%-8s %-12s %-26s %-10s %6s %18s", "N°", "Date", "Client", "Statut", "Lignes", "Total HT"));
        for (Commande c : liste) {
            System.out.println(String.format("%-8s %-12s %-26s %-10s %6d %18s", c.getNumero(), dateFr(c),
                    c.getClient().getNom(), c.getStatut(), c.getLignes().size(), Affichage.montant(c.calculerTotalHT())));
        }
        System.out.println(liste.size() + " commande(s).");
    }

    /** Détail : en-tête, lignes (produit, quantité, prix figé, sous-total) et total HT. */
    private void afficherDetail(Commande c) {
        System.out.println("Commande " + c.getNumero() + " du " + dateFr(c) + " - statut " + c.getStatut());
        System.out.println("Client : " + c.getClient().getCode() + " - " + c.getClient().getNom()
                + " (" + c.getClient().getType() + ")");
        if (c.getLignes().isEmpty()) {
            System.out.println("  (aucune ligne)");
        }
        for (LigneCommande l : c.getLignes()) {
            System.out.println(String.format("  %-6s %-28s %4d x %16s = %16s", l.getProduit().getReference(),
                    l.getProduit().getDesignation(), l.getQuantite(), Affichage.montant(l.getPrixUnitaireHT()),
                    Affichage.montant(l.getPrixUnitaireHT().multiply(java.math.BigDecimal.valueOf(l.getQuantite())))));
        }
        System.out.println("Total HT : " + Affichage.montant(c.calculerTotalHT()));
    }

}
