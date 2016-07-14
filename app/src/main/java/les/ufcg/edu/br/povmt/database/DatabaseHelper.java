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
    public static final int VERSAO_BD = 1;
    public static final String NOME_DB = "POVMTDatabase";



    //---------------- TABELA DE ATIVIDADES -----------------------
    public static final String ATIVIDADE_NOME_TABELA = "tabAtividade";
    public static final String ATIVIDADE_ID = "_id";
    public static final String ATIVIDADE_NOME = "nome";
    public static final String ATIVIDADE_CATEGORIA = "categoria";
    public static final String ATIVIDADE_PRIORIDADE = "prioridade";
    public static final String SQL_ATIVIDADE_CRIAR_TABELA = "CREATE TABLE "
            + ATIVIDADE_NOME_TABELA + "("
            + ATIVIDADE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + ATIVIDADE_NOME + " TEXT NOT NULL, "
            + ATIVIDADE_CATEGORIA + " TEXT, "
            + ATIVIDADE_PRIORIDADE + " TEXT NOT NULL);";
    public static final String SQL_ATIVIDADE_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + ATIVIDADE_NOME_TABELA + ";";



    //---------------------- TABELA DE TIS ------------------------
    public static final String TI_NOME_TABELA = "tabTI";
    public static final String TI_ID = "_id";
    public static final String TI_DATA = "data";
    public static final String TI_SEMANA = "semana";
    public static final String TI_HORAS = "horas";
    public static final String TI_FOTO = "foto";
    public static final String TI_ATIVIDADE_FK = "atividade";
    public static final String SQL_TI_CRIAR_TABELA = "CREATE TABLE "
            + TI_NOME_TABELA + "("
            + TI_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + TI_DATA + " INTEGER NOT NULL, "
            + TI_SEMANA + " INTEGER NOT NULL, "
            + TI_HORAS + " INTEGER NOT NULL, "
            + TI_FOTO + " TEXT NOT NULL, "
            + TI_ATIVIDADE_FK + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + TI_ATIVIDADE_FK + ") REFERENCES "
            + ATIVIDADE_NOME_TABELA + " (" + ATIVIDADE_ID + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE);";
    public static final String SQL_TI_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + TI_NOME_TABELA + ";";



    //-----------------------TABELA DE TAGS -----------------------
    public static final String TAG_NOME_TABELA = "tabTag";
    public static final String TAG_NOME = "nome";
    public static final String SQL_TAG_CRIAR_TABELA = "CREATE TABLE "
            + TAG_NOME_TABELA + "("
            + TAG_NOME + " TEXT NOT NULL PRIMARY KEY);";
    public static final String SQL_TAG_DELETAR_TABELA  = "DROP TABLE IF EXISTS "
            + TAG_NOME_TABELA + ";";



    //// TODO: 13/07/2016  
    //-----------------------TABELA DE USUARIOS --------------------
    public static final String USUARIO_NOME_TABELA = "tabUsuario";
    public static final String SQL_USUARIO_CRIAR_TABELA = "CREATE TABLE "
            + USUARIO_NOME_TABELA + "("+");";
    public static final String SQL_USUARIO_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + USUARIO_NOME_TABELA + ";";



    //-----------------------TABELA TAG-ATIVIDADE ------------------
    public static final String TAGATIVIDADE_NOME_TABELA = "tagAtividadeTab";
    public static final String TAGATIVIDADE_TAG_FK = "tag";
    public static final String TAGATIVIDADE_ATIVIDADE_FK = "atividade";
    public static final String SQL_TAGATIVIDADE_CRIAR_TABELA = "CREATE TABLE "
            + TAGATIVIDADE_NOME_TABELA + "("
            + TAGATIVIDADE_TAG_FK + " INTEGER NOT NULL, "
            + TAGATIVIDADE_ATIVIDADE_FK + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + TAGATIVIDADE_TAG_FK + ") REFERENCES "
            + TAG_NOME_TABELA + " (" + TAG_NOME + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE, "
            + "FOREIGN KEY (" + TAGATIVIDADE_ATIVIDADE_FK + ") REFERENCES "
            + ATIVIDADE_NOME_TABELA + " (" + ATIVIDADE_ID + ") "
            + "ON DELETE CASCADE ON UPDATE CASCADE, "
            + "PRIMARY KEY(" + TAGATIVIDADE_TAG_FK + ", " + TAGATIVIDADE_ATIVIDADE_FK + "));";
    public static final String SQL_TAGATIVIDADE_DELETAR_TABELA = "DROP TABLE IF EXISTS "
            + TAGATIVIDADE_NOME_TABELA + ";";

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
        dbHelper.execSQL(SQL_TAGATIVIDADE_CRIAR_TABELA);
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
        dbHelper.execSQL(SQL_TAGATIVIDADE_DELETAR_TABELA);
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