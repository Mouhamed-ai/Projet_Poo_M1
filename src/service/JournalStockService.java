package service;

import java.util.ArrayList;

import modele.MouvementStock;
import modele.TypeMouvement;

/**
 * (BONUS H1) Journal des mouvements de stock  -  Responsable : Pape Mamadou.
 * Collection : ArrayList<MouvementStock> (ordre chronologique conservé naturellement).
 */
public class JournalStockService {

    private final ArrayList<MouvementStock> mouvements = new ArrayList<>();

    /** Ajoute un mouvement daté (appelé par StockService). */
    public void enregistrer(String referenceProduit, TypeMouvement type, int quantite, String motif) {
        throw ATraiter.pour("H1", "JournalStockService.enregistrer");
    }

    /** Tout le journal (copie). */
    public ArrayList<MouvementStock> lister() { throw ATraiter.pour("H1", "lister le journal"); }

    /** Mouvements d'un seul produit. */
    public ArrayList<MouvementStock> listerParProduit(String referenceProduit) { throw ATraiter.pour("H1", "journal par produit"); }
}
