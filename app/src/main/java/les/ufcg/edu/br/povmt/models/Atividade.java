package les.ufcg.edu.br.povmt.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Notebook on 14/07/2016.
 */
public class Atividade implements Comparable<Atividade> {
    private long id;
    private int tempoInvestido;
    private String nome;
    private String urlFoto;
    private Calendar data;
    private Categoria categoria;
    private Prioridade prioridade;

    public Atividade(long id, String nome, Categoria categoria, Prioridade prioridade, String urlFoto,
                     int tempoInvestido, Calendar data) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.tempoInvestido = tempoInvestido;
        this.urlFoto = urlFoto;
        this.data = data;
    }

    public Atividade(String nome, Categoria categoria, Prioridade prioridade, String urlFoto,
                     int tempoInvestido, Calendar data) {
        this(0, nome, categoria, prioridade, urlFoto, tempoInvestido, data);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTempoInvestido() {
        return tempoInvestido;
    }

    public void setTempoInvestido(int tempoInvestido) {
        this.tempoInvestido = tempoInvestido;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public void incrementaTempoInvestido(int tempo){
        this.tempoInvestido += tempo;
    }

    public void decrementaTempoInvestido(int tempo){
        this.tempoInvestido -= tempo;
    }

    @Override
    public int compareTo(Atividade atividade) {
        if(this.tempoInvestido < atividade.getTempoInvestido()) {
            return -1;
        } else if (this.tempoInvestido > atividade.getTempoInvestido()) {
            return 1;
        }
        return 0;
    }
}
