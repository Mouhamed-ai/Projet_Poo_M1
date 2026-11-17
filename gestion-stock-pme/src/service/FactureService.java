package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import modele.Client;
import modele.Commande;
import modele.ErreurMetier;
import modele.Facture;
import modele.LigneCommande;
import modele.StatutCommande;
import modele.TypeClient;
import ui.Affichage;

/**
 * MODULE FACTURATION  -  Responsable : Moustapha (tâches E6 à E7).
 * UN SEUL endroit calcule la facture (décision F), pour ne pas avoir deux formules différentes.
 */
public class FactureService {

    public static final BigDecimal TAUX_TVA = new BigDecimal("0.18");          // TVA 18 %
    public static final BigDecimal TAUX_REMISE = new BigDecimal("0.05");       // remise 5 %
    public static final BigDecimal SEUIL_REMISE = new BigDecimal("500000");    // remise si HT STRICTEMENT > 500 000
    public static final BigDecimal PAS_ARRONDI = new BigDecimal("5");         // TTC arrondi au multiple de 5 F

    /**
     * E6/E7 - Règle 7 : refuser si la commande n'est pas VALIDEE.
     * Calcul : totalHT -> remise -> HT après remise -> TVA (18 % du HT après remise) -> TTC exact -> arrondi.
     * Montants intermédiaires à 2 décimales (HALF_UP). TTC à payer = TTC exact arrondi au multiple de 5 F
     * le plus proche (milieu vers le haut) ; arrondi = TTC à payer - TTC exact.
     */
    public Facture calculer(Commande commande) {
        if (commande.getStatut() != StatutCommande.VALIDEE) {                       // règle 7
            throw new ErreurMetier("Une facture ne peut être éditée que pour une commande validée "
                    + "(statut actuel : " + commande.getStatut() + ").");
        }
        BigDecimal ht = commande.calculerTotalHT().setScale(2, RoundingMode.HALF_UP);
        BigDecimal remise = calculerRemise(commande.getClient(), ht);
        BigDecimal htApresRemise = ht.subtract(remise);
        BigDecimal tva = htApresRemise.multiply(TAUX_TVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ttcExact = htApresRemise.add(tva);
        BigDecimal ttc = ttcExact.divide(PAS_ARRONDI, 0, RoundingMode.HALF_UP)       // multiple de 5 le plus proche
                .multiply(PAS_ARRONDI).setScale(2);
        BigDecimal arrondi = ttc.subtract(ttcExact);
        return new Facture(commande.getNumero(), commande.getClient(), LocalDate.now(),
                commande.getLignes(), ht, remise, htApresRemise, tva, ttcExact, arrondi, ttc);
    }

    /** E6 - Règle 8 : 5 % si client ENTREPRISE ET totalHT strictement > 500 000 ; sinon 0. */
    public BigDecimal calculerRemise(Client client, BigDecimal totalHT) {
        if (client.getType() == TypeClient.ENTREPRISE && totalHT.compareTo(SEUIL_REMISE) > 0) {
            return totalHT.multiply(TAUX_REMISE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2);
    }

    /** E7 - Met la facture en forme de texte (lignes, HT, remise, HT après remise, TVA, ligne « Arrondi », TTC). */
    public String formater(Facture f) {
        StringBuilder sb = new StringBuilder();
        String trait = "----------------------------------------------------------------------------------";
        sb.append("================================= FACTURE ===================================\n");
        sb.append("Commande : ").append(f.getNumeroCommande()).append("    Date : ").append(f.getDateFacture()).append('\n');
        sb.append("Client   : ").append(f.getClient().getNom()).append(" (").append(f.getClient().getCode())
                .append(", ").append(f.getClient().getType()).append(")\n");
        sb.append(trait).append('\n');
        sb.append(String.format("%-6s %-32s %5s %17s %17s%n", "Réf.", "Désignation", "Qté", "Prix unit. HT", "Sous-total HT"));
        sb.append(trait).append('\n');
        for (LigneCommande l : f.getLignes()) {
            String designation = l.getProduit().getDesignation();
            if (designation.length() > 32) {
                designation = designation.substring(0, 31) + ".";
            }
            sb.append(String.format("%-6s %-32s %5d %17s %17s%n", l.getProduit().getReference(), designation,
                    l.getQuantite(), Affichage.montant(l.getPrixUnitaireHT()), Affichage.montant(l.getSousTotal())));
        }
        sb.append(trait).append('\n');
        ligne(sb, "Total HT", f.getTotalHT());
        if (f.getRemise().signum() > 0) {
            ligne(sb, "Remise 5 % (entreprise, HT > 500 000)", f.getRemise().negate());
        }
        ligne(sb, "HT après remise", f.getHtApresRemise());
        ligne(sb, "TVA 18 %", f.getTva());
        ligne(sb, "TTC exact", f.getTtcExact());
        ligne(sb, "Arrondi (multiple de 5 F)", f.getArrondi());
        sb.append(trait).append('\n');
        ligne(sb, "TOTAL TTC À PAYER", f.getTotalTTC());
        sb.append("==================================================================================\n");
        return sb.toString();
    }

    /** E7 - Affiche la facture à l'écran. */
    public void afficher(Facture facture) { System.out.println(formater(facture)); }

    private void ligne(StringBuilder sb, String libelle, BigDecimal montant) {
        sb.append(String.format("%-56s %27s%n", libelle, Affichage.montant(montant)));
    }
}
