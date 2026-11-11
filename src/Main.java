import donnees.DonneesDemo;
import modele.ErreurMetier;
import modele.Gestionnaire;
import modele.Utilisateur;
import service.AuthService;
import service.ClientService;
import service.CommandeService;
import service.ExportService;
import service.FactureService;
import service.JournalStockService;
import service.PersistanceService;
import service.StockService;
import service.TableauDeBordService;
import ui.Affichage;
import ui.Menu;
import ui.MenuGestionnaire;
import ui.MenuVendeur;
import ui.Saisie;

/**
 * Point d'entrée de l'application. Trois étapes :
 *  1) créer les services,  2) les "câbler" entre eux,  3) charger la démo puis boucler sur la connexion.
 */
public class Main {

    private static final int MAX_ESSAIS = 3;   // essais de connexion autorisés

    public static void main(String[] args) {   // String[] args : seul tableau toléré (imposé par Java)

        // ---------- 1) Création des services (les indépendants d'abord) ----------
        StockService stock = new StockService();
        ClientService clients = new ClientService();
        CommandeService commandes = new CommandeService(stock, clients);   // dépend de stock et clients
        FactureService factures = new FactureService();
        JournalStockService journal = new JournalStockService();
        TableauDeBordService tableau = new TableauDeBordService(commandes, factures);
        ExportService export = new ExportService();
        PersistanceService persistance = new PersistanceService(stock, clients, commandes);
        AuthService auth = new AuthService();

        // ---------- 2) Câblage (dépendances circulaires résolues par setters) ----------
        stock.setCommandeService(commandes);   // règle 6 : le stock demande aux commandes si un produit est utilisé
        stock.setJournal(journal);             // bonus H1
        clients.setCommandeService(commandes); // règle 6 côté clients

        // ---------- 3) Jeu de démonstration ----------
        try {
            DonneesDemo.charger(stock, clients, commandes);
            System.out.println("Données de démonstration chargées.");
        } catch (UnsupportedOperationException e) {          // normal tant que le squelette n'est pas codé
            System.out.println("[Squelette] Démo non chargée, fonction à implémenter : " + e.getMessage());
        } catch (ErreurMetier e) {                           // une donnée de démo viole une règle : à corriger
            System.out.println("[Démo] Donnée refusée : " + e.getMessage());
        }

        // ---------- 4) Boucle de connexion ----------
        System.out.println("Comptes : gestionnaire / gest123   |   vendeur / vend123");
        boolean quitter = false;
        while (!quitter) {
            Affichage.titre("GESTION DE STOCK ET FACTURATION - CONNEXION");
            Utilisateur utilisateur = seConnecter(auth);
            if (utilisateur == null) {
                quitter = true;                              // l'utilisateur a quitté ou trop d'échecs
            } else {
                System.out.println("Bienvenue " + utilisateur.getNomAffiche() + " (" + utilisateur.getRole() + ")");
                Menu menu = (utilisateur instanceof Gestionnaire)
                        ? new MenuGestionnaire(stock, clients, journal, tableau, persistance)
                        : new MenuVendeur(commandes, factures, export);
                menu.lancer();                               // POLYMORPHISME : lancer() du bon menu
            }
        }
        System.out.println("Au revoir.");
    }

    /** Demande login/mot de passe (3 essais). Renvoie null si l'utilisateur tape 0 ou échoue 3 fois. */
    private static Utilisateur seConnecter(AuthService auth) {
        for (int essai = 1; essai <= MAX_ESSAIS; essai++) {
            String login = Saisie.lireTexte("Identifiant (0 pour quitter)");
            if (login.equals("0")) {
                return null;
            }
            String motDePasse = Saisie.lireTexte("Mot de passe");
            Utilisateur u = auth.authentifier(login, motDePasse);
            if (u != null) {
                return u;                                    // connexion réussie
            }
            System.out.println("[REFUSE] Identifiant ou mot de passe incorrect (essai " + essai + "/" + MAX_ESSAIS + ").");
        }
        System.out.println("Trop d'échecs de connexion.");
        return null;
    }
}
