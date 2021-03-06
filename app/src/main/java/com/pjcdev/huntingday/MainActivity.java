package com.pjcdev.huntingday;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private View headerview;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView ivProfile;
    private TextView tvName,tvCity;
    private Fragment fragment=null;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment=new NewActivityFragment();
                String title="Nueva Jornada de Caza";
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.principalArea,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //change title
                getSupportActionBar().setTitle(title);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //create a headerview to conect to header of left menu
        headerview=navigationView.getHeaderView(0);

        //hide navigation bar
        View decorview = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            decorview.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }



        ivProfile=(ImageView)headerview.findViewById(R.id.imageProfile);
        tvName=(TextView)headerview.findViewById(R.id.fullName);
        tvCity=(TextView)headerview.findViewById(R.id.City);

        auth=FirebaseAuth.getInstance();

        //get user profile
        user=FirebaseAuth.getInstance().getCurrentUser();
        //get user profile

        if (user != null) {

            // Name, date birth,adress,email, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            Glide.with(this).load(photoUrl).into(ivProfile);
            tvName.setText(name);
        }

        //handle gso objet to logout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();




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

        //hide check button from toolbar
        MenuItem item= menu.findItem(R.id.check);
        item.setVisible(false);
        this.invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.check) {
            return true;
        }else
        {
            if (id==android.R.id.home){
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title="";

        if (id == R.id.addDogs) {
            fragment=new DogFragment();
            title="Mis Perros";
        } else if (id == R.id.addWeapons) {
            fragment=new WeaponFragment();
            title="Mis Armas";
        } else if (id == R.id.newActivity) {
            fragment=new NewActivityFragment();
            title="Nueva Jornada de Caza";
        } else if (id == R.id.History) {
            fragment=new HistoryFragment();
            title="Mis Actividades";
        } else if (id == R.id.editProfile) {
            fragment=new EditProfileFragment();
            title="Mi Perfil";
        } else if (id == R.id.about) {
            fragment=new AboutFragment();
            title="Acerca de";
        }else if (id == R.id.Logout) {
            signOut();
            goLoginScreen();
        }

        //create the fragment
        if(fragment!=null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.principalArea,fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //change title
            getSupportActionBar().setTitle(title);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser==null) {
            goLoginScreen();
        }
    }

    //method to go LoginScreen
    private void goLoginScreen() {
        Intent intent=new Intent(this,LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //method to log out Google,Facebook & Firebase
    private void signOut() {
        //signout google
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {

                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d("LOGOUT", "Logout correcto");
                            goLoginScreen();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al hacer logout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "no estas conectado", Toast.LENGTH_SHORT).show();
            }
        }
        //signout facebook
        LoginManager.getInstance().logOut();
        //signout firebase
        auth.signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //method to hide floatingActionButton
    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

}
