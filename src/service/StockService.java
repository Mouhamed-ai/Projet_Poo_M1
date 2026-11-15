package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;
import modele.ErreurMetier;
import modele.Produit;
import modele.TypeMouvement;

/**
 * MODULE PRODUITS ET STOCK  -  Responsable : Mouhamed (tâches C1 à C7).
 *
 * Collections de ce service (à justifier dans le README) :
 *  - HashMap<String, Produit> catalogue : accès direct par référence, clé unique (règle 1)
 *  - TreeSet<String> categories         : pas de doublon + ordre alphabétique automatique
 *  - ArrayList<Produit> (retours)       : copies triées/filtrées, le catalogue reste intact
 */
public class StockService {

    private final HashMap<String, Produit> catalogue = new HashMap<>();   // référence -> produit
    private final TreeSet<String> categories = new TreeSet<>();           // catégories distinctes

    // Dépendances branchées par Main (injection par setter).
    private CommandeService commandeService;
    private JournalStockService journal;      // BONUS H1 : peut rester null

    public void setCommandeService(CommandeService commandeService) { this.commandeService = commandeService; }
    public void setJournal(JournalStockService journal) { this.journal = journal; }

    // ================= Méthodes privées utilitaires =================

    /** Reconstruit la liste des catégories à partir de tous les produits du catalogue. */
    private void recalculerCategories() {
        categories.clear();
        for (Produit p : catalogue.values()) {
            categories.add(p.getCategorie());
        }
    }

    /** Refuse une quantité nulle ou négative. */
    private void verifierQuantite(int quantite) {
        if (quantite <= 0) {
            throw new ErreurMetier("La quantité doit être strictement positive.");
        }
    }

    /** Refuse une entrée qui ferait déborder la quantité stockée dans un int. */
    private void verifierCapaciteStock(Produit produit, int quantite) {
        if (quantite > Integer.MAX_VALUE - produit.getQuantiteStock()) {
            throw new ErreurMetier("La quantité totale en stock dépasse la valeur maximale autorisée.");
        }
    }

    // ================= C1 à C4 : gestion des produits =================

    /** C1 - Règle 1 : refuse une référence déjà utilisée, range le produit, met à jour les catégories. */
    public void ajouterProduit(Produit produit) {
        if (produit == null) {
            throw new ErreurMetier("Le produit à ajouter est absent.");
        } 
        if (catalogue.containsKey(produit.getReference())) {
            throw new ErreurMetier("La référence " + produit.getReference() + " existe déjà.");
        }
        catalogue.put(produit.getReference(), produit);
        categories.add(produit.getCategorie());
    }

    /** C2 - Modifie désignation, catégorie, prix, seuil. La RÉFÉRENCE n'est pas modifiable. */
    public void modifierProduit(String reference, String designation, String categorie,
                                BigDecimal prix, int seuil) {
        Produit p = obtenir(reference);

        // On vérifie tout AVANT de modifier, pour ne jamais modifier le produit à moitié.
        if (designation == null || designation.trim().isEmpty()) {
            throw new ErreurMetier("La désignation ne peut pas être vide.");
        }
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new ErreurMetier("La catégorie ne peut pas être vide.");
        }
        if (prix == null || prix.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErreurMetier("Le prix unitaire HT doit être strictement positif.");
        }
        if (seuil < 0) {
            throw new ErreurMetier("Le seuil d'alerte ne peut pas être négatif.");
        }

