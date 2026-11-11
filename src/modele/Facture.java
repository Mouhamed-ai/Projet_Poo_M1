package modele;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Résultat d'un calcul de facture (objet en lecture seule : pas de setters).
 * Le CALCUL est fait UNE SEULE FOIS, dans service.FactureService (décision F).
 * Ordre : total HT -> remise -> HT après remise -> TVA 18 % -> total TTC.
 */
public class Facture {

    private final String numeroCommande;           // la facture porte le numéro de sa commande
    private final Client client;
    private final LocalDate dateFacture;
    private final ArrayList<LigneCommande> lignes; // copie des lignes au moment de la facture
    private final BigDecimal totalHT;              // avant remise
    private final BigDecimal remise;               // montant de la remise (0 si aucune)
    private final BigDecimal htApresRemise;        // totalHT - remise
    private final BigDecimal tva;                  // 18 % de htApresRemise
    private final BigDecimal totalTTC;             // htApresRemise + tva

    public Facture(String numeroCommande, Client client, LocalDate dateFacture,
                   ArrayList<LigneCommande> lignes, BigDecimal totalHT, BigDecimal remise,
                   BigDecimal htApresRemise, BigDecimal tva, BigDecimal totalTTC) {
        this.numeroCommande = numeroCommande;
        this.client = client;
        this.dateFacture = dateFacture;
        this.lignes = new ArrayList<>(lignes);     // copie défensive : la facture ne bouge plus
        this.totalHT = totalHT;
        this.remise = remise;
        this.htApresRemise = htApresRemise;
        this.tva = tva;
        this.totalTTC = totalTTC;
    }

    public String getNumeroCommande() { return numeroCommande; }
    public Client getClient() { return client; }
    public LocalDate getDateFacture() { return dateFacture; }
    public ArrayList<LigneCommande> getLignes() { return lignes; }
    public BigDecimal getTotalHT() { return totalHT; }
    public BigDecimal getRemise() { return remise; }
    public BigDecimal getHtApresRemise() { return htApresRemise; }
    public BigDecimal getTva() { return tva; }
    public BigDecimal getTotalTTC() { return totalTTC; }
}
