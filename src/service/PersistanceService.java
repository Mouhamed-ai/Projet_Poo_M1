package service;

/**
 * (BONUS H4) Sauvegarde / rechargement CSV  -  Responsable : Sokhna.
 * ATTENTION au piège du sujet : String.split() renvoie un TABLEAU (interdit).
 * Alternatives sans tableau : java.util.Scanner avec useDelimiter(";"), ou StringTokenizer.
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

    /** Écrit produits.csv, clients.csv, commandes.csv (+ lignes) dans le dossier. */
    public void sauvegarder(String dossier) { throw ATraiter.pour("H4", "sauvegarder"); }

    /** Relit les fichiers CSV et reconstruit les collections. Fichier absent => ErreurMetier claire. */
    public void charger(String dossier) { throw ATraiter.pour("H4", "charger"); }
}
