package modele;

import java.math.BigDecimal;

/**
 * Un produit du catalogue.
 * ENCAPSULATION : tous les attributs sont private ; on ne les modifie que par des
 * méthodes qui VÉRIFIENT les règles (prix > 0, stock jamais négatif : règle 2).
 * Ainsi il est IMPOSSIBLE de fabriquer un produit invalide, où que l'on soit dans le code.
 */
public class Produit {

    private final String reference;        // identifiant unique (règle 1) ; final = jamais modifiée
    private String designation;            // nom affiché
    private String categorie;              // ex. "Fournitures", "Informatique"
    private BigDecimal prixUnitaireHT;     // BigDecimal : calcul monétaire exact (pas de float/double)
    private int quantiteStock;             // quantité disponible (>= 0 toujours)
    private int seuilAlerte;               // en dessous (ou égal), on signale un stock bas

    public Produit(String reference, String designation, String categorie,
                   BigDecimal prixUnitaireHT, int quantiteStock, int seuilAlerte) {
        this.reference = Controles.texteNonVide(reference, "La référence"); // vérifie puis affecte
        setDesignation(designation);                                      // réutilise les contrôles des setters
        setCategorie(categorie);
        setPrixUnitaireHT(prixUnitaireHT);
        this.quantiteStock = Controles.entierPositifOuNul(quantiteStock, "La quantité en stock");
        setSeuilAlerte(seuilAlerte);
    }

    // ---------- Getters (lecture) ----------
    public String getReference() { return reference; }
    public String getDesignation() { return designation; }
    public String getCategorie() { return categorie; }
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public int getQuantiteStock() { return quantiteStock; }
    public int getSeuilAlerte() { return seuilAlerte; }

    // ---------- Setters (écriture contrôlée) ----------
    public void setDesignation(String designation) {
        this.designation = Controles.texteNonVide(designation, "La désignation");
    }

    public void setCategorie(String categorie) {
        this.categorie = Controles.texteNonVide(categorie, "La catégorie");
    }

    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) {
        this.prixUnitaireHT = Controles.prixStrictementPositif(prixUnitaireHT);
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = Controles.entierPositifOuNul(seuilAlerte, "Le seuil d'alerte");
    }

    // ---------- Opérations sur le stock (le seul moyen de changer quantiteStock) ----------
    /** Entrée en stock : quantité ajoutée strictement positive. */
    public void ajouterStock(int quantite) {
        Controles.entierStrictementPositif(quantite, "La quantité ajoutée");
        this.quantiteStock += quantite;                // on augmente le stock
    }

    /** Sortie de stock : refusée si elle rendrait le stock négatif (règle 2). */
    public void retirerStock(int quantite) {
        Controles.entierStrictementPositif(quantite, "La quantité retirée");
        if (quantite > quantiteStock) {                // pas assez de stock
            throw new ErreurMetier("Stock insuffisant pour " + reference
                    + " : demandé " + quantite + ", disponible " + quantiteStock + ".");
        }
        this.quantiteStock -= quantite;                // on diminue le stock
    }

    /** Vrai si le stock est inférieur OU ÉGAL au seuil (cas limite du sujet). */
    public boolean estEnAlerte() {
        return quantiteStock <= seuilAlerte;
    }

    @Override
    public String toString() {
        return reference + " | " + designation + " | " + categorie + " | "
                + prixUnitaireHT.toPlainString() + " FCFA | stock=" + quantiteStock
                + " | seuil=" + seuilAlerte + (estEnAlerte() ? "  [ALERTE]" : "");
    }
}
