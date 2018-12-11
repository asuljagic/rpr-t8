package ba.unsa.etf.rpr.tutorijal08;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ValidatorPostanskogBroja implements Runnable {

    private String broj;
    private Boolean validan;

    ValidatorPostanskogBroja(String broj) {
        this.broj = broj;
        validan = null;
    }

    public boolean provjeriPostanskiBroj(String broj) throws Exception {
        // nadjeno na netu
        URL link = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + broj);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(link.openStream())
        );
        String sadrzaj = "";
        String line;
        while ((line = in.readLine()) != null)
            sadrzaj += line;

        System.out.println(sadrzaj);
        in.close();

        if (sadrzaj.equals("OK")) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {  // Runnable ne moze bacat izuzetke ali run moze baciti izuzetak RuntimeException
        try {
            System.out.println("provjeravam " + broj);
            validan = provjeriPostanskiBroj(broj);
        }
        catch (Exception e) {}
    }

    public Boolean getValidan() {
        return validan;
    }

    public void setBroj(String broj) {
        this.broj = broj;
    }
}
