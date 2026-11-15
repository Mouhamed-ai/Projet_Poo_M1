package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import modele.Client;
import modele.Commande;
import modele.Controles;
import modele.ErreurMetier;
import modele.LigneCommande;
import modele.Produit;
import modele.StatutCommande;

/**
 * MODULE COMMANDES  -  Responsable : Aminata (tâches E1 à E5 et aides) ; la section « Consultation » (E8)
 * est à Moustapha.
 * C'est le module le plus délicat : il touche au stock, aux clients et aux règles 3, 4, 5.
 *
 * Collections :
 *  - HashMap<String, Commande> commandes                      : accès direct par numéro
 *  - HashMap<String, ArrayList<Commande>> commandesParClient  : historique par client (code -> ses commandes)
 *  - (les lignes sont dans Commande : ArrayList<LigneCommande>)
 */
public class CommandeService {

    private final HashMap<String, Commande> commandes = new HashMap<>();
    private final HashMap<String, ArrayList<Commande>> commandesParClient = new HashMap<>();
    private int compteurNumero = 0;                    // sert à fabriquer CMD001, CMD002...

    private final StockService stockService;           // pour lire les produits et bouger le stock
    private final ClientService clientService;         // pour vérifier que le client existe

    public CommandeService(StockService stockService, ClientService clientService) {
        this.stockService = stockService;
        this.clientService = clientService;
    }

    /** E1 - Client existant obligatoire. Numéro "CMD" + compteur sur 3 chiffres. Rangée dans les DEUX maps. */
    public Commande creerCommande(String codeClient, LocalDate date) {
        Client client = clientService.obtenir(codeClient);                  // ErreurMetier si inconnu
        compteurNumero++;
        String numero = String.format("CMD%03d", compteurNumero);           // CMD001, CMD002...
        Commande commande = new Commande(numero, client, date);
        commandes.put(numero, commande);                                    // map 1 : par numéro
        ranger(commande);                                                   // map 2 : historique du client
        return commande;
    }

    /** E1 - Variante : date du jour (appelle la précédente avec LocalDate.now()). */
    public Commande creerCommande(String codeClient) { return creerCommande(codeClient, LocalDate.now()); }

    /**
     * E2 - Règle 5 : commande modifiable ? Règle 4 : produit déjà présent => augmenter la quantité
     * de la ligne existante, sinon nouvelle LigneCommande (prix figé).
     * Contrôle du stock dès l'ajout : quantité TOTALE de la ligne <= stock disponible.
     * Le stock n'est PAS décrémenté ici (c'est E4) ; le contrôle est refait à la validation.
     */
    public void ajouterLigne(String numeroCommande, String referenceProduit, int quantite) {
        Commande commande = obtenir(numeroCommande);
        verifierModifiable(commande);                                       // règle 5
        Controles.entierStrictementPositif(quantite, "La quantité");
        Produit produit = stockService.obtenir(referenceProduit);           // ErreurMetier si inconnu
        LigneCommande existante = commande.trouverLigne(referenceProduit);  // règle 4
        int total = quantite + (existante == null ? 0 : existante.getQuantite());
        verifierStock(produit, total);                                      // contrôle dès l'ajout
        if (existante == null) {
            commande.getLignes().add(new LigneCommande(produit, quantite)); // prix figé automatiquement
        } else {
            existante.ajouterQuantite(quantite);                            // une seule ligne par produit
        }
    }

    /** E3 - Règle 5. Remplacer la quantité d'une ligne existante (même contrôle de stock que E2). */
    public void modifierQuantite(String numeroCommande, String referenceProduit, int nouvelleQuantite) {
        Commande commande = obtenir(numeroCommande);
        verifierModifiable(commande);
        Controles.entierStrictementPositif(nouvelleQuantite, "La quantité");
        LigneCommande ligne = commande.trouverLigne(referenceProduit);
        if (ligne == null) {
            throw new ErreurMetier("Le produit " + referenceProduit + " n'est pas dans la commande " + numeroCommande + ".");
        }
        verifierStock(ligne.getProduit(), nouvelleQuantite);
        ligne.setQuantite(nouvelleQuantite);
    }