        p.setDesignation(designation);
        p.setCategorie(categorie);
        p.setPrixUnitaireHT(prix);
        p.setSeuilAlerte(seuil);
        recalculerCategories();   // l'ancienne catégorie a pu disparaître
    }

    /**
     * C3 - Règle du groupe : refus si le produit figure dans une commande VALIDEE ;
     * ses lignes sont retirées des commandes EN_COURS.
     * Renvoie les numéros des commandes EN_COURS touchées (le menu les affiche).
     */
    public ArrayList<String> supprimerProduit(String reference) {
        obtenir(reference);   // introuvable : erreur
        if (commandeService.produitDansCommandeValidee(reference)) {
            throw new ErreurMetier("Suppression refusée : ce produit figure dans une commande validée.");
        }
        ArrayList<String> touchees = commandeService.retirerProduitDesCommandesEnCours(reference);
        catalogue.remove(reference);
        recalculerCategories();
        return touchees;
    }

    /** C4 - Entrée en stock (quantité > 0). Journalise ENTREE si le journal existe. */
    public void reapprovisionner(String reference, int quantite) {
        verifierQuantite(quantite);
        Produit p = obtenir(reference);
        verifierCapaciteStock(p, quantite);
        p.ajouterStock(quantite);
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.ENTREE, quantite, "Réapprovisionnement");
        }
    }

    // ================= Accès utilisés par les autres modules =================

    /** Renvoie le produit ou lance ErreurMetier si la référence est inconnue. */
    public Produit obtenir(String reference) {
        Produit p = catalogue.get(reference);
        if (p == null) {
            throw new ErreurMetier("Produit introuvable : " + reference);
        }
        return p;
    }

    /** Catégories triées (copie du TreeSet). */
    public TreeSet<String> getCategories() {
        return new TreeSet<>(categories);
    }

    /** Appelée par CommandeService.valider : sortie de stock (règle 2 contrôlée par Produit). */
    public void sortirDuStock(String reference, int quantite, String motif) {
        verifierQuantite(quantite);
        Produit p = obtenir(reference);
        p.retirerStock(quantite);   // lance ErreurMetier si le stock devient négatif
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.SORTIE, quantite, motif);
        }
    }

    /** Appelée par CommandeService.annuler (commande validée) : restitution du stock + journal ENTREE. */
    public void restituerAuStock(String reference, int quantite, String motif) {
        verifierQuantite(quantite);
        Produit p = obtenir(reference);
        verifierCapaciteStock(p, quantite);
        p.ajouterStock(quantite);
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.ENTREE, quantite, motif);
        }
    }

    // ================= C5 à C7 : listes, recherche, alertes =================

    /** Tous les produits (copie). Utilisé par le tableau de bord et la sauvegarde. */
    public ArrayList<Produit> tousLesProduits() {
        return new ArrayList<>(catalogue.values());
    }

    /** C5 - Copie triée par désignation (alphabétique, sans tenir compte des majuscules). */
    public ArrayList<Produit> listerParDesignation() {
        ArrayList<Produit> liste = tousLesProduits();
        Collections.sort(liste, (a, b) -> a.getDesignation().compareToIgnoreCase(b.getDesignation()));
        return liste;
    }

    /** C5 - Tri par prix unitaire HT croissant. */
    public ArrayList<Produit> listerParPrix() {
        ArrayList<Produit> liste = tousLesProduits();
        Collections.sort(liste, (a, b) -> a.getPrixUnitaireHT().compareTo(b.getPrixUnitaireHT()));
        return liste;
    }

    /** C5 - Tri par quantité en stock croissante. */
    public ArrayList<Produit> listerParQuantite() {
        ArrayList<Produit> liste = tousLesProduits();
        Collections.sort(liste, (a, b) -> Integer.compare(a.getQuantiteStock(), b.getQuantiteStock()));
        return liste;
    }

    /** C6 - Recherche par référence exacte : liste de 0 ou 1 produit. */
    public ArrayList<Produit> rechercherParReference(String reference) {
        ArrayList<Produit> resultats = new ArrayList<>();
        Produit p = (reference == null) ? null : catalogue.get(reference);
        if (p != null) {
            resultats.add(p);
        }
        return resultats;
    }

    /** C6 - Recherche par fragment de désignation, insensible à la casse. */
    public ArrayList<Produit> rechercherParDesignation(String fragment) {
        ArrayList<Produit> resultats = new ArrayList<>();
        if (fragment == null) {
            return resultats;
        }
        String recherche = fragment.toLowerCase(Locale.ROOT);
        for (Produit p : catalogue.values()) {
            if (p.getDesignation().toLowerCase(Locale.ROOT).contains(recherche)) {
                resultats.add(p);
            }
        }
        return resultats;
    }

    /** C6 - Recherche par catégorie, insensible à la casse. */
    public ArrayList<Produit> rechercherParCategorie(String categorie) {
        ArrayList<Produit> resultats = new ArrayList<>();
        if (categorie == null) {
            return resultats;
        }
        for (Produit p : catalogue.values()) {
            if (p.getCategorie().equalsIgnoreCase(categorie)) {
                resultats.add(p);
            }
        }
        return resultats;
    }

    /** C7 - Produits dont la quantité est <= au seuil (Produit.estEnAlerte). */
    public ArrayList<Produit> produitsEnAlerte() {
        ArrayList<Produit> resultats = new ArrayList<>();
        for (Produit p : catalogue.values()) {
            if (p.estEnAlerte()) {
                resultats.add(p);
            }
        }
        return resultats;
    }
}
