package les.ufcg.edu.br.povmt.models;

import java.util.Calendar;

/**
 * Created by Julio on 14/07/2016.
 */
public class TI {

    private long id;
    private Calendar data;
    private int horas;

    public TI(long id, Calendar data, int horas) {
        this.id = id;
        this.data = data;
        this.horas = horas;
    }

    public TI(Calendar data, int horas, String urlFoto) {
        this(0, data, horas);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}