    /** E3 - Règle 5. Retirer une ligne AVEC UN Iterator (iterator.remove()). */
    public void retirerLigne(String numeroCommande, String referenceProduit) {
        Commande commande = obtenir(numeroCommande);
        verifierModifiable(commande);
        boolean retiree = false;
        Iterator<LigneCommande> it = commande.getLignes().iterator();
        while (it.hasNext()) {
            LigneCommande ligne = it.next();
            if (ligne.getProduit().getReference().equals(referenceProduit)) {
                it.remove();                                                // suppression sûre pendant le parcours
                retiree = true;
            }
        }
        if (!retiree) {
            throw new ErreurMetier("Le produit " + referenceProduit + " n'est pas dans la commande " + numeroCommande + ".");
        }
    }

    /**
     * E4 - VALIDATION EN DEUX PASSES (règle 3) :
     *   passe 1 : au moins une ligne ? stock suffisant pour CHAQUE ligne ? sinon ErreurMetier, RIEN n'est modifié ;
     *   passe 2 : seulement si tout est bon -> stockService.sortirDuStock pour chaque ligne, puis statut VALIDEE.
     */
    public void valider(String numeroCommande) {
        Commande commande = obtenir(numeroCommande);
        if (!commande.estModifiable()) {
            throw new ErreurMetier("La commande " + numeroCommande + " est " + commande.getStatut()
                    + " : seule une commande EN_COURS peut être validée.");
        }
        if (commande.getLignes().isEmpty()) {                               // règle 3 : au moins une ligne
            throw new ErreurMetier("Une commande sans ligne ne peut pas être validée.");
        }
        for (LigneCommande ligne : commande.getLignes()) {                  // PASSE 1 : on vérifie tout
            verifierStock(ligne.getProduit(), ligne.getQuantite());
        }
        for (LigneCommande ligne : commande.getLignes()) {                  // PASSE 2 : on applique
            stockService.sortirDuStock(ligne.getProduit().getReference(), ligne.getQuantite(),
                    "Validation " + numeroCommande);
        }
        commande.setStatut(StatutCommande.VALIDEE);
    }

