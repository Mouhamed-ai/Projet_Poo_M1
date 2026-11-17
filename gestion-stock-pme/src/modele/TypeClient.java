package modele;

/**
 * Type d'un client (énumération = liste FERMÉE de valeurs possibles).
 * Pourquoi une enum et pas un String ? Parce qu'on ne peut pas faire de faute de frappe :
 * le compilateur refuse TypeClient.PARTICULEIR, alors qu'il accepterait "PARTICULEIR".
 */
public enum TypeClient {
    PARTICULIER,   // client individuel : n'a jamais de remise
    ENTREPRISE     // société : 5 % de remise si le total HT dépasse 500 000 FCFA (règle 8)
}
