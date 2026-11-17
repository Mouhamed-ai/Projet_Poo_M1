package service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import modele.Client;
import modele.Commande;
import modele.ErreurMetier;
import modele.LigneCommande;
import modele.Produit;
import modele.StatutCommande;
import modele.TypeClient;

/**
 * (BONUS H4) Sauvegarde / rechargement CSV  -  Responsable : Sokhna.
 * Piège du sujet : String.split() renvoie un TABLEAU (interdit). On lit avec StringTokenizer (séparateur ";").
 * Les champs texte ne contiennent jamais ";" (remplacé par "," à l'écriture) et ne sont jamais vides.
 */
public class PersistanceService {

    private final StockService stockService;
    private final ClientService clientService;
    private final CommandeService commandeService;

    public PersistanceService(StockService stockService, ClientService clientService, CommandeService commandeService) {
        this.stockService = stockService;
        this.clientService = clientService;
        this.commandeService = commandeService;
    }

    /** Écrit produits.csv, clients.csv, commandes.csv et lignes.csv dans le dossier. */
    public void sauvegarder(String dossier) {
        try {
            Path rep = Paths.get(dossier);
            Files.createDirectories(rep);
            ArrayList<String> produits = new ArrayList<>();
            for (Produit p : stockService.listerParDesignation()) {
                produits.add(texte(p.getReference()) + ";" + texte(p.getDesignation()) + ";" + texte(p.getCategorie()) + ";"
                        + p.getPrixUnitaireHT().toPlainString() + ";" + p.getQuantiteStock() + ";" + p.getSeuilAlerte());
            }
            ArrayList<String> clients = new ArrayList<>();
            for (Client c : clientService.lister()) {
                clients.add(texte(c.getCode()) + ";" + texte(c.getNom()) + ";" + texte(c.getTelephone()) + ";"
                        + texte(c.getEmail()) + ";" + c.getType());
            }
            ArrayList<String> commandes = new ArrayList<>();
            ArrayList<String> lignes = new ArrayList<>();
            for (Commande c : commandeService.listerToutes()) {
                commandes.add(c.getNumero() + ";" + c.getClient().getCode() + ";" + c.getDate() + ";" + c.getStatut());
                for (LigneCommande l : c.getLignes()) {
                    lignes.add(c.getNumero() + ";" + l.getProduit().getReference() + ";" + l.getQuantite() + ";"
                            + l.getPrixUnitaireHT().toPlainString());
                }
            }
            ecrire(rep.resolve("produits.csv"), produits);
            ecrire(rep.resolve("clients.csv"), clients);
            ecrire(rep.resolve("commandes.csv"), commandes);
            ecrire(rep.resolve("lignes.csv"), lignes);
        } catch (IOException e) {
            throw new ErreurMetier("Sauvegarde impossible : " + e.getMessage());
        }
    }

    /** Relit les fichiers CSV et reconstruit les collections. Fichier absent => ErreurMetier claire. */
    public void charger(String dossier) {
        Path rep = Paths.get(dossier);
        List<String> produits = lire(rep.resolve("produits.csv"));       // on lit tout AVANT de vider
        List<String> clients = lire(rep.resolve("clients.csv"));
        List<String> commandes = lire(rep.resolve("commandes.csv"));
        List<String> lignes = lire(rep.resolve("lignes.csv"));
        try {
            commandeService.vider();
            stockService.vider();
            clientService.vider();
            for (String ligne : produits) {
                StringTokenizer t = new StringTokenizer(ligne, ";");
                stockService.ajouterProduit(new Produit(t.nextToken(), t.nextToken(), t.nextToken(),
                        new BigDecimal(t.nextToken()), Integer.parseInt(t.nextToken()), Integer.parseInt(t.nextToken())));
            }
            for (String ligne : clients) {
                StringTokenizer t = new StringTokenizer(ligne, ";");
                clientService.ajouterClient(new Client(t.nextToken(), t.nextToken(), t.nextToken(), t.nextToken(),
                        TypeClient.valueOf(t.nextToken())));
            }
            for (String ligne : commandes) {
                StringTokenizer t = new StringTokenizer(ligne, ";");
                String numero = t.nextToken();
                Client client = clientService.obtenir(t.nextToken());
                Commande c = new Commande(numero, client, LocalDate.parse(t.nextToken()));
                c.setStatut(StatutCommande.valueOf(t.nextToken()));
                for (String l : lignes) {                                  // lignes de CETTE commande
                    StringTokenizer u = new StringTokenizer(l, ";");
                    if (u.nextToken().equals(numero)) {
                        Produit p = stockService.obtenir(u.nextToken());
                        c.getLignes().add(new LigneCommande(p, Integer.parseInt(u.nextToken()), new BigDecimal(u.nextToken())));
                    }
                }
                commandeService.restaurer(c);
            }
        } catch (RuntimeException e) {
            if (e instanceof ErreurMetier) {
                throw e;
            }
            throw new ErreurMetier("Fichiers de sauvegarde illisibles : " + e.getClass().getSimpleName()
                    + " (" + e.getMessage() + ")");
        }
    }

    // ---------- Outils privés ----------

    private String texte(String valeur) {
        return valeur.replace(';', ',');                                    // ";" est notre séparateur
    }

    private void ecrire(Path fichier, List<String> lignes) throws IOException {
        Files.write(fichier, lignes, StandardCharsets.UTF_8);
    }

    private List<String> lire(Path fichier) {
        if (!Files.exists(fichier)) {
            throw new ErreurMetier("Fichier de sauvegarde introuvable : " + fichier);
        }
        try {
            return Files.readAllLines(fichier, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ErreurMetier("Lecture impossible de " + fichier + " : " + e.getMessage());
        }
    }
}
