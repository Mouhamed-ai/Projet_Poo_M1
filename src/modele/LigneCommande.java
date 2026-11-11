package modele;

import java.math.BigDecimal;

/**
 * Une ligne d'une commande : (produit, quantité).
 * IMPORTANT (décision E) : le prix unitaire est COPIÉ ("figé") à la création de la ligne.
 * Si le prix du catalogue change plus tard, les anciennes commandes et factures
 * gardent le prix qui était valable le jour de la vente.
 */
public class LigneCommande {

    private final Produit produit;                  // le produit commandé
    private int quantite;                           // quantité commandée (> 0)
    private final BigDecimal prixUnitaireHT;        // prix figé au moment de l'ajout

    public LigneCommande(Produit produit, int quantite) {
        if (produit == null) {
            throw new ErreurMetier("Le produit de la ligne est obligatoire.");
        }
        this.produit = produit;
        this.quantite = Controles.entierStrictementPositif(quantite, "La quantité");
        this.prixUnitaireHT = produit.getPrixUnitaireHT();   // copie du prix à cet instant
    }

    public Produit getProduit() { return produit; }
    public int getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }

    /** Règle 4 : ajouter un produit déjà présent => on augmente la quantité de la ligne. */
    public void ajouterQuantite(int supplement) {
        this.quantite += Controles.entierStrictementPositif(supplement, "La quantité ajoutée");
    }

    /** Remplace la quantité (utilisé par « modifier une quantité »). */
    public void setQuantite(int quantite) {
        this.quantite = Controles.entierStrictementPositif(quantite, "La quantité");
    }

    /** Sous-total HT = prix figé x quantité. */
    public BigDecimal getSousTotal() {
        return prixUnitaireHT.multiply(BigDecimal.valueOf(quantite));
    }

    @Override
    public String toString() {
        return produit.getReference() + " | " + produit.getDesignation() + " | "
                + prixUnitaireHT.toPlainString() + " x " + quantite
                + " = " + getSousTotal().toPlainString();
    }
}
