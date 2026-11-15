package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import modele.ErreurMetier;
import modele.Facture;

/** (BONUS H2) Export d'une facture en fichier texte  -  Responsable : Moustapha. */
public class ExportService {

    /**
     * Écrit le texte de la facture (FactureService.formater) dans "<dossier>/facture_<numéro>.txt" (UTF-8).
     * Crée le dossier s'il n'existe pas. Renvoie le chemin du fichier créé.
     * Une erreur d'écriture devient une ErreurMetier (pas de plantage).
     */
    public String exporterFacture(Facture facture, String texteFacture, String dossier) {
        File rep = new File(dossier);
        if (!rep.exists() && !rep.mkdirs()) {
            throw new ErreurMetier("Impossible de créer le dossier : " + dossier);
        }
        File fichier = new File(rep, "facture_" + facture.getNumeroCommande() + ".txt");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fichier), "UTF-8"))) {
            pw.print(texteFacture);
        } catch (IOException e) {
            throw new ErreurMetier("Export impossible : " + e.getMessage());
        }
        return fichier.getPath();
    }
}
