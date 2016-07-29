package les.ufcg.edu.br.povmt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
import java.util.List;

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
    private static final String URL_CRIAR_USUARIO = "http://lucasmatos.pythonanywhere.com/povmt/";

    private List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    private Usuario user;
    private GoogleApiClient mGoogleApiClient;

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
            int atualizacoes = dataSource.atualizarUsuario(user);
            if(atualizacoes == 0) {
                dataSource.inserirUsuario(user);
            }
            editor.apply();
            goToMainPage(true);

        } else {
            goToMainPage(false);
        }
    }

    private void persistUserInformation(Usuario usuario) {
        DataSource dataSource = DataSource.getInstance(getApplicationContext());

        int atualizacoes = dataSource.atualizarUsuario(usuario);
        if(atualizacoes == 0) {
            dataSource.inserirUsuario(usuario);
        }

        try {
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("id", user.getId());
            jsonObject.accumulate("nome", user.getNome());
            jsonObject.accumulate("email", user.getEmail());
            jsonObject.accumulate("url_foto", user.getUrl());
            json = jsonObject.toString();

            new HttpPostAsyncTask().execute(URL_CRIAR_USUARIO, json);}
        catch(Exception e) {
            Log.d("JsonObject", e.getLocalizedMessage());
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

}
