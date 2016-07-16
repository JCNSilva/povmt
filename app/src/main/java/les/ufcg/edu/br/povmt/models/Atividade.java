package les.ufcg.edu.br.povmt.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Notebook on 14/07/2016.
 */
public class Atividade {
    private long id;
    private String nome;
    private Categoria categoria;
    private Prioridade prioridade;
    private List<TI> tiList;

    public Atividade(long id, String nome, Categoria categoria, Prioridade prioridade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.tiList = new ArrayList<>();
    }

    public Atividade(String nome, Categoria categoria, Prioridade prioridade) {
        this(0, nome, categoria, prioridade);
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
}
