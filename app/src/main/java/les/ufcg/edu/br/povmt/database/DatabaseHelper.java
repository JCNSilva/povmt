package les.ufcg.edu.br.povmt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Julio on 13/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;


    //------------------ INFORMAÇÕES GERAIS -----------------------
    public static final int VERSAO_BD = 10;
    public static final String NOME_DB = "POVMTDatabase";



    //-----------------------TABELA DE USUARIOS --------------------
    public static final String USUARIO_NOME_TABELA = "tabUsuario";
    public static final String USUARIO_ID = "_id";
    public static final String USUARIO_NOME = "nome";
    public static final String USUARIO_EMAIL = "email";
    public static final String USUARIO_URL = "url";
    public static final String USUARIO_HORA_MODIFICACAO = "horaModificacao";

    public static final String SQL_USUARIO_CRIAR_TABELA = "CREATE TABLE "
            + USUARIO_NOME_TABELA + "("
            + USUARIO_ID + " TEXT NOT NULL PRIMARY KEY, "
            + USUARIO_NOME + " TEXT NOT NULL, "
            + USUARIO_EMAIL + " TEXT NOT NULL, "
            + USUARIO_URL + " TEXT NOT NULL, "
            + USUARIO_HORA_MODIFICACAO + " TEXT);";

    public static final String SQL_USUARIO_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + USUARIO_NOME_TABELA + ";";



    //---------------- TABELA DE ATIVIDADES -----------------------
    public static final String ATIVIDADE_NOME_TABELA = "tabAtividade";
    public static final String ATIVIDADE_ID = "_id";
    public static final String ATIVIDADE_NOME = "nome";
    public static final String ATIVIDADE_CATEGORIA = "categoria";
    public static final String ATIVIDADE_PRIORIDADE = "prioridade";
    public static final String ATIVIDADE_FOTO = "foto";
    public static final String ATIVIDADE_HORA_MODIFICACAO = "horaModificacao";
    public static final String ATIVIDADE_USUARIO_FK = "usuario";

    public static final String SQL_ATIVIDADE_CRIAR_TABELA = "CREATE TABLE "
            + ATIVIDADE_NOME_TABELA + "("
            + ATIVIDADE_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + ATIVIDADE_NOME + " TEXT NOT NULL, "
            + ATIVIDADE_CATEGORIA + " TEXT, "
            + ATIVIDADE_PRIORIDADE + " TEXT NOT NULL, "
            + ATIVIDADE_FOTO + " TEXT, "
            + ATIVIDADE_HORA_MODIFICACAO + " TEXT, "
            + ATIVIDADE_USUARIO_FK + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + ATIVIDADE_USUARIO_FK + ") REFERENCES "
            + USUARIO_NOME_TABELA + " (" + USUARIO_ID + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE);";

    public static final String SQL_ATIVIDADE_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + ATIVIDADE_NOME_TABELA + ";";



    //---------------------- TABELA DE TIS ------------------------
    public static final String TI_NOME_TABELA = "tabTI";
    public static final String TI_ID = "_id";
    public static final String TI_DATA = "data";
    public static final String TI_SEMANA = "semana";
    public static final String TI_HORAS = "horas";
    public static final String TI_HORA_MODIFICACAO = "horaModificacao";
    public static final String TI_ATIVIDADE_FK = "atividade";

    public static final String SQL_TI_CRIAR_TABELA = "CREATE TABLE "
            + TI_NOME_TABELA + "("
            + TI_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + TI_DATA + " TEXT NOT NULL, "
            + TI_SEMANA + " INTEGER NOT NULL, "
            + TI_HORAS + " INTEGER NOT NULL, "
            + TI_HORA_MODIFICACAO + " TEXT, "
            + TI_ATIVIDADE_FK + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + TI_ATIVIDADE_FK + ") REFERENCES "
            + ATIVIDADE_NOME_TABELA + " (" + ATIVIDADE_ID + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE);";

    public static final String SQL_TI_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + TI_NOME_TABELA + ";";



    //-----------------------TABELA DE TAGS -----------------------
    public static final String TAG_NOME_TABELA = "tabTag";
    public static final String TAG_NOME = "nome";
    public static final String TAG_ID = "_id";
    public static final String TAG_HORA_MODIFICADA = "horaModificada";
    public static final String TAG_ATIVIDADE_FK = "atividade";

    public static final String SQL_TAG_CRIAR_TABELA = "CREATE TABLE "
            + TAG_NOME_TABELA + "("
            + TAG_ID + " INTEGER NOT NULL, "
            + TAG_NOME + " TEXT NOT NULL, "
            + TAG_HORA_MODIFICADA + " TEXT, "
            + TAG_ATIVIDADE_FK + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + TAG_ATIVIDADE_FK + ") REFERENCES "
            + ATIVIDADE_NOME_TABELA + " (" + ATIVIDADE_ID + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE, "
            + "PRIMARY KEY(" + TAG_ID + ", " + TAG_ATIVIDADE_FK + "));";

    public static final String SQL_TAG_DELETAR_TABELA  = "DROP TABLE IF EXISTS "
            + TAG_NOME_TABELA + ";";



    //----------------------MÉTODOS E CONSTRUTORES ------------------
    public DatabaseHelper(Context context) {
        super(context, NOME_DB, null, VERSAO_BD);
    }

    public static DatabaseHelper getInstance(final Context context) {
        synchronized (DatabaseHelper.class) {
            if (instance == null) {
                instance = new DatabaseHelper(context);
            }
            return instance;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase dbHelper) {
        dbHelper.execSQL(SQL_ATIVIDADE_CRIAR_TABELA);
        dbHelper.execSQL(SQL_TI_CRIAR_TABELA);
        dbHelper.execSQL(SQL_TAG_CRIAR_TABELA);
        dbHelper.execSQL(SQL_USUARIO_CRIAR_TABELA);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.database.sqlite.SQLiteOpenHelper
     * #onUpgrade(android.database.sqlite .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase dbHelper, int oldVersion,
                          int newVersion) {
        dbHelper.execSQL(SQL_ATIVIDADE_DELETAR_TABELA);
        dbHelper.execSQL(SQL_TI_DELETAR_TABELA);
        dbHelper.execSQL(SQL_TAG_DELETAR_TABELA);
        dbHelper.execSQL(SQL_USUARIO_DELETAR_TABELA);
        onCreate(dbHelper);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onDowngrade(android.database
     * .sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onDowngrade(SQLiteDatabase dbHelper, int oldVersion,
                            int newVersion) {
        onUpgrade(dbHelper, oldVersion, newVersion);
    }
}