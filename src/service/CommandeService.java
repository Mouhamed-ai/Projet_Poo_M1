package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import modele.Commande;
import modele.StatutCommande;

/**
 * MODULE COMMANDES  -  Responsable : Aminata (tâches E1 à E5 et aides) ; la section « Consultation » (E8) est à Moustapha.
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

    /** E1 - Client existant obligatoire. Numéro "CMD" + compteur sur 3 chiffres. Ranger dans les DEUX maps. */
    public Commande creerCommande(String codeClient, LocalDate date) { throw ATraiter.pour("E1", "créer une commande"); }

    /** E1 - Variante : date du jour (appelle la précédente avec LocalDate.now()). */
    public Commande creerCommande(String codeClient) { return creerCommande(codeClient, LocalDate.now()); }

    /**
     * E2 - Règle 5 : commande modifiable ? Règle 4 : produit déjà présent => augmenter la quantité
     * de la ligne existante (Commande.trouverLigne), sinon nouvelle LigneCommande (prix figé).
     * Contrôle du stock dès l'ajout (décision du groupe) : la quantité TOTALE de la ligne (existante + ajoutée)
     * doit être <= stock disponible, sinon ErreurMetier. Le stock n'est PAS décrémenté ici (c'est E4).
     * Le contrôle est refait à la validation, car le stock peut avoir changé entre-temps.
     */
    public void ajouterLigne(String numeroCommande, String referenceProduit, int quantite) {
        throw ATraiter.pour("E2", "ajouter une ligne");
    }

    /** E3 - Règle 5. Remplacer la quantité d'une ligne existante (même contrôle de stock que E2). */
    public void modifierQuantite(String numeroCommande, String referenceProduit, int nouvelleQuantite) {
        throw ATraiter.pour("E3", "modifier une quantité");
    }

    /** E3 - Règle 5. Retirer une ligne AVEC UN Iterator (iterator.remove()). */
    public void retirerLigne(String numeroCommande, String referenceProduit) {
        throw ATraiter.pour("E3", "retirer une ligne");
    }

    /**
     * E4 - VALIDATION EN DEUX PASSES (règle 3) :
     *   passe 1 : au moins une ligne ? stock suffisant pour CHAQUE ligne ? sinon ErreurMetier, RIEN n'est modifié ;
     *   passe 2 : seulement si tout est bon -> stockService.sortirDuStock pour chaque ligne, puis statut VALIDEE.
     */
    public void valider(String numeroCommande) { throw ATraiter.pour("E4", "valider une commande"); }

    /**
     * E5 - Interdire si déjà ANNULEE. Si la commande était VALIDEE : stockService.restituerAuStock
     * pour chaque ligne (une seule fois). Statut -> ANNULEE.
     */
    public void annuler(String numeroCommande) { throw ATraiter.pour("E5", "annuler une commande"); }

    // ---------- Aides utilisées par tous (Aminata, à écrire en premier le mercredi) ----------
    /** Renvoie la commande ou lance ErreurMetier("Commande introuvable : ..."). */
    public Commande obtenir(String numero) { throw ATraiter.pour("AIDE", "obtenir une commande"); }

    // ---------- Consultation (E8, Moustapha) ----------

    /** Toutes les commandes (copie). Utilisé par le tableau de bord et la sauvegarde. */
    public ArrayList<Commande> listerToutes() { throw ATraiter.pour("E8", "lister toutes les commandes"); }

    /** Commandes ayant un statut donné. */
    public ArrayList<Commande> listerParStatut(StatutCommande statut) { throw ATraiter.pour("E8", "lister par statut"); }

    /** Historique d'un client (liste vide si aucune commande). */
    public ArrayList<Commande> commandesDuClient(String codeClient) { throw ATraiter.pour("AIDE", "commandes d'un client"); }

    // ---------- Services rendus aux autres modules (règle 6 + décision D) ----------
    /** Vrai si le produit figure dans une commande VALIDEE (suppression refusée, règle 6). */
    public boolean produitDansCommandeValidee(String referenceProduit) { throw ATraiter.pour("AIDE", "produit dans commande validée"); }

    /**
     * Retire (Iterator) toutes les lignes du produit dans les commandes EN_COURS.
     * Renvoie les numéros des commandes touchées (liste vide si aucune), pour le message à l'utilisateur.
     */
    public ArrayList<String> retirerProduitDesCommandesEnCours(String referenceProduit) { throw ATraiter.pour("AIDE", "retirer le produit des commandes en cours"); }

    /** Vrai si le client a une commande EN_COURS ou VALIDEE. */
    public boolean clientADesCommandesActives(String codeClient) { throw ATraiter.pour("AIDE", "client avec commandes actives"); }
}
