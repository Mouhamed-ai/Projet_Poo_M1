package ui;

import modele.Commande;
import modele.Facture;
import service.CommandeService;
import service.ExportService;
import service.FactureService;

/** Sous-menu Facturation  -  Responsable : Moustapha (E7, bonus H2). */
public class MenuFacturation extends Menu {

    private final CommandeService commandes;
    private final FactureService factures;
    private final ExportService export;       // bonus H2

    public MenuFacturation(CommandeService commandes, FactureService factures, ExportService export) {
        this.commandes = commandes;
        this.factures = factures;
        this.export = export;
    }

    @Override
    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            Affichage.titre("FACTURATION");
            System.out.println("1. Éditer la facture d'une commande validée");
            System.out.println("2. Exporter une facture en fichier texte (bonus)");
            System.out.println("0. Retour");
            switch (Saisie.lireChoix("Votre choix", 0, 2)) {
                case 1: executer(this::editer); break;
                case 2: executer(this::exporter); break;
                default: continuer = false; break;
            }
        }
    }

    private void editer() {
        String numero = Saisie.lireTexteNonVide("Numéro de la commande");
        Commande commande = commandes.obtenir(numero);
        factures.afficher(factures.calculer(commande));                        // refuse si la commande n'est pas VALIDEE
        Saisie.pause();
    }

    private void exporter() {
        String numero = Saisie.lireTexteNonVide("Numéro de la commande");
        Facture facture = factures.calculer(commandes.obtenir(numero));
        String dossier = Saisie.lireTexteNonVide("Dossier d'export (ex. factures)");
        String chemin = export.exporterFacture(facture, factures.formater(facture), dossier);
        System.out.println("Facture exportée : " + chemin);
    }
}
