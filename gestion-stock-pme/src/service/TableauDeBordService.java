package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import modele.Commande;
import modele.LigneCommande;
import modele.StatutCommande;

/**
 * (BONUS H3) Tableau de bord  -  Responsable : Mouhamed.
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
    public BigDecimal chiffreAffairesTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Commande c : commandeService.listerParStatut(StatutCommande.VALIDEE)) {
            total = total.add(factureService.calculer(c).getHtApresRemise());
        }
        return total;
    }

    /** CA par mois : clé "2026-11" -> montant. TreeMap => mois affichés dans l'ordre. */
    public TreeMap<String, BigDecimal> chiffreAffairesParMois() {
        TreeMap<String, BigDecimal> parMois = new TreeMap<>();
        for (Commande c : commandeService.listerParStatut(StatutCommande.VALIDEE)) {
            String mois = String.format("%04d-%02d", c.getDate().getYear(), c.getDate().getMonthValue());
            BigDecimal ca = factureService.calculer(c).getHtApresRemise();
            BigDecimal deja = parMois.get(mois);
            parMois.put(mois, (deja == null) ? ca : deja.add(ca));
        }
        return parMois;
    }

    /** Top 5 des produits vendus (référence -> quantité totale), du plus vendu au moins vendu. */
    public ArrayList<Map.Entry<String, Integer>> top5Produits() {
        HashMap<String, Integer> quantites = new HashMap<>();
        for (Commande c : commandeService.listerParStatut(StatutCommande.VALIDEE)) {
            for (LigneCommande l : c.getLignes()) {
                String ref = l.getProduit().getReference();
                Integer deja = quantites.get(ref);
                quantites.put(ref, (deja == null ? 0 : deja) + l.getQuantite());
            }
        }
        ArrayList<Map.Entry<String, Integer>> liste = new ArrayList<>(quantites.entrySet());
        Collections.sort(liste, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());                    // décroissant
            }
        });
        return new ArrayList<>(liste.subList(0, Math.min(5, liste.size())));
    }

    /** Meilleurs clients (code client -> CA), limité à 'nombre' entrées. */
    public ArrayList<Map.Entry<String, BigDecimal>> meilleursClients(int nombre) {
        HashMap<String, BigDecimal> parClient = new HashMap<>();
        for (Commande c : commandeService.listerParStatut(StatutCommande.VALIDEE)) {
            String code = c.getClient().getCode();
            BigDecimal ca = factureService.calculer(c).getHtApresRemise();
            BigDecimal deja = parClient.get(code);
            parClient.put(code, (deja == null) ? ca : deja.add(ca));
        }
        ArrayList<Map.Entry<String, BigDecimal>> liste = new ArrayList<>(parClient.entrySet());
        Collections.sort(liste, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> a, Map.Entry<String, BigDecimal> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });
        return new ArrayList<>(liste.subList(0, Math.min(Math.max(nombre, 0), liste.size())));
    }
}
