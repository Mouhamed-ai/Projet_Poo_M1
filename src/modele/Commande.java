package modele;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Une commande : un client, une date, un statut et des lignes.
 * Cette classe ne contient que les données et les calculs simples.
 * Les RÈGLES (validation, annulation, stock...) sont dans service.CommandeService.
 */
public class Commande {

    private final String numero;                                   // ex. "CMD001"
    private final Client client;                                   // client qui commande
    private final LocalDate date;                                  // date de la commande
    private StatutCommande statut;                                 // EN_COURS au départ
    private final ArrayList<LigneCommande> lignes = new ArrayList<>(); // lignes (ordre d'ajout conservé)

    public Commande(String numero, Client client, LocalDate date) {
        this.numero = Controles.texteNonVide(numero, "Le numéro de commande");
        if (client == null) {
            throw new ErreurMetier("Une commande doit être liée à un client.");
        }
        this.client = client;
        this.date = (date != null) ? date : LocalDate.now();       // date du jour par défaut
        this.statut = StatutCommande.EN_COURS;                     // toute commande démarre EN_COURS
    }

    public String getNumero() { return numero; }
    public Client getClient() { return client; }
    public LocalDate getDate() { return date; }
    public StatutCommande getStatut() { return statut; }

    /** Le service change le statut (EN_COURS -> VALIDEE / ANNULEE). */
    public void setStatut(StatutCommande statut) { this.statut = statut; }

    /**
     * Renvoie la VRAIE liste (pas une copie) : le service a besoin d'un Iterator
     * pour supprimer une ligne pendant un parcours (exigence du sujet).
     */
    public ArrayList<LigneCommande> getLignes() { return lignes; }

    /** Règle 5 : seule une commande EN_COURS peut être modifiée. */
    public boolean estModifiable() { return statut == StatutCommande.EN_COURS; }

    /** Cherche la ligne d'un produit (règle 4) ; renvoie null si absente. */
    public LigneCommande trouverLigne(String referenceProduit) {
        for (LigneCommande ligne : lignes) {                                   // parcours for-each
            if (ligne.getProduit().getReference().equals(referenceProduit)) {  // même référence ?
                return ligne;
            }
        }
        return null;                                                           // non trouvée
    }

    /** Total HT = somme des sous-totaux de toutes les lignes (avant remise). */
    public BigDecimal calculerTotalHT() {
        BigDecimal total = BigDecimal.ZERO;           // on part de 0
        for (LigneCommande ligne : lignes) {
            total = total.add(ligne.getSousTotal());  // BigDecimal est immuable : on réaffecte le résultat
        }
        return total;
    }

    @Override
    public String toString() {
        return numero + " | " + date + " | client " + client.getCode() + " | "
                + statut + " | " + lignes.size() + " ligne(s) | HT "
                + calculerTotalHT().toPlainString();
    }
}
