package ui;

import java.util.ArrayList;

import modele.Commande;
import modele.LigneCommande;
import modele.StatutCommande;
import service.CommandeService;

/** Sous-menu Commandes  -  Responsable : Aminata (E1 à E5) ; consultation E8 : Moustapha. */
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

    private void creer() {
        String codeClient = Saisie.lireTexteNonVide("Code du client");
        Commande c = commandes.creerCommande(codeClient);
        System.out.println("Commande " + c.getNumero() + " créée pour " + c.getClient().getNom() + " (EN_COURS).");
    }

    private void ajouterLigne() {
        String numero = Saisie.lireTexteNonVide("Numéro de commande");
        String reference = Saisie.lireTexteNonVide("Référence du produit");
        int quantite = Saisie.lireEntierPositif("Quantité");
        commandes.ajouterLigne(numero, reference, quantite);
        System.out.println("Ligne ajoutée. Total HT : " + Affichage.montant(commandes.obtenir(numero).calculerTotalHT()));
    }

    private void modifierQuantite() {
        String numero = Saisie.lireTexteNonVide("Numéro de commande");
        String reference = Saisie.lireTexteNonVide("Référence du produit");
        int quantite = Saisie.lireEntierPositif("Nouvelle quantité");
        commandes.modifierQuantite(numero, reference, quantite);
        System.out.println("Quantité modifiée.");
    }

    private void retirerLigne() {
        String numero = Saisie.lireTexteNonVide("Numéro de commande");
        String reference = Saisie.lireTexteNonVide("Référence du produit à retirer");
        commandes.retirerLigne(numero, reference);
        System.out.println("Ligne retirée.");
    }

    private void valider() {
        String numero = Saisie.lireTexteNonVide("Numéro de commande à valider");
        commandes.valider(numero);
        System.out.println("Commande " + numero + " validée : le stock a été mis à jour.");
    }

    private void annuler() {
        String numero = Saisie.lireTexteNonVide("Numéro de commande à annuler");
        if (!Saisie.lireOuiNon("Confirmer l'annulation de " + numero)) {
            System.out.println("Annulation abandonnée.");
            return;
        }
        commandes.annuler(numero);
        System.out.println("Commande " + numero + " annulée.");
    }

    /** E8 - Consultation (Moustapha) : toutes, par statut, ou le détail d'une commande. */
    private void consulter() {
        System.out.println("1. Toutes les commandes   2. Par statut   3. Détail d'une commande");
        int choix = Saisie.lireChoix("Votre choix", 1, 3);
        if (choix == 1) {
            afficherListe(commandes.listerToutes());
        } else if (choix == 2) {
            int s = Saisie.lireChoix("Statut : 1 = EN_COURS, 2 = VALIDEE, 3 = ANNULEE", 1, 3);
            StatutCommande statut = (s == 1) ? StatutCommande.EN_COURS : (s == 2) ? StatutCommande.VALIDEE : StatutCommande.ANNULEE;
            afficherListe(commandes.listerParStatut(statut));
        } else {
            afficherDetail(commandes.obtenir(Saisie.lireTexteNonVide("Numéro de commande")));
        }
    }

    private void afficherListe(ArrayList<Commande> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucune commande.");
            return;
        }
        System.out.println(String.format("%-8s %-12s %-9s %-24s %7s %22s", "Numéro", "Date", "Statut", "Client", "Lignes", "Total HT"));
        for (Commande c : liste) {
            System.out.println(String.format("%-8s %-12s %-9s %-24s %7d %22s", c.getNumero(), c.getDate(), c.getStatut(),
                    c.getClient().getNom(), c.getLignes().size(), Affichage.montant(c.calculerTotalHT())));
        }
        System.out.println(liste.size() + " commande(s).");
    }

    private void afficherDetail(Commande c) {
        System.out.println("Commande " + c.getNumero() + " du " + c.getDate() + " - " + c.getStatut());
        System.out.println("Client : " + c.getClient().getNom() + " (" + c.getClient().getCode() + ", " + c.getClient().getType() + ")");
        if (c.getLignes().isEmpty()) {
            System.out.println("Aucune ligne.");
        }
        for (LigneCommande l : c.getLignes()) {
            System.out.println(String.format("  %-6s %-28s %5d x %18s = %18s", l.getProduit().getReference(),
                    l.getProduit().getDesignation(), l.getQuantite(), Affichage.montant(l.getPrixUnitaireHT()),
                    Affichage.montant(l.getSousTotal())));
        }
        System.out.println("Total HT : " + Affichage.montant(c.calculerTotalHT()));
    }
}
