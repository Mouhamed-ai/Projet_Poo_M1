package modele;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Résultat d'un calcul de facture (objet en lecture seule : pas de setters).
 * Le CALCUL est fait UNE SEULE FOIS, dans service.FactureService (décision F).
 * Ordre : total HT -> remise -> HT après remise -> TVA 18 % -> TTC exact -> arrondi -> total TTC à payer.
 * Décision du groupe : le TTC à payer est arrondi au multiple de 5 F le plus proche (milieu vers le haut),
 * et l'écart apparaît sur une ligne « Arrondi » (positif ou négatif).
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
    private final BigDecimal ttcExact;             // htApresRemise + tva (avant arrondi)
    private final BigDecimal arrondi;              // totalTTC - ttcExact (peut être négatif ou nul)
    private final BigDecimal totalTTC;             // ttcExact + arrondi : multiple de 5 F, montant à payer

    public Facture(String numeroCommande, Client client, LocalDate dateFacture,
                   ArrayList<LigneCommande> lignes, BigDecimal totalHT, BigDecimal remise,
                   BigDecimal htApresRemise, BigDecimal tva, BigDecimal ttcExact,
                   BigDecimal arrondi, BigDecimal totalTTC) {
        this.numeroCommande = numeroCommande;
        this.client = client;
        this.dateFacture = dateFacture;
        this.lignes = new ArrayList<>(lignes);     // copie défensive : la facture ne bouge plus
        this.totalHT = totalHT;
        this.remise = remise;
        this.htApresRemise = htApresRemise;
        this.tva = tva;
        this.ttcExact = ttcExact;
        this.arrondi = arrondi;
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
    public BigDecimal getTtcExact() { return ttcExact; }
    public BigDecimal getArrondi() { return arrondi; }
    public BigDecimal getTotalTTC() { return totalTTC; }
}
