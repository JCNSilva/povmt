package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public long inserirAtividade(Atividade atividade) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ATIVIDADE_ID, atividade.getId());
        contentValues.put(DatabaseHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(DatabaseHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(DatabaseHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());

        return getDatabase().insert(DatabaseHelper.ATIVIDADE_NOME_TABELA, null, contentValues);
    }

    public long updateAtividade(Atividade atividade) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ATIVIDADE_ID, atividade.getId());
        contentValues.put(DatabaseHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(DatabaseHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(DatabaseHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());

        return getDatabase().update(DatabaseHelper.ATIVIDADE_NOME_TABELA, contentValues,
                DatabaseHelper.ATIVIDADE_ID + " = '" + String.valueOf(atividade.getId())
                        + "'", null);
    }

    public int deleteAtividade(String idUser, String nomeAtividade) {
        return getDatabase().delete(DatabaseHelper.ATIVIDADE_NOME_TABELA, DatabaseHelper.USUARIO_ID +
                " = '" + "' AND " + DatabaseHelper.ATIVIDADE_NOME + " = '" + nomeAtividade + "'", null);
    }

    private SQLiteDatabase getDatabase() {
        if (this.database == null) {
            this.database = this.dbHelper.getWritableDatabase();
        }
        return this.database;
    }
}
