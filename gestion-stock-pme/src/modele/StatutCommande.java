package modele;

/**
 * Cycle de vie d'une commande.
 * EN_COURS -> VALIDEE  (validation : le stock est décrémenté)
 * EN_COURS -> ANNULEE  (annulation : aucun stock touché)
 * VALIDEE  -> ANNULEE  (annulation : le stock est restitué)
 * Une commande VALIDEE ou ANNULEE n'est plus modifiable (règle 5).
 */
public enum StatutCommande {
    EN_COURS,   // commande en préparation : on peut encore modifier ses lignes
    VALIDEE,    // commande confirmée : stock sorti, facture éditable
    ANNULEE     // commande annulée : définitivement figée
}
