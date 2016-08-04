package les.ufcg.edu.br.povmt.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.models.Usuario;
import les.ufcg.edu.br.povmt.utils.HttpPostAsyncTask;


public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String PREFERENCE_NAME = "USER_PREFERENCE";
    public static final String USER_NOME = "USER_NOME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_URL_PHOTO = "USER_URL_PHOTO";
    public static final String USER_ID = "USER_ID";

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SplashActivity";

    private List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    private Usuario user;
    private GoogleApiClient mGoogleApiClient;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        queue = Volley.newRequestQueue(this.getApplicationContext());

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);


        signInButton.setOnClickListener(this);

        //
//        goToMainPage(true);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String nome = acct.getDisplayName();
            String email = acct.getEmail();
            Uri foto_url = acct.getPhotoUrl();
            String id_user = acct.getId();

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(USER_NOME, nome);
            editor.putString(USER_EMAIL, email);
            editor.putString(USER_ID, id_user);

            if (foto_url != null) {
                editor.putString(USER_URL_PHOTO, foto_url.toString());
                user = new Usuario(id_user, nome, email, foto_url.toString());
            } else {
                editor.putString(USER_URL_PHOTO, null);
               user = new Usuario(id_user, nome, email, "");
            }
            DataSource dataSource = DataSource.getInstance(getApplicationContext());

            persistUserInformation(user);
            /*int atualizacoes = dataSource.atualizarUsuario(user);
            if(atualizacoes == 0) {
                dataSource.inserirUsuario(user);
            }*/
            editor.apply();
            goToMainPage(true);

        } else {
            goToMainPage(false);
            showEneableMessageIfNeeded();
        }
    }

    private void persistUserInformation(Usuario usuario) {
        DataSource dataSource = DataSource.getInstance(getApplicationContext());

        int atualizacoes = dataSource.atualizarUsuario(usuario);

        if(atualizacoes == 0) { //O usuário não existia previamente
            dataSource.inserirUsuario(usuario);
            refletirCadastro();

        } else { //O usuário já existia
            //Pega todas as informaçoes do usuario
        }
    }

    private void refletirCadastro() {
        final String URL_INFO_USUARIO = "http://lucasmatos.pythonanywhere.com/povmt/user/" + user.getId();
        final String URL_CRIAR_USUARIO = "http://lucasmatos.pythonanywhere.com/povmt/";

        final Response.Listener<JSONObject> usuarioRequestResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.has("data_created") && !response.isNull("data_created")){
                        String dataPersistido = response.getString("data_created");
                        DataSource.getInstance(getApplicationContext())
                                .setDataSincronizacaoUsuario(user.getId(), dataPersistido);

                        Log.d(TAG, "" + DataSource.getInstance(getApplicationContext())
                                .getDataSincronizacaoUsuario(user.getId()));
                    } else {
                        Log.w(TAG, "A resposta veio sem data");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao converter dados");
                }
            }
        };

        final Response.ErrorListener usuarioRequestErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        };

        final JsonObjectRequest getUsuarioRequest = new JsonObjectRequest(Request.Method.GET, URL_INFO_USUARIO, null,
                usuarioRequestResponseListener, usuarioRequestErrorListener);

        final Response.Listener<String> cadastroRequestResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                //Callback (?)
                queue.add(getUsuarioRequest);
            }
        };


        final StringRequest cadastroRequest = new StringRequest(Request.Method.POST, URL_CRIAR_USUARIO,
            cadastroRequestResponseListener, usuarioRequestErrorListener) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", user.getId());
                params.put("nome", user.getNome());
                params.put("email", user.getEmail());
                params.put("url_foto", user.getUrl());

                return params;
            }
        };

        queue.add(cadastroRequest);
    }

    private void handleVolleyError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Log.e(TAG, "Sem resposta!");
        } else if (error instanceof AuthFailureError) {
            Log.e(TAG, "Erro de autenticacao!");
        } else if (error instanceof ServerError) {
            Log.e(TAG, "Erro de servidor!");
        } else if (error instanceof NetworkError) {
            Log.e(TAG, "Erro de rede!");
        } else if (error instanceof ParseError) {
            Log.e(TAG, "Erro ao converter resposta!");
        } else {
            Log.e(TAG, "Erro desconhecido!");
        }
    }

    private void goToMainPage(boolean b) {
        if (b) {
            Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /** Method to show message internet if  is disabled
     */
    public void showEneableMessageIfNeeded() {
        if (!isNetworkAvailable()) {
            displayPromptForEnablingInternet();
        }
    }

    /** Method that checks if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /** Method to display Prompt for enabling internet
     */
    private void displayPromptForEnablingInternet() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(SplashActivity.this);
        final String actionWifiSettings = Settings.ACTION_WIFI_SETTINGS;
        final String actionWirelessSettings = Settings.ACTION_WIRELESS_SETTINGS;
        final String message = getString(R.string.enable_network);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.bt_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                SplashActivity.this.startActivity(new Intent(actionWifiSettings));
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.bt_mobile_network),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                SplashActivity.this.startActivity(new Intent(actionWirelessSettings));
                                dialog.dismiss();
                            }
                        })
                .setNeutralButton(getString(R.string.bt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idButton) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();
    }
}
