package modele;

import java.time.LocalDateTime;

/** (BONUS H1) Une ligne du journal des mouvements de stock : entrée ou sortie datée. */
public class MouvementStock {

    private final LocalDateTime dateHeure;       // quand
    private final String referenceProduit;       // quel produit
    private final TypeMouvement type;            // ENTREE ou SORTIE
    private final int quantite;                  // combien
    private final String motif;                  // pourquoi (ex. "Réapprovisionnement", "Validation CMD001")

    public MouvementStock(String referenceProduit, TypeMouvement type, int quantite, String motif) {
        this.dateHeure = LocalDateTime.now();    // horodatage automatique
        this.referenceProduit = referenceProduit;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
    }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getReferenceProduit() { return referenceProduit; }
    public TypeMouvement getType() { return type; }
    public int getQuantite() { return quantite; }
    public String getMotif() { return motif; }

    @Override
    public String toString() {
        return dateHeure.withNano(0) + " | " + type + " | " + referenceProduit
                + " | " + quantite + " | " + motif;
    }
}
