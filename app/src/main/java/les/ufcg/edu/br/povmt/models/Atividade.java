package les.ufcg.edu.br.povmt.models;

/**
 * Created by Notebook on 14/07/2016.
 */
public class Atividade {
    private byte[] id;
    private String nome;
    private Categoria categoria;
    private Prioridade prioridade;

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

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}
