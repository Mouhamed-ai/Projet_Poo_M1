package donnees;

import java.math.BigDecimal;
import java.time.LocalDate;

import modele.Client;
import modele.Commande;
import modele.Produit;
import modele.TypeClient;
import service.ClientService;
import service.CommandeService;
import service.StockService;

/**
 * G1 - Jeu de démonstration chargé au démarrage  -  Responsable : Sokhna.
 * 10 produits, 5 clients, 3 commandes (valeurs du plan : sections 9.1 à 9.4).
 * On passe PAR LES SERVICES (pas par les collections directement) : ainsi les règles de gestion
 * sont appliquées aussi aux données de démo. Ce code fonctionnera dès que C1, D1, E1, E2 et E4 seront codés.
 */
public final class DonneesDemo {

    private DonneesDemo() { }

    public static void charger(StockService stock, ClientService clients, CommandeService commandes) {
        // ----- 10 produits (dont P008 sous le seuil et P010 EGAL au seuil : alertes) -----
        stock.ajouterProduit(new Produit("P001", "Ramette papier A4", "Fournitures", new BigDecimal("3500"), 200, 30));
        stock.ajouterProduit(new Produit("P002", "Boîte de 50 stylos", "Fournitures", new BigDecimal("4000"), 80, 15));
        stock.ajouterProduit(new Produit("P003", "Classeur à levier", "Fournitures", new BigDecimal("1800"), 120, 20));
        stock.ajouterProduit(new Produit("P004", "Agrafeuse", "Fournitures", new BigDecimal("2500"), 40, 10));
        stock.ajouterProduit(new Produit("P005", "Ordinateur portable 15 pouces", "Informatique", new BigDecimal("450000"), 12, 3));
        stock.ajouterProduit(new Produit("P006", "Imprimante laser", "Informatique", new BigDecimal("185000"), 8, 3));
        stock.ajouterProduit(new Produit("P007", "Souris sans fil", "Accessoires", new BigDecimal("7500"), 60, 10));
        stock.ajouterProduit(new Produit("P008", "Clavier", "Accessoires", new BigDecimal("9000"), 5, 8));
        stock.ajouterProduit(new Produit("P009", "Clé USB 64 Go", "Accessoires", new BigDecimal("6500"), 100, 20));
        stock.ajouterProduit(new Produit("P010", "Écran 24 pouces", "Informatique", new BigDecimal("120000"), 4, 4));

        // ----- 5 clients (téléphones et emails fictifs) -----
        clients.ajouterClient(new Client("C001", "Amadou Diallo", "770000001", "amadou.diallo@exemple.sn", TypeClient.PARTICULIER));
        clients.ajouterClient(new Client("C002", "Fatou Ndiaye", "770000002", "fatou.ndiaye@exemple.sn", TypeClient.PARTICULIER));
        clients.ajouterClient(new Client("C003", "Sénégal Tech SARL", "330000003", "contact@senegaltech.exemple.sn", TypeClient.ENTREPRISE));
        clients.ajouterClient(new Client("C004", "Teranga Services", "330000004", "contact@teranga.exemple.sn", TypeClient.ENTREPRISE));
        clients.ajouterClient(new Client("C005", "Mariama Sow", "770000005", "mariama.sow@exemple.sn", TypeClient.PARTICULIER));

        // ----- 3 commandes : dates différentes pour que le CA par mois (bonus) ait du sens -----
        Commande c1 = commandes.creerCommande("C003", LocalDate.of(2026, 10, 12));   // CMD001
        commandes.ajouterLigne(c1.getNumero(), "P005", 2);                            // 2 x 450 000
        commandes.ajouterLigne(c1.getNumero(), "P007", 5);                            // 5 x 7 500 => HT 937 500 (remise 5 %)
        commandes.valider(c1.getNumero());

        Commande c2 = commandes.creerCommande("C001", LocalDate.of(2026, 11, 3));    // CMD002
        commandes.ajouterLigne(c2.getNumero(), "P001", 10);                           // 10 x 3 500
        commandes.ajouterLigne(c2.getNumero(), "P002", 4);                            // 4 x 4 000 => HT 51 000
        commandes.valider(c2.getNumero());

        Commande c3 = commandes.creerCommande("C004", LocalDate.of(2026, 11, 8));    // CMD003 (reste EN_COURS)
        commandes.ajouterLigne(c3.getNumero(), "P006", 1);                            // 185 000
        commandes.ajouterLigne(c3.getNumero(), "P010", 1);                            // 120 000 => HT 305 000
    }
}
