/** Controle automatique des regles metier (sans menu). Voir docs/PLAN_DE_TEST.md, section 6. */
import java.math.BigDecimal;
import java.time.LocalDate;
import modele.*;
import service.*;

public class VerifRegles {
    static int ok = 0, ko = 0;
    static void check(String nom, boolean cond) { if (cond) { ok++; } else { ko++; System.out.println("ECHEC : " + nom); } }
    static void refuse(String nom, Runnable r) {
        try { r.run(); ko++; System.out.println("ECHEC (aucun refus) : " + nom); }
        catch (ErreurMetier e) { ok++; }
    }
    public static void main(String[] a) {
        StockService st = new StockService(); ClientService cl = new ClientService();
        CommandeService co = new CommandeService(st, cl); FactureService fa = new FactureService();
        st.setCommandeService(co); cl.setCommandeService(co);
        st.ajouterProduit(new Produit("A", "Prod A", "Cat1", new BigDecimal("100000"), 10, 2));
        st.ajouterProduit(new Produit("B", "Prod B", "Cat2", new BigDecimal("1"), 5, 1));
        cl.ajouterClient(new Client("E", "Ent", "1", "e@x.sn", TypeClient.ENTREPRISE));
        cl.ajouterClient(new Client("P", "Part", "2", "p@x.sn", TypeClient.PARTICULIER));
        refuse("ref en double", () -> st.ajouterProduit(new Produit("A", "x", "y", BigDecimal.ONE, 1, 1)));
        refuse("prix 0", () -> new Produit("Z", "x", "y", BigDecimal.ZERO, 1, 1));
        refuse("stock negatif", () -> new Produit("Z", "x", "y", BigDecimal.ONE, -1, 1));
        refuse("client en double", () -> cl.ajouterClient(new Client("E", "x", "1", "e@x.sn", TypeClient.PARTICULIER)));
        // HT = 500 000 exactement : pas de remise
        Commande c1 = co.creerCommande("E", LocalDate.of(2026, 11, 1));
        co.ajouterLigne(c1.getNumero(), "A", 5);
        check("HT 500000", c1.calculerTotalHT().compareTo(new BigDecimal("500000")) == 0);
        refuse("facture non validee", () -> fa.calculer(c1));
        co.valider(c1.getNumero());
        Facture f1 = fa.calculer(c1);
        check("pas de remise a 500000", f1.getRemise().signum() == 0);
        check("TTC 590000", f1.getTotalTTC().compareTo(new BigDecimal("590000")) == 0);
        // HT = 500 001 : remise
        Commande c2 = co.creerCommande("E", LocalDate.of(2026, 11, 2));
        co.ajouterLigne(c2.getNumero(), "A", 5);
        co.ajouterLigne(c2.getNumero(), "B", 1);
        co.valider(c2.getNumero());
        Facture f2 = fa.calculer(c2);
        check("remise 25000.05", f2.getRemise().compareTo(new BigDecimal("25000.05")) == 0);
        check("TVA 85500.17", f2.getTva().compareTo(new BigDecimal("85500.17")) == 0);
        check("TTC exact 560501.12", f2.getTtcExact().compareTo(new BigDecimal("560501.12")) == 0);
        check("arrondi -1.12", f2.getArrondi().compareTo(new BigDecimal("-1.12")) == 0);
        check("TTC 560500", f2.getTotalTTC().compareTo(new BigDecimal("560500")) == 0);
        // particulier : jamais de remise meme grosse commande
        check("stock A apres 2 validations", st.obtenir("A").getQuantiteStock() == 0);
        Commande c3 = co.creerCommande("P");
        refuse("valider vide", () -> co.valider(c3.getNumero()));
        refuse("ajout > stock", () -> co.ajouterLigne(c3.getNumero(), "A", 1));
        co.ajouterLigne(c3.getNumero(), "B", 2);
        co.ajouterLigne(c3.getNumero(), "B", 2);
        check("une ligne par produit (merge)", c3.getLignes().size() == 1 && c3.getLignes().get(0).getQuantite() == 4);
        refuse("total fusionne > stock", () -> co.ajouterLigne(c3.getNumero(), "B", 5));
        // validation deux passes : on met 2 lignes, puis le stock du 2e baisse
        Commande c4 = co.creerCommande("P");
        st.reapprovisionner("A", 3);
        co.ajouterLigne(c4.getNumero(), "A", 3);
        co.ajouterLigne(c4.getNumero(), "B", 1);
        st.sortirDuStock("B", 4, "test");                        // B tombe a 0
        refuse("validation refusee (stock B)", () -> co.valider(c4.getNumero()));
        check("rien decremente (A intact)", st.obtenir("A").getQuantiteStock() == 3);
        check("statut reste EN_COURS", c4.getStatut() == StatutCommande.EN_COURS);
        st.reapprovisionner("B", 4);
        co.valider(c4.getNumero());
        refuse("modifier commande validee", () -> co.ajouterLigne(c4.getNumero(), "B", 1));
        co.annuler(c4.getNumero());
        check("annulation restitue A", st.obtenir("A").getQuantiteStock() == 3);
        refuse("annuler 2 fois", () -> co.annuler(c4.getNumero()));
        // suppression produit : refusee tant que le produit figure dans une commande VALIDEE
        refuse("suppression produit dans commande validee", () -> st.supprimerProduit("A"));
        System.out.println(ok + " controles reussis, " + ko + " echec(s).");
        System.exit(ko == 0 ? 0 : 1);
    }
}
