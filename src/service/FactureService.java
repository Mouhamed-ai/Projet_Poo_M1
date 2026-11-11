package service;

import java.math.BigDecimal;

import modele.Client;
import modele.Commande;
import modele.Facture;

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
     * Calcul : totalHT -> remise -> HT après remise -> TVA (18 % du HT après remise) -> TTC exact.
     * Montants intermédiaires à 2 décimales : setScale(2, RoundingMode.HALF_UP).
     * Décision du groupe : TTC à payer = TTC exact arrondi au multiple de 5 F le plus proche
     * (milieu vers le haut) : ttcExact.divide(PAS_ARRONDI, 0, RoundingMode.HALF_UP).multiply(PAS_ARRONDI).
     * arrondi = totalTTC - ttcExact. Exemple CMD001 : TVA 160 312,50 ; TTC exact 1 050 937,50 ; arrondi +2,50 ; TTC 1 050 940.
     */
    public Facture calculer(Commande commande) { throw ATraiter.pour("E6", "calculer la facture"); }

    /** E6 - Règle 8 : 5 % si client ENTREPRISE ET totalHT > 500 000 ; sinon 0. (Tests T11, T12, T13.) */
    public BigDecimal calculerRemise(Client client, BigDecimal totalHT) { throw ATraiter.pour("E6", "calculer la remise"); }

    /** E7 - Met la facture en forme de texte (lignes, HT, remise, HT après remise, TVA, ligne « Arrondi », TTC). Réutilisé par l'export bonus H2. */
    public String formater(Facture facture) { throw ATraiter.pour("E7", "formater la facture"); }

    /** E7 - Affiche la facture à l'écran. */
    public void afficher(Facture facture) { System.out.println(formater(facture)); }
}
