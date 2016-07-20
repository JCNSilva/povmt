package les.ufcg.edu.br.povmt.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mendel on 14/07/2016.
 */
public class Atividade implements Comparable<Atividade> {
    private long id;
    private String nome;
    private Categoria categoria;
    private Prioridade prioridade;
    private List<TI> tiList;
    private String urlFoto;

    public Atividade(long id, String nome, Categoria categoria, Prioridade prioridade, String urlFoto) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.tiList = new ArrayList<>();
        this.urlFoto = urlFoto;
    }

    public Atividade(String nome, Categoria categoria, Prioridade prioridade, String urlFoto) {
        this(0, nome, categoria, prioridade, urlFoto);
        this.tiList = new ArrayList<>();
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

    public List<TI> getTiList() {
        return tiList;
    }

    public void setTiList(List<TI> tiList) {
        this.tiList = tiList;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public void addTI(TI ti) {
        if (ti == null) {
            return;
        }
        tiList.add(ti);
    }

    public void removeTI(TI ti) {
        if (ti == null) {
            return;
        }
        tiList.remove(ti);
    }

    public int getTI() {
        int soma = 0;
        for (TI ti : tiList) {
            soma += ti.getHoras();
        }
        return soma;
    }

    @Override
    public int compareTo(Atividade aAtividade) {
        if (this.getTI() < aAtividade.getTI()) {
            return -1;
        } else if (this.getTI() > aAtividade.getTI()) {
            return 1;
        }
        return 0;
    }
}
