package ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/** Petits outils d'affichage pour que toutes les sorties se ressemblent. */
public final class Affichage {

    private static final DecimalFormat FORMAT_MONTANT;     // ex. 1 050 937,50

    static {                                               // bloc exécuté une fois au chargement de la classe
        DecimalFormatSymbols symboles = new DecimalFormatSymbols();
        symboles.setGroupingSeparator(' ');                // séparateur de milliers : espace simple
        symboles.setDecimalSeparator(',');                 // virgule décimale à la française
        FORMAT_MONTANT = new DecimalFormat("#,##0.00", symboles);
        FORMAT_MONTANT.setRoundingMode(RoundingMode.HALF_UP);   // arrondi demandé par la décision F
    }

    private Affichage() { }

    /** Montant formaté : "1 050 937,50 FCFA". */
    public static String montant(BigDecimal montant) {
        return FORMAT_MONTANT.format(montant) + " FCFA";
    }

    /** Titre encadré. */
    public static void titre(String texte) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  " + texte);
        System.out.println("==================================================");
    }
}
