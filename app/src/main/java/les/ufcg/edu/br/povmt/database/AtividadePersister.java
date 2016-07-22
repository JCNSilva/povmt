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
    private Context context;

    public AtividadePersister(Context context) {
        this.context = context;
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


    public long inserirAtividade(Atividade atividade, String idUser) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_FOTO, atividade.getUrlFoto());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        long id = getDatabase().insert(dbHelper.ATIVIDADE_NOME_TABELA, null, contentValues);
        atividade.setId(id);
        return id;
    }


    public long atualizarAtividade(Atividade atividade, String idUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.ATIVIDADE_NOME, atividade.getNome());
        contentValues.put(dbHelper.ATIVIDADE_CATEGORIA, atividade.getCategoria().toString());
        contentValues.put(dbHelper.ATIVIDADE_PRIORIDADE, atividade.getPrioridade().toString());
        contentValues.put(dbHelper.ATIVIDADE_FOTO, atividade.getUrlFoto());
        contentValues.put(dbHelper.ATIVIDADE_USUARIO_FK, idUser);

        return getDatabase().update(dbHelper.ATIVIDADE_NOME_TABELA, contentValues,
                dbHelper.ATIVIDADE_ID + " = '" + String.valueOf(atividade.getId())
                        + "'", null);
    }


    public int deleteAtividade(long idAtividade) {
        return getDatabase().delete(dbHelper.ATIVIDADE_NOME_TABELA, dbHelper.ATIVIDADE_ID + " = '" + idAtividade + "'", null);
    }


    public List<Atividade> getAtividades(String idUser) {
        String[] columns = new String []{dbHelper.ATIVIDADE_ID, dbHelper.ATIVIDADE_NOME,
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE, dbHelper.ATIVIDADE_FOTO};
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
                dbHelper.ATIVIDADE_CATEGORIA, dbHelper.ATIVIDADE_PRIORIDADE, dbHelper.ATIVIDADE_FOTO};
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
        final long idAtividade = cursor.getLong(cursor.getColumnIndex(dbHelper.ATIVIDADE_ID));

        Atividade model = new Atividade(idAtividade,
                cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_NOME)),
                Categoria.valueOf(cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_CATEGORIA))),
                Prioridade.valueOf(cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_PRIORIDADE))),
                cursor.getString(cursor.getColumnIndex(dbHelper.ATIVIDADE_FOTO)));

        TIPersister tip = new TIPersister(this.context);
        model.setTiList(tip.getTIs(idAtividade));
        return model;
    }
}
