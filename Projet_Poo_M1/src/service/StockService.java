package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import modele.Produit;

/**
 * MODULE PRODUITS ET STOCK  -  Responsable : Pape Mamadou (tâches C1 à C7).
 *
 * Collections de ce service (à justifier dans le README) :
 *  - HashMap<String, Produit> catalogue : accès direct par référence, clé unique (règle 1)
 *  - TreeSet<String> categories         : pas de doublon + ordre alphabétique automatique
 *  - ArrayList<Produit> (retours)       : copies triées/filtrées, le catalogue reste intact
 */
public class StockService {

    private final HashMap<String, Produit> catalogue = new HashMap<>();   // référence -> produit
    private final TreeSet<String> categories = new TreeSet<>();           // catégories distinctes

    // Dépendances "optionnelles" branchées par Main (injection par setter, voir Main).
    // Pourquoi un setter ? StockService a besoin de CommandeService (règle 6) ET
    // CommandeService a besoin de StockService : impossible de se passer les deux
    // dans les constructeurs (qui serait créé en premier ?).
    private CommandeService commandeService;
    private JournalStockService journal;      // BONUS H1 : peut rester null

    public void setCommandeService(CommandeService commandeService) { this.commandeService = commandeService; }
    public void setJournal(JournalStockService journal) { this.journal = journal; }

    // ================= C1 à C4 : gestion des produits =================

    /** C1 - Règle 1 : refuser si la référence existe (containsKey). Mettre à jour le TreeSet des catégories. */
    public void ajouterProduit(Produit produit) {
        throw ATraiter.pour("C1", "StockService.ajouterProduit");
    }

    /** C2 - Modifier désignation, catégorie, prix, seuil. La RÉFÉRENCE n'est pas modifiable. Penser aux catégories. */
    public void modifierProduit(String reference, String designation, String categorie,
                                BigDecimal prix, int seuil) {
        throw ATraiter.pour("C2", "StockService.modifierProduit");
    }

    /**
     * C3 - Règle 6 : refuser UNIQUEMENT si le produit figure dans une commande VALIDEE :
     * commandeService.produitDansCommandeValidee(reference). Décision du groupe : s'il figure dans des
     * commandes EN_COURS, la suppression est permise : commandeService.retirerProduitDesCommandesEnCours(reference)
     * retire ses lignes (Iterator) et renvoie les numéros touchés, à afficher à l'utilisateur.
     * Après suppression : recalculer le TreeSet des catégories (une catégorie peut disparaître).
     */
    public void supprimerProduit(String reference) {
        throw ATraiter.pour("C3", "StockService.supprimerProduit");
    }

    /** C4 - Entrée en stock (quantité > 0). Si journal != null : journal.enregistrer(... ENTREE ...). */
    public void reapprovisionner(String reference, int quantite) {
        throw ATraiter.pour("C4", "StockService.reapprovisionner");
    }

    // ================= Accès utilisés par les autres modules =================

    /** Renvoie le produit ou lance ErreurMetier("Produit introuvable : ...") si la référence est inconnue. */
    public Produit obtenir(String reference) {
        throw ATraiter.pour("C1", "StockService.obtenir");
    }

    /** Tous les produits (copie). Utilisé par le tableau de bord et la sauvegarde. */
    public ArrayList<Produit> tousLesProduits() {
        throw ATraiter.pour("C5", "StockService.tousLesProduits");
    }

    /** Catégories triées (copie du TreeSet). */
    public TreeSet<String> getCategories() {
        throw ATraiter.pour("C1", "StockService.getCategories");
    }

    /**
     * Appelé par CommandeService.valider : sortie de stock d'un produit
     * (utiliser Produit.retirerStock ; journaliser SORTIE si journal != null).
     */
    public void sortirDuStock(String reference, int quantite, String motif) {
        throw ATraiter.pour("C4", "StockService.sortirDuStock");
    }

    /** Appelé par CommandeService.annuler (commande validée) : restitution du stock (+ journal ENTREE). */
    public void restituerAuStock(String reference, int quantite, String motif) {
        throw ATraiter.pour("C4", "StockService.restituerAuStock");
    }

    // ================= C5 à C7 : listes, recherche, alertes =================

    /** C5 - Copie du catalogue (new ArrayList<>(catalogue.values())) triée avec Collections.sort + Comparator. */
    public ArrayList<Produit> listerParDesignation() { throw ATraiter.pour("C5", "tri par désignation"); }

    /** C5 - Tri par prix unitaire HT croissant. */
    public ArrayList<Produit> listerParPrix() { throw ATraiter.pour("C5", "tri par prix"); }

    /** C5 - Tri par quantité en stock croissante. */
    public ArrayList<Produit> listerParQuantite() { throw ATraiter.pour("C5", "tri par quantité"); }

    /** C6 - Recherche par référence exacte, par fragment de désignation, ou par catégorie. Liste vide si rien. */
    public ArrayList<Produit> rechercherParReference(String reference) { throw ATraiter.pour("C6", "recherche référence"); }
    public ArrayList<Produit> rechercherParDesignation(String fragment) { throw ATraiter.pour("C6", "recherche désignation"); }
    public ArrayList<Produit> rechercherParCategorie(String categorie) { throw ATraiter.pour("C6", "recherche catégorie"); }

    /** C7 - Produits dont quantité <= seuil (utiliser Produit.estEnAlerte). */
    public ArrayList<Produit> produitsEnAlerte() { throw ATraiter.pour("C7", "produits en alerte"); }
}
