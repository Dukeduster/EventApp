package co.edu.udea.mobile.eventapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import co.edu.udea.mobile.eventapp.adapters.AttendanceAdapter;
import co.edu.udea.mobile.eventapp.adapters.EventAdapter;
import co.edu.udea.mobile.eventapp.adapters.SessionAdapter;
import co.edu.udea.mobile.eventapp.dto.Attendance;
import co.edu.udea.mobile.eventapp.dto.AttendanceShow;
import co.edu.udea.mobile.eventapp.dto.Event;
import co.edu.udea.mobile.eventapp.dto.EventSession;
import co.edu.udea.mobile.eventapp.dto.UserApp;
import co.edu.udea.mobile.eventapp.restapi.RestClientImpl;
import co.edu.udea.mobile.eventapp.utils.NetworkUtilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    RecyclerView recyclerView;
    LatLng myPos;
    private String currentView = "EVENTS";
    private int idEvent = 0;
    private String idSession;
    //Receiver que se encarga de mostrar las vistas asociadas a las operaciones de usuario desde otras actividades
    BroadcastReceiver rec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("LOADSESSIONS".equals(intent.getAction())) {
                int event = intent.getIntExtra("event", 0);
                idEvent = event;
                loadSessions(event);
                currentView = "SESSIONS";
                getSupportActionBar().setTitle("Sesiones");
            } else if ("LOADEVENTS".equals(intent.getAction())) {
                loadEvents();
                currentView = "SESSIONS";
            } else if ("LOADASSIST".equals(intent.getAction())) {
                idSession = intent.getStringExtra("session");
                loadAssistanceBySession(idSession);
            }

        }
    };
    private int MY_PERMISSIONS_REQUEST_GPS;
    private int MY_PERMISSIONS_REQUEST_GPS_COARSE;
    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("SESSIONS".equals(currentView)) {
                    Intent i = new Intent(MainActivity.this, AddSessionActivity.class);
                    i.putExtra("idEvent", idEvent);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                    startActivity(i);
                }

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.scrollView);
        //Registros de los intents para los receivers
        IntentFilter filterS = new IntentFilter("LOADSESSIONS");
        IntentFilter filterE = new IntentFilter("LOADEVENTS");
        IntentFilter filterA = new IntentFilter("LOADASSIST");
        this.registerReceiver(rec, filterS);
        this.registerReceiver(rec, filterE);
        this.registerReceiver(rec, filterA);
        loadEvents();
        askPermissions();
    }

    /**
     * Muestra las vista anterior  según la vista actual
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentView.equals("SESSIONS")) {
            loadEvents();
        } else if (currentView.equals("ASSISTBYSESSION")) {
            loadSessions(idEvent);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Menu
        //Registro de asistencia invoca la actividad para captura de codigo QR
        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, false);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        } else if (id == R.id.nav_gallery) {
            loadAssistanceByUser();
        } else if (id == R.id.nav_slideshow) {
            loadEvents();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Consume Api rest para el recurso Eventos
     */
    private void loadEvents() {
        getSupportActionBar().setTitle("Mis Eventos");
        currentView = "EVENTS";
        UserApp user = getUserFromPrefs();
        Log.e("client route", user.getName());
        Call<List<Event>> call = RestClientImpl.getClientLogin()
                .getEvents(user.getId());
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.e("code", "" + response.code());
                List<Event> events = response.body();
                if (events != null) {
                    showEvents(events);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setVisibility(View.INVISIBLE);
                } else {
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                if (t.getMessage().startsWith("java.lang.IllegalStateException")) {
                    Log.e("Error", t.getMessage());
                }
            }
        });
    }

    /**
     * Carga las sesiones de un Evento dado
     *
     * @param idEvent id del evento
     */
    private void loadSessions(int idEvent) {
        getSupportActionBar().setTitle("Sesiones");
        currentView = "SESSIONS";
        Log.e("code", "" + idEvent);
        Call<List<EventSession>> call = RestClientImpl.getClientLogin()
                .getSessions(idEvent);
        call.enqueue(new Callback<List<EventSession>>() {
            @Override
            public void onResponse(Call<List<EventSession>> call, Response<List<EventSession>> response) {
                Log.e("code", "" + response.code());
                List<EventSession> sessions = response.body();
                Log.e("body", "" + response.body());

                if (sessions != null) {
                    Log.e("sessions size", "" + sessions.size());
                    showSessions(sessions);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setVisibility(View.INVISIBLE);
                } else {
                    Log.e("no hay nada", "" + response.code());
                    sessions = new LinkedList<>();
                    showSessions(sessions);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setText("No hay sessiones");
                    txtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<EventSession>> call, Throwable t) {
                if (t.getMessage().startsWith("java.lang.IllegalStateException")) {
                    Log.e("Error", t.getMessage());
                }
            }
        });

    }

    /**
     * Carga la asistenca de una sesion del curso
     *
     * @param idSession id de sesion
     */

    private void loadAssistanceBySession(String idSession) {
        currentView = "ASSISTBYSESSION";
        getSupportActionBar().setTitle("Asistencias");
        Call<List<AttendanceShow>> call = RestClientImpl.getClientLogin()
                .getAssistBySession(idSession);
        call.enqueue(new Callback<List<AttendanceShow>>() {
            @Override
            public void onResponse(Call<List<AttendanceShow>> call, Response<List<AttendanceShow>> response) {
                Log.e("code", "" + response.code());
                List<AttendanceShow> asistencias = response.body();
                if (asistencias != null) {
                    Log.e("numero de asistencias", "" + asistencias.size());
                    showAssistance(asistencias);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setVisibility(View.INVISIBLE);
                } else {
                    asistencias = new LinkedList<>();
                    showAssistance(asistencias);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setText("No hay asistencias");
                    txtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceShow>> call, Throwable t) {
                if (t.getMessage().startsWith("java.lang.IllegalStateException")) {
                    Log.e("Error", t.getMessage());
                }
            }
        });

    }

    /**
     * Carga la asistenca de una sesion por usuario
     */
    private void loadAssistanceByUser() {
        currentView = "ASSISTBYUSER";
        getSupportActionBar().setTitle("Asistencias");
        UserApp user = getUserFromPrefs();
        Call<List<AttendanceShow>> call = RestClientImpl.getClientLogin()
                .getAssistByUser(user.getId());
        call.enqueue(new Callback<List<AttendanceShow>>() {
            @Override
            public void onResponse(Call<List<AttendanceShow>> call, Response<List<AttendanceShow>> response) {
                Log.e("code", "" + response.code());
                List<AttendanceShow> asistencias = response.body();
                if (asistencias != null) {
                    showAssistance(asistencias);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setVisibility(View.INVISIBLE);
                } else {
                    asistencias = new LinkedList<>();
                    showAssistance(asistencias);
                    TextView txtView = (TextView) findViewById(R.id.no_courses);
                    txtView.setText("No hay asistencias");
                    txtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceShow>> call, Throwable t) {
                if (t.getMessage().startsWith("java.lang.IllegalStateException")) {
                    Log.e("Error", t.getMessage());
                }
            }
        });

    }

    /**
     * Carga el adaptador para cars de Asistencia
     *
     * @param attendances lista de objetos Asistencia
     */
    private void showAssistance(List<AttendanceShow> attendances) {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        RecyclerView.Adapter adapter = new AttendanceAdapter(attendances, this);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Carga el adaptador para cars de Eventos
     *
     * @param events lista de objetos Eventos
     */
    private void showEvents(List<Event> events) {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        RecyclerView.Adapter adapter = new EventAdapter(events, this);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Carga el adaptador para cards de Sesiones
     *
     * @param sessions lista de objetos Sesiones
     */

    private void showSessions(List<EventSession> sessions) {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        RecyclerView.Adapter adapter = new SessionAdapter(sessions, this);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Carga los datos del usuario logueado
     *
     * @return
     */
    private UserApp getUserFromPrefs() {
        SharedPreferences prefs =
                getSharedPreferences("EventPrefs", Context.MODE_PRIVATE);
        UserApp user = new UserApp();
        user.setId(prefs.getInt("id", 0));
        user.setName(prefs.getString("name", ""));
        user.setLastname(prefs.getString("lastname", ""));
        user.setPassword(prefs.getString("password", ""));
        user.setUsername(prefs.getString("username", ""));
        user.setEmail(prefs.getString("email", ""));
        return user;

    }


    @Override
    public void onDestroy() {
        unregisterReceiver(rec);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    //Captura de codigo QR
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.e("Barcode read: ", barcode.displayValue);
                    //Reporte  de asistencia
                    saveAssist(barcode.displayValue);
                } else {
                    Log.e("Error Barcode", "No barcode captured intent data is null");
                }
            } else {

                Log.e("Error barcode", "" + resultCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Guarda la asistencia
     *
     * @param barcodeParam Codigo QR ya leido
     */
    public void saveAssist(String barcodeParam) {
        UserApp user = getUserFromPrefs();
        String barcode = barcodeParam.trim();
        final Attendance assist = new Attendance();
        assist.setSession(barcode);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(new Date());
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        String hour = dateFormat.format(new Date());
        assist.setAttendanceDate(day + "T" + hour);
        assist.setAttendant(user.getId());
        updatePosition();

        Double lat = BigDecimal.valueOf(myPos.latitude)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
        Double lon = BigDecimal.valueOf(myPos.longitude)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
        if (myPos != null) {
            assist.setLatitude(lat);
            assist.setLongitude(lon);
        } else {
            assist.setLatitude(0D);
            assist.setLongitude(0D);
        }
        if (NetworkUtilities.isConnected(this)) {
            Call<Attendance> call = RestClientImpl.getClientLogin().saveAttendance(assist);
            call.enqueue(new Callback<Attendance>() {
                @Override
                public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                    Log.e("response body", String.valueOf(response.body()));
                    Attendance assistR = response.body();
                    if (response.code() == 201) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Asistencia Reportada ", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Ha ocurrido un Error  ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<Attendance> call, Throwable t) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Ha ocurrido un Error  ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No Hay conexion a internet ", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /**
     * Pregunta por permisos al usuario
     */
    public void askPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS_COARSE
            );
        }

    }

    /**
     * Actualiza la variable posicion segun los valores arrojados por el GPS del dispositivo y los permisos
     */
    public void updatePosition() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS_COARSE
            );
        }
        if (MY_PERMISSIONS_REQUEST_GPS == PackageManager.PERMISSION_GRANTED || MY_PERMISSIONS_REQUEST_GPS_COARSE
                == PackageManager.PERMISSION_GRANTED) {
            this.permissionsGranted = true;
            Log.e("position", "Granted");
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                myPos = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e("inc mypos lat", myPos.latitude + "");
                Log.e("inc mypos long", myPos.longitude + "");

            }
        }
    }
}