    /**
     * E5 - Interdire si déjà ANNULEE. Si la commande était VALIDEE : restitution du stock pour chaque ligne
     * (une seule fois). Statut -> ANNULEE.
     */
    public void annuler(String numeroCommande) {
        Commande commande = obtenir(numeroCommande);
        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new ErreurMetier("La commande " + numeroCommande + " est déjà annulée.");
        }
        if (commande.getStatut() == StatutCommande.VALIDEE) {              // le stock avait été sorti : on le rend
            for (LigneCommande ligne : commande.getLignes()) {
                stockService.restituerAuStock(ligne.getProduit().getReference(), ligne.getQuantite(),
                        "Annulation " + numeroCommande);
            }
        }
        commande.setStatut(StatutCommande.ANNULEE);
    }

    // ---------- Aides utilisées par tous ----------

    /** Renvoie la commande ou lance ErreurMetier("Commande introuvable : ..."). */
    public Commande obtenir(String numero) {
        Commande c = (numero == null) ? null : commandes.get(numero.trim().toUpperCase());
        if (c == null) {
            throw new ErreurMetier("Commande introuvable : " + numero);
        }
        return c;
    }

    // ---------- Consultation (E8, Moustapha) ----------

    /** Toutes les commandes (copie), triées par numéro. Utilisé par le tableau de bord et la sauvegarde. */
    public ArrayList<Commande> listerToutes() {
        ArrayList<Commande> liste = new ArrayList<>(commandes.values());
        trierParNumero(liste);
        return liste;
    }

    /** Commandes ayant un statut donné, triées par numéro. */
    public ArrayList<Commande> listerParStatut(StatutCommande statut) {
        ArrayList<Commande> liste = new ArrayList<>();
        for (Commande c : commandes.values()) {
            if (c.getStatut() == statut) {
                liste.add(c);
            }
        }
        trierParNumero(liste);
        return liste;
    }

    /** Historique d'un client (liste vide si aucune commande). */
    public ArrayList<Commande> commandesDuClient(String codeClient) {
        ArrayList<Commande> liste = commandesParClient.get(codeClient);
        return (liste == null) ? new ArrayList<Commande>() : new ArrayList<>(liste);   // copie
    }

    // ---------- Services rendus aux autres modules (règle 6 + décision du groupe) ----------

    /** Vrai si le produit figure dans une commande VALIDEE (suppression refusée, règle 6). */
    public boolean produitDansCommandeValidee(String referenceProduit) {
        for (Commande c : commandes.values()) {
            if (c.getStatut() == StatutCommande.VALIDEE && c.trouverLigne(referenceProduit) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retire (Iterator) toutes les lignes du produit dans les commandes EN_COURS.
     * Renvoie les numéros des commandes touchées (liste vide si aucune).
     */
    public ArrayList<String> retirerProduitDesCommandesEnCours(String referenceProduit) {
        ArrayList<String> touchees = new ArrayList<>();
        for (Commande c : listerParStatut(StatutCommande.EN_COURS)) {
            Iterator<LigneCommande> it = c.getLignes().iterator();
            while (it.hasNext()) {
                if (it.next().getProduit().getReference().equals(referenceProduit)) {
                    it.remove();
                    if (!touchees.contains(c.getNumero())) {
                        touchees.add(c.getNumero());
                    }
                }
            }
        }
        return touchees;
    }

    /** Vrai si le client a une commande EN_COURS ou VALIDEE. */
    public boolean clientADesCommandesActives(String codeClient) {
        for (Commande c : commandesDuClient(codeClient)) {
            if (c.getStatut() != StatutCommande.ANNULEE) {
                return true;
            }
        }
        return false;
    }

    // ---------- Rechargement d'une sauvegarde (bonus H4) ----------

    /** Vide toutes les commandes et remet le compteur à zéro. */
    public void vider() {
        commandes.clear();
        commandesParClient.clear();
        compteurNumero = 0;
    }

    /** Remet une commande relue depuis un fichier, et recale le compteur sur le plus grand numéro. */
    public void restaurer(Commande commande) {
        commandes.put(commande.getNumero(), commande);
        ranger(commande);
        try {
            int n = Integer.parseInt(commande.getNumero().substring(3));
            if (n > compteurNumero) {
                compteurNumero = n;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // numéro inhabituel : on laisse le compteur tel quel
        }
    }

    // ---------- Outils privés ----------

    /** Range une commande dans la liste de son client (crée la liste au besoin). */
    private void ranger(Commande commande) {
        String code = commande.getClient().getCode();
        ArrayList<Commande> liste = commandesParClient.get(code);
        if (liste == null) {
            liste = new ArrayList<>();
            commandesParClient.put(code, liste);
        }
        liste.add(commande);
    }

    /** Règle 5 : seule une commande EN_COURS peut être modifiée. */
    private void verifierModifiable(Commande commande) {
        if (!commande.estModifiable()) {
            throw new ErreurMetier("La commande " + commande.getNumero() + " est " + commande.getStatut()
                    + " : elle n'est plus modifiable.");
        }
    }

    /** Refuse si la quantité demandée dépasse le stock disponible. */
    private void verifierStock(Produit produit, int quantiteDemandee) {
        if (quantiteDemandee > produit.getQuantiteStock()) {
            throw new ErreurMetier("Stock insuffisant pour " + produit.getReference() + " : demandé "
                    + quantiteDemandee + ", disponible " + produit.getQuantiteStock() + ".");
        }
    }

    private void trierParNumero(ArrayList<Commande> liste) {
        Collections.sort(liste, new Comparator<Commande>() {
            @Override
            public int compare(Commande a, Commande b) {
                return a.getNumero().compareTo(b.getNumero());
            }
        });
    }
}
