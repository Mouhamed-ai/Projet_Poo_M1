package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    // Dépendances "optionnelles" branchées par Main (injection par setter, voir Main).
    // Pourquoi un setter ? StockService a besoin de CommandeService (règle 6) ET
    // CommandeService a besoin de StockService : impossible de se passer les deux
    // dans les constructeurs (qui serait créé en premier ?).
    private CommandeService commandeService;
    private JournalStockService journal;      // BONUS H1 : peut rester null

    public void setCommandeService(CommandeService commandeService) { this.commandeService = commandeService; }
    public void setJournal(JournalStockService journal) { this.journal = journal; }

    // ================= C1 à C4 : gestion des produits =================

    /** C1 - Règle 1 : refuser si la référence existe (containsKey). Met à jour le TreeSet des catégories. */
    public void ajouterProduit(Produit produit) {
        if (produit == null) {
            throw new ErreurMetier("Le produit est obligatoire.");
        }
        if (catalogue.containsKey(produit.getReference())) {                 // règle 1 : référence unique
            throw new ErreurMetier("La référence " + produit.getReference() + " existe déjà.");
        }
        catalogue.put(produit.getReference(), produit);                      // on range dans la HashMap
        categories.add(produit.getCategorie());                              // le TreeSet ignore les doublons
    }

    /** C2 - Modifier désignation, catégorie, prix, seuil. La RÉFÉRENCE n'est pas modifiable. */
    public void modifierProduit(String reference, String designation, String categorie,
                                BigDecimal prix, int seuil) {
        Produit p = obtenir(reference);
        p.setDesignation(designation);          // les setters du modèle contrôlent chaque valeur
        p.setCategorie(categorie);
        p.setPrixUnitaireHT(prix);              // le prix des lignes déjà créées ne change pas (prix figé)
        p.setSeuilAlerte(seuil);
        recalculerCategories();                 // l'ancienne catégorie peut avoir disparu
    }

    /**
     * C3 - Règle 6 : refuser UNIQUEMENT si le produit figure dans une commande VALIDEE.
     * Décision du groupe : s'il figure dans des commandes EN_COURS, la suppression est permise et
     * ses lignes sont retirées de ces commandes. Renvoie les numéros des commandes touchées.
     */
    public ArrayList<String> supprimerProduit(String reference) {
        obtenir(reference);                                                   // erreur si introuvable
        if (commandeService != null && commandeService.produitDansCommandeValidee(reference)) {
            throw new ErreurMetier("Suppression refusée : le produit " + reference
                    + " figure dans une commande validée.");
        }
        ArrayList<String> touchees = new ArrayList<>();
        if (commandeService != null) {
            touchees = commandeService.retirerProduitDesCommandesEnCours(reference);
        }
        catalogue.remove(reference);
        recalculerCategories();                                               // une catégorie peut disparaître
        return touchees;
    }

    /** C4 - Entrée en stock (quantité > 0). */
    public void reapprovisionner(String reference, int quantite) {
        Produit p = obtenir(reference);
        p.ajouterStock(quantite);               // contrôle quantité > 0 dans le modèle
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.ENTREE, quantite, "Réapprovisionnement");
        }
    }

    // ================= Accès utilisés par les autres modules =================

    /** Renvoie le produit ou lance ErreurMetier("Produit introuvable : ...") si la référence est inconnue. */
    public Produit obtenir(String reference) {
        Produit p = (reference == null) ? null : catalogue.get(reference.trim());
        if (p == null) {
            throw new ErreurMetier("Produit introuvable : " + reference);
        }
        return p;
    }

    /** Tous les produits (copie). Utilisé par le tableau de bord et la sauvegarde. */
    public ArrayList<Produit> tousLesProduits() {
        return new ArrayList<>(catalogue.values());
    }

    /** Catégories triées (copie du TreeSet). */
    public TreeSet<String> getCategories() {
        return new TreeSet<>(categories);
    }

    /** Appelé par CommandeService.valider : sortie de stock d'un produit (règle 2 : jamais négatif). */
    public void sortirDuStock(String reference, int quantite, String motif) {
        Produit p = obtenir(reference);
        p.retirerStock(quantite);               // lance ErreurMetier si le stock devient négatif
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.SORTIE, quantite, motif);
        }
    }

    /** Appelé par CommandeService.annuler (commande validée) : restitution du stock. */
    public void restituerAuStock(String reference, int quantite, String motif) {
        Produit p = obtenir(reference);
        p.ajouterStock(quantite);
        if (journal != null) {
            journal.enregistrer(reference, TypeMouvement.ENTREE, quantite, motif);
        }
    }

    /** Vide le catalogue (utilisé seulement par le rechargement d'une sauvegarde, bonus H4). */
    public void vider() {
        catalogue.clear();
        categories.clear();
    }

    // ================= C5 à C7 : listes, recherche, alertes =================

    /** C5 - Copie du catalogue triée par désignation (insensible à la casse). */
    public ArrayList<Produit> listerParDesignation() {
        ArrayList<Produit> liste = tousLesProduits();                         // copie : le catalogue ne bouge pas
        Collections.sort(liste, new Comparator<Produit>() {
            @Override
            public int compare(Produit a, Produit b) {
                return a.getDesignation().compareToIgnoreCase(b.getDesignation());
            }
        });
        return liste;
    }

    /** C5 - Tri par prix unitaire HT croissant. */
    public ArrayList<Produit> listerParPrix() {
        ArrayList<Produit> liste = tousLesProduits();
        Collections.sort(liste, new Comparator<Produit>() {
            @Override
            public int compare(Produit a, Produit b) {
                return a.getPrixUnitaireHT().compareTo(b.getPrixUnitaireHT());
            }
        });
        return liste;
    }

    /** C5 - Tri par quantité en stock croissante. */
    public ArrayList<Produit> listerParQuantite() {
        ArrayList<Produit> liste = tousLesProduits();
        Collections.sort(liste, new Comparator<Produit>() {
            @Override
            public int compare(Produit a, Produit b) {
                return Integer.compare(a.getQuantiteStock(), b.getQuantiteStock());
            }
        });
        return liste;
    }

    /** C6 - Recherche par référence exacte (0 ou 1 résultat). */
    public ArrayList<Produit> rechercherParReference(String reference) {
        ArrayList<Produit> resultat = new ArrayList<>();
        Produit p = (reference == null) ? null : catalogue.get(reference.trim());
        if (p != null) {
            resultat.add(p);
        }
        return resultat;
    }

    /** C6 - Recherche par fragment de désignation, insensible à la casse. */
    public ArrayList<Produit> rechercherParDesignation(String fragment) {
        ArrayList<Produit> resultat = new ArrayList<>();
        String motif = (fragment == null) ? "" : fragment.trim().toLowerCase();
        for (Produit p : listerParDesignation()) {                            // liste déjà triée : résultat trié
            if (p.getDesignation().toLowerCase().contains(motif)) {
                resultat.add(p);
            }
        }
        return resultat;
    }

    /** C6 - Recherche par catégorie (nom exact, insensible à la casse). */
    public ArrayList<Produit> rechercherParCategorie(String categorie) {
        ArrayList<Produit> resultat = new ArrayList<>();
        String cible = (categorie == null) ? "" : categorie.trim();
        for (Produit p : listerParDesignation()) {
            if (p.getCategorie().equalsIgnoreCase(cible)) {
                resultat.add(p);
            }
        }
        return resultat;
    }

    /** C7 - Produits dont quantité <= seuil, du plus bas stock au plus haut. */
    public ArrayList<Produit> produitsEnAlerte() {
        ArrayList<Produit> alertes = new ArrayList<>();
        for (Produit p : catalogue.values()) {
            if (p.estEnAlerte()) {
                alertes.add(p);
            }
        }
        Collections.sort(alertes, new Comparator<Produit>() {
            @Override
            public int compare(Produit a, Produit b) {
                return Integer.compare(a.getQuantiteStock(), b.getQuantiteStock());
            }
        });
        return alertes;
    }

    /** Reconstruit le TreeSet des catégories à partir du catalogue (après modification ou suppression). */
    private void recalculerCategories() {
        categories.clear();
        for (Produit p : catalogue.values()) {
            categories.add(p.getCategorie());
        }
    }
}
