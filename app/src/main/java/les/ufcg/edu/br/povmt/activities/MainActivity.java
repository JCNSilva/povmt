package les.ufcg.edu.br.povmt.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import les.ufcg.edu.br.povmt.R;
import les.ufcg.edu.br.povmt.database.DataSource;
import les.ufcg.edu.br.povmt.fragments.AboutFragment;
import les.ufcg.edu.br.povmt.fragments.ConfigurationsFragment;
import les.ufcg.edu.br.povmt.fragments.HistoryFragment;
import les.ufcg.edu.br.povmt.fragments.HomeFragment;
import les.ufcg.edu.br.povmt.fragments.RegisterTIFragment;
import les.ufcg.edu.br.povmt.models.Atividade;
import les.ufcg.edu.br.povmt.models.Categoria;
import les.ufcg.edu.br.povmt.models.Prioridade;
import les.ufcg.edu.br.povmt.models.TI;
import les.ufcg.edu.br.povmt.utils.CircleTransform;
import les.ufcg.edu.br.povmt.utils.IonResume;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, IonResume {

    public static final String HOME_TAG = "HOME_TAG";
    private static final String HISTORY_TAG = "HISTORY_TAG";
    private static final String ABOUT_TAG = "ABOUT_TAG";
    private static final String CONFIG_TAG = "CONFIG_TAG";
    public static final String ACTION = "com.example.android.receivers.NOTIFICATION_ALARM";
    private SharedPreferences sharedPreferences;
    private TextView nameUsr;
    private ImageView imgUsr;
    private TextView emailUsr;
    private GoogleApiClient mGoogleApiClient;
    private FragmentManager fragmentManager;
    private int lastFragment;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private AboutFragment aboutFragment;
    private ConfigurationsFragment configFragment;
    private FloatingActionButton fab;
    private DataSource dataSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataSource = DataSource.getInstance(getApplicationContext());
        sharedPreferences = getSharedPreferences(SplashActivity.PREFERENCE_NAME, MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setUpFragments();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                RegisterTIFragment cdf = new RegisterTIFragment(homeFragment);
                cdf.show(ft, "dialog");
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                CreateTIFragment cdf = new CreateTIFragment();
//                cdf.show(ft, "dialog");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerLayout = navigationView.getHeaderView(0);

        nameUsr = (TextView) headerLayout.findViewById(R.id.nameUsr);
        emailUsr = (TextView) headerLayout.findViewById(R.id.emailUsr);
        imgUsr = (ImageView) headerLayout.findViewById(R.id.imgUsr);

        setUpViewsDrawer();
//        setUpFragments();
    }


    /** Method to create all fragments
     */
    private void setUpFragments(){
        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        configFragment = new ConfigurationsFragment(this);
        aboutFragment = new AboutFragment();

        currentFragment = homeFragment;

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment, HOME_TAG);
        fragmentTransaction.commit();

//        homeFragment.atualizaLista();
    }

    private void setUpViewsDrawer(){
        String nome = sharedPreferences.getString(SplashActivity.USER_NOME, "");
        String email = sharedPreferences.getString(SplashActivity.USER_EMAIL, "");
        String urlImage = sharedPreferences.getString(SplashActivity.USER_URL_PHOTO, "");

        nameUsr.setText(nome);
        emailUsr.setText(email);
        nameUsr.setVisibility(View.VISIBLE);

        if(!urlImage.equals("")){
            Picasso.with(this).load(urlImage).transform(new CircleTransform()).into(imgUsr);
        } else {
            Picasso.with(this).load(R.drawable.default_user).transform(new CircleTransform()).into(imgUsr);
        }
    }

    private void hideIcons() {
        if (currentFragment != homeFragment) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
   }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == 0) {
            id = lastFragment;
        }

        switch (id) {
            case R.id.nav_week:
                getSupportActionBar().setTitle(getString(R.string.app_name));
                if (fragmentManager.findFragmentByTag(HOME_TAG) == null) {
                    fragmentTransaction.hide(currentFragment);
                    fragmentTransaction.add(R.id.fragment_container, homeFragment, HOME_TAG);
                    fragmentTransaction.show(homeFragment).commit();
                } else if (!fragmentManager.findFragmentByTag(HOME_TAG).isVisible()) {
                    fragmentTransaction.hide(currentFragment).show(homeFragment).commit();
                }
                currentFragment = homeFragment;
                lastFragment = R.id.nav_week;
                break;
            case R.id.nav_history:
                getSupportActionBar().setTitle(getString(R.string.historico));
                if (fragmentManager.findFragmentByTag(HISTORY_TAG) == null) {
                    fragmentTransaction.hide(currentFragment);
                    fragmentTransaction.add(R.id.fragment_container, historyFragment, HISTORY_TAG);
                    fragmentTransaction.show(historyFragment).commit();
                } else if (!fragmentManager.findFragmentByTag(HISTORY_TAG).isVisible()) {
                    fragmentTransaction.hide(currentFragment).show(historyFragment).commit();
                }
                currentFragment = historyFragment;
                lastFragment = R.id.nav_history;
                break;
            case R.id.nav_config:
                getSupportActionBar().setTitle(getString(R.string.config));
                if (fragmentManager.findFragmentByTag(CONFIG_TAG) == null) {
                    fragmentTransaction.hide(currentFragment);
                    fragmentTransaction.add(R.id.fragment_container, configFragment, CONFIG_TAG);
                    fragmentTransaction.show(configFragment).commit();
                } else if (!fragmentManager.findFragmentByTag(CONFIG_TAG).isVisible()) {
                    fragmentTransaction.hide(currentFragment).show(configFragment).commit();
                }
                currentFragment = configFragment;
                lastFragment = R.id.nav_config;
                break;
            case R.id.nav_about:
                getSupportActionBar().setTitle(getString(R.string.sobre));
                if (fragmentManager.findFragmentByTag(ABOUT_TAG) == null) {
                    fragmentTransaction.hide(currentFragment);
                    fragmentTransaction.add(R.id.fragment_container, aboutFragment, ABOUT_TAG);
                    fragmentTransaction.show(aboutFragment).commit();
                } else if (!fragmentManager.findFragmentByTag(ABOUT_TAG).isVisible()) {
                    fragmentTransaction.hide(currentFragment).show(aboutFragment).commit();
                }
                currentFragment = aboutFragment;
                lastFragment = R.id.nav_about;
                break;
            case R.id.logout:
                signOut();

                Intent mainIntent = new Intent(MainActivity.this,SplashActivity.class);
                startActivity(mainIntent);
                finish();
            default:
                break;

        }

        hideIcons();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        sharedPreferences = getSharedPreferences(SplashActivity.PREFERENCE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(SplashActivity.USER_NOME, "");
                        editor.putString(SplashActivity.USER_URL_PHOTO, "");
                        editor.putString(SplashActivity.USER_EMAIL, "");

                        editor.apply();
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void notificar(int hora, int minuto) {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        calSet.setTimeInMillis(System.currentTimeMillis());
        calSet.set(Calendar.HOUR_OF_DAY, hora);
        calSet.set(Calendar.MINUTE, minuto);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        setAlarme(calSet);
    }

    private void setAlarme(Calendar calendar) {
        Calendar calNow = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        if (time <= calNow.getTimeInMillis()) {
            time = time + (AlarmManager.INTERVAL_DAY+1);
        }

        Intent intent = new Intent(ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelAlarm(){
        Log.d("Script", "Alarme cancelado.");
        Intent intent = new Intent(ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void refresh() {
        if (configFragment.isNotificacaoAtiva()) {
            Log.d("Script", "Notificação está ativada.");

            notificar(configFragment.getHoraNotificacao(), configFragment.getMinutoNotificacao());
        }
    }

}
