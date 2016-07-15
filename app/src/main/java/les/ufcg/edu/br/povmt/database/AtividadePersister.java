package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.models.Atividade;

/**
 * Created by Notebook on 14/07/2016.
 */
public class AtividadePersister {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private static AtividadePersister atividadePersister;

    public AtividadePersister(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public static AtividadePersister getInstance(Context context) {
        if (atividadePersister == null) {
            atividadePersister = new AtividadePersister(context);
        }
        return atividadePersister;
    }

    public long inserirAtividade(Atividade atividade, String idUser) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_ID, atividade.getId());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        return getDatabase().insert(dbHelper.ATIVIDADE_NOME_TABELA, null, contentValues);
    }

    public long atualizarAtividade(Atividade atividade, String idUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.ATIVIDADE_ID, atividade.getId());
        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        return getDatabase().update(dbHelper.ATIVIDADE_NOME_TABELA, contentValues,
                dbHelper.ATIVIDADE_ID + " = '" + String.valueOf(atividade.getId())
                        + "'", null);
    }

    public int deleteAtividade(String idUser, String nomeAtividade) {
        return getDatabase().delete(dbHelper.ATIVIDADE_NOME_TABELA, dbHelper.USUARIO_ID +
                " = '" + idUser + "' AND " + dbHelper.ATIVIDADE_NOME + " = '" + nomeAtividade + "'", null);
    }

    public List<Atividade> getAtividades(String idUser) {
        String[] columns = new String []{dbHelper.ATIVIDADE_ID, dbHelper.ATIVIDADE_NOME,
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE,
                idUser};
        Cursor cursor = getDatabase().query(dbHelper.ATIVIDADE_NOME_TABELA, columns, null, null, null, null, null, null);

        List<Atividade> atividades = new ArrayList<>();
        while (cursor.moveToNext()) {
            Atividade model = createAtividade(cursor);
            atividades.add(model);
        }
        cursor.close();
        return atividades;
    }

    private Atividade createAtividade(Cursor cursor) {
        Atividade model = new Atividade(cursor.getLong(cursor.getColumnIndex(dbHelper.ATIVIDADE_ID)),
                cursor.getLong(cursor.getColumnIndex(dbHelper.ATIVIDADE_USUARIO_FK)),
                cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_NOME)));
        return model;
    }

    private SQLiteDatabase getDatabase() {
        if (this.database == null) {
            this.database = this.dbHelper.getWritableDatabase();
        }
        return this.database;
    }
}
