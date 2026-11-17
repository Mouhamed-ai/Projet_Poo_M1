package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import modele.ErreurMetier;
import modele.Facture;

/** (BONUS H2) Export d'une facture en fichier texte  -  Responsable : Moustapha. */
public class ExportService {

    /**
     * Écrit le texte de la facture (FactureService.formater) dans "<dossier>/facture_<numéro>.txt".
     * Renvoie le chemin du fichier créé. Une erreur d'écriture devient une ErreurMetier (pas de plantage).
     */
    public String exporterFacture(Facture facture, String texteFacture, String dossier) {
        try {
            Path rep = Paths.get(dossier);
            Files.createDirectories(rep);                                       // crée le dossier au besoin
            Path fichier = rep.resolve("facture_" + facture.getNumeroCommande() + ".txt");
            Files.write(fichier, texteFacture.getBytes(StandardCharsets.UTF_8));
            return fichier.toAbsolutePath().toString();
        } catch (IOException | RuntimeException e) {
            throw new ErreurMetier("Export impossible : " + e.getMessage());
        }
    }
}
