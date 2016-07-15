package les.ufcg.edu.br.povmt.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julio on 14/07/2016.
 */
public class Usuario {

    private long id;
    private String nome;
    private String email;
    private String url;
    private List<Atividade> atividadeList;

    public Usuario(final long id, String nome, String email, String url){
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.url = url;
        atividadeList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Atividade> getAtividadeList() {
        return atividadeList;
    }

    public void setAtividadeList(List<Atividade> atividadeList) {
        this.atividadeList = atividadeList;
    }

    public void addTI(Atividade atividade) {
        if (atividade == null) {
            return;
        }
        atividadeList.add(atividade);
    }

    public void removeTI(Atividade atividade) {
        if (atividade == null) {
            return;
        }
        atividadeList.remove(atividade);
    }
}
