package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import les.ufcg.edu.br.povmt.models.TI;

/**
 * Created by Julio on 16/07/2016.
 */
public class TIPersister {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private static TIPersister tiPersister;


    public TIPersister(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public static TIPersister getInstance(Context context) {
        if (tiPersister == null) {
            tiPersister = new TIPersister(context);
        }
        return tiPersister;
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


    public long inserirTI(TI ti, long idAtividade) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.TI_DATA, ti.getData().getTimeInMillis());
        contentValues.put(dbHelper.TI_SEMANA, ti.getData().get(Calendar.WEEK_OF_YEAR));
        contentValues.put(dbHelper.TI_HORAS, ti.getHoras());
        contentValues.put(dbHelper.TI_FOTO, ti.getUrlFoto());
        contentValues.put(dbHelper.TI_ATIVIDADE_FK, idAtividade);

        long id = getDatabase().insert(dbHelper.TI_NOME_TABELA, null, contentValues);
        ti.setId(id);
        return id;
    }


    public TI getTI(long idTI) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_DATA,
                dbHelper.TI_HORAS, dbHelper.TI_FOTO};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns, dbHelper.TI_ID + " = '"
                + idTI + "'", null, null, null, null);

        cursor.moveToFirst();
        if(cursor.getCount() == 1){
            TI ti = createTI(cursor);
            cursor.close();
            return ti;
        }
        cursor.close();
        return null;
    }


    public List<TI> getTIs(long idAtividade) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_DATA,
                dbHelper.TI_HORAS, dbHelper.TI_FOTO};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns,
                dbHelper.TI_ATIVIDADE_FK + " = '" + idAtividade + "'", null, null, null, null, null);

        List<TI> tisList = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            TI model = createTI(cursor);
            tisList.add(model);
        }

        cursor.close();
        return tisList;
    }


    private TI createTI(Cursor cursor) {
        TI model = new TI(cursor.getLong(cursor.getColumnIndex(dbHelper.TI_ID)),
                Calendar.getInstance(),
                cursor.getInt(cursor.getColumnIndex(dbHelper.TI_HORAS)),
                cursor.getString(cursor.getColumnIndex(dbHelper.TI_FOTO)));

        model.getData().setTimeInMillis(
                cursor.getLong(cursor.getColumnIndex(dbHelper.TI_DATA))
        );
        return model;
    }



}
