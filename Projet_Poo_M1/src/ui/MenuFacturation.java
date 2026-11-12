package ui;

import service.ATraiter;
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

    private void editer() { throw ATraiter.pour("E7", "factures.afficher(factures.calculer(commande))"); }
    private void exporter() { throw ATraiter.pour("H2", "export.exporterFacture(...)"); }
}
