package service;

import java.util.ArrayList;

import modele.MouvementStock;
import modele.TypeMouvement;

/**
 * (BONUS H1) Journal des mouvements de stock  -  Responsable : Mouhamed.
 * Collection : ArrayList<MouvementStock> (ordre chronologique conservé naturellement).
 */
public class JournalStockService {

    private final ArrayList<MouvementStock> mouvements = new ArrayList<>();

    /** Ajoute un mouvement daté (appelé par StockService). */
    public void enregistrer(String referenceProduit, TypeMouvement type, int quantite, String motif) {
        mouvements.add(new MouvementStock(referenceProduit, type, quantite, motif));
    }

    /** Tout le journal (copie). */
    public ArrayList<MouvementStock> lister() {
        return new ArrayList<>(mouvements);
    }

    /** Mouvements d'un seul produit. */
    public ArrayList<MouvementStock> listerParProduit(String referenceProduit) {
        ArrayList<MouvementStock> resultat = new ArrayList<>();
        for (MouvementStock m : mouvements) {
            if (m.getReferenceProduit().equals(referenceProduit)) {
                resultat.add(m);
            }
        }
        return resultat;
    }
}
