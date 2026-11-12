package service;

import modele.Facture;

/** (BONUS H2) Export d'une facture en fichier texte  -  Responsable : Moustapha. */
public class ExportService {

    /**
     * Écrit le texte de la facture (FactureService.formater) dans "<dossier>/facture_<numéro>.txt".
     * Utiliser java.io.PrintWriter ou java.nio.file.Files. Renvoie le chemin du fichier créé.
     * Une erreur d'écriture doit devenir une ErreurMetier (pas de plantage).
     */
    public String exporterFacture(Facture facture, String texteFacture, String dossier) {
        throw ATraiter.pour("H2", "exporter une facture");
    }
}
