package modele;

/** (BONUS H1) Sens d'un mouvement de stock dans le journal. */
public enum TypeMouvement {
    ENTREE,   // le stock augmente (réapprovisionnement, annulation d'une commande validée)
    SORTIE    // le stock diminue (validation d'une commande)
}
