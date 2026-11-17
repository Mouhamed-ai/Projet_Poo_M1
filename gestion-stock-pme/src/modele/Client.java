package modele;

/** Un client de l'entreprise. Le code est unique (règle 1) et ne change jamais. */
public class Client {

    private final String code;      // identifiant unique, final
    private String nom;
    private String telephone;
    private String email;
    private TypeClient type;        // PARTICULIER ou ENTREPRISE (détermine la remise)

    public Client(String code, String nom, String telephone, String email, TypeClient type) {
        this.code = Controles.texteNonVide(code, "Le code client");
        setNom(nom);                // les setters contiennent les contrôles
        setTelephone(telephone);
        setEmail(email);
        setType(type);
    }

    public String getCode() { return code; }
    public String getNom() { return nom; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public TypeClient getType() { return type; }

    public void setNom(String nom) { this.nom = Controles.texteNonVide(nom, "Le nom"); }

    public void setTelephone(String telephone) {
        this.telephone = Controles.texteNonVide(telephone, "Le téléphone");
    }

    public void setEmail(String email) {
        String e = Controles.texteNonVide(email, "L'email");       // d'abord non vide
        if (!e.contains("@")) {                                     // contrôle minimal de format
            throw new ErreurMetier("L'email doit contenir le caractère '@'.");
        }
        this.email = e;
    }

    public void setType(TypeClient type) {
        if (type == null) {
            throw new ErreurMetier("Le type de client est obligatoire (PARTICULIER ou ENTREPRISE).");
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return code + " | " + nom + " | " + telephone + " | " + email + " | " + type;
    }
}
