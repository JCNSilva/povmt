package les.ufcg.edu.br.povmt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import les.ufcg.edu.br.povmt.models.Usuario;

/**
 * Created by treinamento-09 on 15/07/16.
 */
public class UsuarioPersister {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private static UsuarioPersister usuarioPersister;

    public UsuarioPersister(Context context) {
            dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public static UsuarioPersister getInstance(Context context) {
        if (usuarioPersister == null) {
            usuarioPersister = new UsuarioPersister(context);
        }
        return usuarioPersister;
    }

    public long inserirUsuario(Usuario usuario) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.USUARIO_ID, usuario.getId());
        contentValues.put(dbHelper.USUARIO_NOME, usuario.getNome());
        contentValues.put(dbHelper.USUARIO_EMAIL, usuario.getEmail());
        contentValues.put(dbHelper.USUARIO_URL, usuario.getUrl());

        return getDatabase().insert(dbHelper.USUARIO_NOME_TABELA, null, contentValues);
    }

    public long atualizarUsuario(Usuario usuario) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(dbHelper.USUARIO_NOME, usuario.getNome());
        contentValues.put(dbHelper.USUARIO_EMAIL, usuario.getEmail());
        contentValues.put(dbHelper.USUARIO_URL, usuario.getUrl());

        return getDatabase().update(dbHelper.USUARIO_NOME_TABELA, contentValues,
                dbHelper.USUARIO_ID + " = '" + String.valueOf(usuario.getId())
                        + "'", null);
    }

    public int deleteAtividade(String idUser) {
        return getDatabase().delete(dbHelper.USUARIO_NOME_TABELA, dbHelper.USUARIO_ID +
                " = '" + idUser + "'", null);
    }

    public Usuario getUsuario() {
        String[] columns = new String []{dbHelper.USUARIO_ID, dbHelper.USUARIO_NOME,
                dbHelper.USUARIO_EMAIL, dbHelper.USUARIO_URL};

        Cursor cursor = getDatabase().query(dbHelper.USUARIO_NOME_TABELA,
                columns, null, null, null, null, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            Usuario usuario = createUsuario(cursor);
            return usuario;
        }

        cursor.close();
        return null;
    }

    private Usuario createUsuario(Cursor cursor) {
        Usuario model = new Usuario(cursor.getLong(cursor.getColumnIndex(dbHelper.USUARIO_ID)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_NOME)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_EMAIL)),
                cursor.getString(cursor.getColumnIndex(dbHelper.USUARIO_URL)));
        return model;
    }

    private SQLiteDatabase getDatabase() {
        if (this.database == null) {
            this.database = this.dbHelper.getWritableDatabase();
        }
        return this.database;
    }
}
