package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.Categoria;
import les.ufcg.edu.br.povmt.models.Prioridade;
import les.ufcg.edu.br.povmt.models.TI;
import les.ufcg.edu.br.povmt.models.Usuario;

/**
 * Created by Julio on 28/07/2016.
 */
public class DataSource {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private static DataSource dataSource;
    /** Constructor of the DataSource
     *   @param context
     */
    private DataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    /** Method to open the database
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public static DataSource getInstance(Context context){
        if (dataSource == null){
            dataSource = new DataSource(context);
        }
        return dataSource;
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
        if(cursor.getCount() > 0){
            do {
                Atividade model = createAtividade(cursor);
                atividades.add(model);
            } while(cursor.moveToNext());
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

        model.setId(idAtividade);
        model.setTiList(getTIs(idAtividade));
        return model;
    }


    public long inserirTI(TI ti, long idAtividade) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.TI_DATA, ti.getData());
        contentValues.put(dbHelper.TI_SEMANA, ti.getSemana());
        contentValues.put(dbHelper.TI_HORAS, ti.getHoras());
        contentValues.put(dbHelper.TI_ATIVIDADE_FK, idAtividade);

        long id = getDatabase().insert(dbHelper.TI_NOME_TABELA, null, contentValues);
        ti.setId(id);
        return id;
    }

    public TI getTI(long idTI) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_DATA,
                dbHelper.TI_HORAS, dbHelper.TI_SEMANA};

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
                dbHelper.TI_HORAS, dbHelper.TI_SEMANA};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns,
                dbHelper.TI_ATIVIDADE_FK + " = '" + idAtividade + "'", null, null, null, null, null);

        List<TI> tisList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                TI model = createTI(cursor);
                tisList.add(model);
            } while(cursor.moveToNext());
        }

        cursor.close();
        return tisList;
    }

    public List<TI> getTISemana(long idAtividade, int semana) {
        String[] columns = new String[] {dbHelper.TI_ID, dbHelper.TI_DATA,
                dbHelper.TI_HORAS, dbHelper.TI_SEMANA};

        Cursor cursor = getDatabase().query(dbHelper.TI_NOME_TABELA, columns,
                dbHelper.TI_ATIVIDADE_FK + " = '" + idAtividade + "' AND " +
                        dbHelper.TI_SEMANA + " = '" + semana + "'", null, null, null, null, null);

        List<TI> tisList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                TI model = createTI(cursor);
                tisList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tisList;
    }

    private TI createTI(Cursor cursor) {
        TI model = new TI(cursor.getLong(cursor.getColumnIndex(dbHelper.TI_ID)),
                cursor.getString(cursor.getColumnIndex(dbHelper.TI_DATA)),
                cursor.getInt(cursor.getColumnIndex(dbHelper.TI_SEMANA)),
                cursor.getInt(cursor.getColumnIndex(dbHelper.TI_HORAS)));
        return model;
    }


    public String inserirUsuario(Usuario usuario) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.USUARIO_ID, usuario.getId());
        contentValues.put(dbHelper.USUARIO_NOME, usuario.getNome());
        contentValues.put(dbHelper.USUARIO_EMAIL, usuario.getEmail());
        contentValues.put(dbHelper.USUARIO_URL, usuario.getUrl());

        getDatabase().insert(dbHelper.USUARIO_NOME_TABELA, null, contentValues);

        for(Atividade atividade: usuario.getAtividadeList()){
            inserirAtividade(atividade, usuario.getId());
        }
        return usuario.getId();
    }

    public int setDataSincronizacaoUsuario(String idUsuario, String timestamp){
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.USUARIO_HORA_MODIFICACAO, timestamp);

        return getDatabase().update(dbHelper.USUARIO_NOME_TABELA, contentValues,
                dbHelper.USUARIO_ID + " = '" + idUsuario + "'", null);
    }

    public String getDataSincronizacaoUsuario(String idUsuario){
        String[] columns = new String []{dbHelper.USUARIO_HORA_MODIFICACAO};

        Cursor cursor = getDatabase().query(dbHelper.USUARIO_NOME_TABELA,
                columns, dbHelper.USUARIO_ID + " = '" + idUsuario + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            String dataModificacao = cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_HORA_MODIFICACAO));
            cursor.close();
            return dataModificacao;
        }
        return null;
    }

    public int atualizarUsuario(Usuario usuario) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.USUARIO_NOME, usuario.getNome());
        contentValues.put(dbHelper.USUARIO_EMAIL, usuario.getEmail());
        contentValues.put(dbHelper.USUARIO_URL, usuario.getUrl());

        int linhasAfetadas = getDatabase().update(dbHelper.USUARIO_NOME_TABELA, contentValues,
                dbHelper.USUARIO_ID + " = '" + usuario.getId()
                        + "'", null);

        for(Atividade atividade: usuario.getAtividadeList()){
            atualizarAtividade(atividade, usuario.getId());
        }
        return linhasAfetadas;

    }

    public int deletarUsuario(String idUser) {
        return getDatabase().delete(dbHelper.USUARIO_NOME_TABELA, dbHelper.USUARIO_ID +
                " = '" + idUser + "'", null);
    }

    public Usuario recuperarUsuario(String idUser) {
        String[] columns = new String []{dbHelper.USUARIO_ID, dbHelper.USUARIO_NOME,
                dbHelper.USUARIO_EMAIL, dbHelper.USUARIO_URL};

        Cursor cursor = getDatabase().query(dbHelper.USUARIO_NOME_TABELA,
                columns, dbHelper.USUARIO_ID + " = '" + idUser + "'", null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            Usuario usuario = createUsuario(cursor);
            usuario.setAtividadeList(getAtividades(idUser));
            cursor.close();
            return usuario;
        }

        cursor.close();
        return null;
    }

    private Usuario createUsuario(Cursor cursor) {
        Usuario model = new Usuario(cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_ID)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_NOME)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_EMAIL)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_URL)));
        return model;
    }
}
