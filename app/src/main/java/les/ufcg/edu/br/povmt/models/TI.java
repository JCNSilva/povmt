package les.ufcg.edu.br.povmt.models;

/**
 * Created by Julio on 14/07/2016.
 */
public class TI {

    private long id;
    private String data;
    private int horas;
    private int semana;

    public TI(long id, String data, int semana, int horas) {
        this.id = id;
        this.data = data;
        this.horas = horas;
        this.semana = semana;
    }

    public TI(String data, int semana, int horas) {
        this(0, data, semana, horas);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }
}
