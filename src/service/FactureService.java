package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import modele.Client;
import modele.Commande;
import modele.ErreurMetier;
import modele.Facture;
import modele.LigneCommande;
import modele.StatutCommande;
import modele.TypeClient;
import ui.Affichage;

/**
 * Service de facturation (tâches E6, E6 bis et E7 : Moustapha).
 *
 * Il n'y a QU'UN SEUL endroit qui calcule une facture : la méthode calculer().
 * Ordre imposé : HT, remise, HT après remise, TVA sur le HT après remise,
 * TTC exact, arrondi au multiple de 5 F le plus proche, TTC à payer.
 *
 * Tous les montants sont des BigDecimal (jamais de double pour de l'argent).
 * BigDecimal est immuable : chaque opération renvoie un nouveau nombre.
 */
public class FactureService {

    /** TVA de 18 % (règle 7 de l'énoncé). */
    public static final BigDecimal TAUX_TVA = new BigDecimal("0.18");

    /** Remise de 5 % accordée aux clients ENTREPRISE (règle 8). */
    public static final BigDecimal TAUX_REMISE = new BigDecimal("0.05");

    /** La remise s'applique seulement si le total HT dépasse STRICTEMENT ce seuil (500 000 F). */
    public static final BigDecimal SEUIL_REMISE = new BigDecimal("500000");

    /** Le TTC à payer est arrondi au multiple de 5 F le plus proche (décision du groupe, README section 7). */
    public static final BigDecimal PAS_ARRONDI = new BigDecimal("5");

    /** Mettre à true pour toujours afficher les lignes « Remise » et « HT après remise », même à 0. */
    private static final boolean AFFICHER_REMISE_NULLE = false;

    // ------------------------------------------------------------------ E6

    /**
     * Calcule la facture d'une commande (règle 7 : seule une commande VALIDEE est facturable).
     * Ne modifie jamais la commande : le résultat est un objet Facture en lecture seule.
     */
    public Facture calculer(Commande commande) {
        if (commande == null) {
            throw new ErreurMetier("Aucune commande à facturer.");
        }
        if (commande.getStatut() != StatutCommande.VALIDEE) {                       // règle 7
            throw new ErreurMetier("Une facture ne peut être éditée que pour une commande validée.");
        }
        BigDecimal ht = commande.calculerTotalHT();
        BigDecimal remise = calculerRemise(commande.getClient(), ht);
        BigDecimal htApresRemise = ht.subtract(remise);
        // la TVA se calcule sur le HT APRÈS remise, arrondie au centime
        BigDecimal tva = htApresRemise.multiply(TAUX_TVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ttcExact = htApresRemise.add(tva);
        // multiple de 5 le plus proche : on divise par 5, on arrondit à l'entier (le milieu va vers le haut), on remultiplie
        BigDecimal ttc = ttcExact.divide(PAS_ARRONDI, 0, RoundingMode.HALF_UP)
                .multiply(PAS_ARRONDI).setScale(2);
        BigDecimal arrondi = ttc.subtract(ttcExact);
        return new Facture(commande.getNumero(), commande.getClient(), LocalDate.now(),
                commande.getLignes(), ht, remise, htApresRemise, tva, ttcExact, arrondi, ttc);
    }

    /**
     * Règle 8 : 5 % du total HT pour un client ENTREPRISE dont le total HT dépasse STRICTEMENT 500 000 F.
     * Un total de 500 000 exactement ne donne donc aucune remise. Sinon, la remise vaut zéro.
     */
    public BigDecimal calculerRemise(Client client, BigDecimal totalHT) {
        if (client.getType() == TypeClient.ENTREPRISE && totalHT.compareTo(SEUIL_REMISE) > 0) {
            return totalHT.multiply(TAUX_REMISE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // ------------------------------------------------------------------ E7

    /** Construit le texte de la facture (sans rien afficher) : utile pour l'écran et pour l'export. */
    public String formater(Facture f) {
        if (f == null) {
            throw new ErreurMetier("Aucune facture à afficher.");
        }
        String traitDouble = repeter('=', 77);
        String traitSimple = repeter('-', 77);
        StringBuilder sb = new StringBuilder();
        sb.append(traitDouble).append('\n');
        sb.append("FACTURE - Commande ").append(f.getNumeroCommande()).append('\n');
        sb.append("Date    : ").append(f.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append('\n');
        sb.append("Client  : ").append(f.getClient().getCode()).append(" - ").append(f.getClient().getNom())
          .append(" (").append(f.getClient().getType()).append(")\n");
        sb.append(traitDouble).append('\n');
        sb.append(String.format("%-6s %-26s %5s %18s %18s%n", "Réf.", "Désignation", "Qté", "Prix unit. HT", "Sous-total HT"));
        sb.append(traitSimple).append('\n');
        for (LigneCommande l : f.getLignes()) {
            BigDecimal sousTotal = l.getSousTotal();
            sb.append(String.format("%-6s %-26s %5d %18s %18s%n",
                    l.getProduit().getReference(), tronquer(l.getProduit().getDesignation(), 26),
                    l.getQuantite(), Affichage.montant(l.getPrixUnitaireHT()), Affichage.montant(sousTotal)));
        }
        sb.append(traitSimple).append('\n');
        sb.append(ligneMontant("Total HT", Affichage.montant(f.getTotalHT())));
        if (f.getRemise().signum() > 0 || AFFICHER_REMISE_NULLE) {
            sb.append(ligneMontant("Remise entreprise (5 %)", "- " + Affichage.montant(f.getRemise())));
            sb.append(ligneMontant("Total HT après remise", Affichage.montant(f.getHtApresRemise())));
        }
        sb.append(ligneMontant("TVA (18 %)", Affichage.montant(f.getTva())));
        sb.append(ligneMontant("Total TTC exact", Affichage.montant(f.getTtcExact())));
        sb.append(ligneMontant("Arrondi (multiple de 5 F)",
                (f.getArrondi().signum() > 0 ? "+" : "") + Affichage.montant(f.getArrondi())));
        sb.append(traitSimple).append('\n');
        sb.append(ligneMontant("TTC À PAYER", Affichage.montant(f.getTotalTTC())));
        sb.append(traitDouble).append('\n');
        return sb.toString();
    }

    /** Affiche la facture à l'écran (méthode demandée par le guide, tâche E7). */
    public void afficher(Facture f) {
        System.out.println(formater(f));
    }

    // ---------------------------------------------------------- petits outils

    private String ligneMontant(String libelle, String montant) {
        return String.format("%-58s %18s%n", libelle, montant);
    }

    /** Coupe un texte trop long sans utiliser de tableau (substring). */
    private String tronquer(String texte, int max) {
        if (texte.length() <= max) {
            return texte;
        }
        return texte.substring(0, max - 1) + ".";
    }

    private String repeter(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
