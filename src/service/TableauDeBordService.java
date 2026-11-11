package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * (BONUS H3) Tableau de bord  -  Responsable : Pape Mamadou.
 * Définition retenue : chiffre d'affaires = somme des HT APRÈS remise des commandes VALIDEES,
 * rattachées au mois de la date de la commande. Collections : TreeMap (mois triés), entrySet().
 */
public class TableauDeBordService {

    private final CommandeService commandeService;
    private final FactureService factureService;

    public TableauDeBordService(CommandeService commandeService, FactureService factureService) {
        this.commandeService = commandeService;
        this.factureService = factureService;
    }

    /** Chiffre d'affaires total. */
    public BigDecimal chiffreAffairesTotal() { throw ATraiter.pour("H3", "CA total"); }

    /** CA par mois : clé "2026-11" -> montant. TreeMap => mois affichés dans l'ordre. */
    public TreeMap<String, BigDecimal> chiffreAffairesParMois() { throw ATraiter.pour("H3", "CA par mois"); }

    /** Top 5 des produits vendus (référence -> quantité totale), du plus vendu au moins vendu. */
    public ArrayList<Map.Entry<String, Integer>> top5Produits() { throw ATraiter.pour("H3", "top 5 produits"); }

    /** Meilleurs clients (code client -> CA), limité à 'nombre' entrées. */
    public ArrayList<Map.Entry<String, BigDecimal>> meilleursClients(int nombre) { throw ATraiter.pour("H3", "meilleurs clients"); }
}
