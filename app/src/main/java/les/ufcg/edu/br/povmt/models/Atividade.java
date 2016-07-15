package les.ufcg.edu.br.povmt.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Notebook on 14/07/2016.
 */
public class Atividade {
    private long id;
    private long idUser;
    private String nome;
    private Categoria categoria;
    private Prioridade prioridade;
    private List<TI> tiList;

    public Atividade(long id, long idUser, String nome) {
        this.id = id;
        this.idUser = idUser;
        this.nome = nome;
        tiList = new ArrayList<>();
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
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
