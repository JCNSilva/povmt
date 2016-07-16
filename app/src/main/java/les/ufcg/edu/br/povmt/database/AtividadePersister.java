package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.Categoria;
import les.ufcg.edu.br.povmt.models.Prioridade;

/**
 * Created by Mendel on 14/07/2016.
 */
public class AtividadePersister {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private static AtividadePersister atividadePersister;

    public AtividadePersister(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public static AtividadePersister getInstance(Context context) {
        if (atividadePersister == null) {
            atividadePersister = new AtividadePersister(context);
        }
        return atividadePersister;
    }


    public void open() {
        database = dbHelper.getWritableDatabase();
    }


    private SQLiteDatabase getDatabase() {
        if (this.database == null) {
            this.database = this.dbHelper.getWritableDatabase();
        }
        return this.database;
    }


    public long inserirAtividade(Atividade atividade, long idUser) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        long id = getDatabase().insert(dbHelper.ATIVIDADE_NOME_TABELA, null, contentValues);
        atividade.setId(id);
        return id;
    }


    public long atualizarAtividade(Atividade atividade, long idUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        return getDatabase().update(dbHelper.ATIVIDADE_NOME_TABELA, contentValues,
                dbHelper.ATIVIDADE_ID + " = '" + String.valueOf(atividade.getId())
                        + "'", null);
    }


    public int deleteAtividade(long idAtividade) {
        return getDatabase().delete(dbHelper.ATIVIDADE_NOME_TABELA, dbHelper.ATIVIDADE_ID + " = '" + idAtividade + "'", null);
    }


    public List<Atividade> getAtividades(long idUser) {
        String[] columns = new String []{dbHelper.ATIVIDADE_ID, dbHelper.ATIVIDADE_NOME,
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE};
        Cursor cursor = getDatabase().query(dbHelper.ATIVIDADE_NOME_TABELA, columns,
                dbHelper.ATIVIDADE_USUARIO_FK + " = '" + idUser + "'", null, null, null, null, null);

        List<Atividade> atividades = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Atividade model = createAtividade(cursor);
            atividades.add(model);
        }
        cursor.close();
        return atividades;
    }


    public Atividade getAtividade(long idAtividade) {
        String[] columns = new String []{dbHelper.ATIVIDADE_ID, dbHelper.ATIVIDADE_NOME,
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE};
        Cursor cursor = getDatabase().query(dbHelper.ATIVIDADE_NOME_TABELA, columns,
                dbHelper.ATIVIDADE_ID + " = '" + idAtividade + "'", null, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            Atividade model = createAtividade(cursor);
            cursor.close();
            return model;
        }
        cursor.close();
        return null;
    }



    private Atividade createAtividade(Cursor cursor) {
        Atividade model = new Atividade(cursor.getLong(cursor.getColumnIndex(dbHelper.ATIVIDADE_ID)),
                cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_NOME)),
                getCategoria(cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_CATEGORIA))),
                getPrioridade(cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_PRIORIDADE))));
        return model;
    }

    private Prioridade getPrioridade(String strPrioridade){
        switch(strPrioridade.toLowerCase()){
            case "baixa":
                return Prioridade.BAIXA;
            case "media":
                return Prioridade.MEDIA;
            case "alta":
                return Prioridade.ALTA;
            default:
                return Prioridade.MEDIA;
        }
    }

    private Categoria getCategoria(String strCategoria){
        switch(strCategoria.toLowerCase()){
            case "trabalho":
                return Categoria.TRABALHO;
            case "lazer":
                return Categoria.LAZER;
            default:
                return Categoria.TRABALHO;
        }
    }
}
