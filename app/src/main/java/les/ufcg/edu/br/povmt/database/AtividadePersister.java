package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.TI;

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

    public long inserirAtividade(Atividade atividade, long idUser) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_ID, atividade.getId());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        return getDatabase().insert(dbHelper.ATIVIDADE_NOME_TABELA, null, contentValues);
    }

    public long inserirTI(TI ti, long idAtividade) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.TI_ATIVIDADE_FK, idAtividade);
        contentValues.put(dbHelper.TI_DATA, ti.getData().toString());
        contentValues.put(dbHelper.TI_HORAS, ti.getHoras());
        contentValues.put(dbHelper.TI_FOTO, ti.getUrlFoto());
        contentValues.put(dbHelper.TI_ID, ti.getId());

        return getDatabase().insert(dbHelper.TI_NOME_TABELA, null, contentValues);
    }

    public long atualizarAtividade(Atividade atividade, long idUser) {
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

    public int deleteAtividade(long idUser, String nomeAtividade) {
        return getDatabase().delete(dbHelper.ATIVIDADE_NOME_TABELA, dbHelper.USUARIO_ID +
                " = '" + idUser + "' AND " + dbHelper.ATIVIDADE_NOME + " = '" + nomeAtividade + "'", null);
    }

    public TI getTI(long idTI, long idAtividade, String fotoUrl, int horas ) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_FOTO,
                dbHelper.TI_HORAS, dbHelper.TI_ATIVIDADE_FK};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns, dbHelper.TI_ID + " = '"
                + idTI + "' AND " + dbHelper.TI_FOTO + " = '" + fotoUrl + "' AND "
                + dbHelper.TI_HORAS + " = '" + horas + "' AND " + dbHelper.TI_ATIVIDADE_FK
                + " = '" + idAtividade, null, null, null, null);

        TI ti = createTI(cursor);

        return ti;
    }

    private TI createTI(Cursor cursor) {
        TI model = new TI(cursor.getLong(cursor.getColumnIndex(dbHelper.TI_ID)),
                cursor.getLong(cursor.getColumnIndex(dbHelper.TI_ATIVIDADE_FK)),
                cursor.getInt(cursor.getColumnIndex(dbHelper.TI_HORAS)),
                cursor.getString(cursor.getColumnIndex(dbHelper.TI_FOTO)));

        return model;
    }

    public List<Atividade> getAtividades(long idUser) {
        String[] columns = new String []{dbHelper.ATIVIDADE_ID, dbHelper.ATIVIDADE_NOME,
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE,
                dbHelper.USUARIO_ID};
        Cursor cursor = getDatabase().query(dbHelper.ATIVIDADE_NOME_TABELA, columns,
                dbHelper.USUARIO_ID + " < '" + idUser + "'", null, null, null, null, null);

        List<Atividade> atividades = new ArrayList<>();
        while (cursor.moveToNext()) {
            Atividade model = createAtividade(cursor);
            atividades.add(model);
        }
        cursor.close();
        return atividades;
    }

    public List<TI> getTIs(long idAtividade) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_FOTO,
                dbHelper.TI_HORAS, dbHelper.TI_ATIVIDADE_FK};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns,
                dbHelper.ATIVIDADE_ID + " < '" + idAtividade + "'", null, null, null, null, null);

        List<TI> tisList = new ArrayList<>();

        while (cursor.moveToNext()) {
            TI model = createTI(cursor);
            tisList.add(model);
        }

        cursor.close();
        return tisList;
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
