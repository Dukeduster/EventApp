package co.edu.udea.mobile.eventapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import co.edu.udea.mobile.eventapp.dto.Event;
import co.edu.udea.mobile.eventapp.dto.EventSession;
import co.edu.udea.mobile.eventapp.dto.UserApp;
import co.edu.udea.mobile.eventapp.restapi.RestClientImpl;
import co.edu.udea.mobile.eventapp.utils.DatePickerFragment;
import co.edu.udea.mobile.eventapp.utils.NetworkUtilities;
import co.edu.udea.mobile.eventapp.utils.TimePickerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.fabric.sdk.android.services.common.SafeToast;

public class AddSessionActivity extends AppCompatActivity {


    private View mProgressView;
    private View mLoginFormView;
    private EditText mDateSession;
    private EditText mTimeSession;
    private EditText mCodeSession;
    private AutoCompleteTextView mDescription;
    private AutoCompleteTextView mNameSession;
    private String dateFormatted;
    private String timeFormatted;
    private int eventId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_session);
        setSupportActionBar(toolbar);
        setupActionBar();
        eventId = getIntent().getIntExtra("idEvent", 0);

        // Set up the login form.
        // mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // populateAutoComplete();
        mDateSession = (EditText) findViewById(R.id.date_expire);
        mDateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        mTimeSession = (EditText) findViewById(R.id.time_expire);
        mTimeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        mLoginFormView = findViewById(R.id.session_form);
        mProgressView = findViewById(R.id.session_progress);
        mNameSession = (AutoCompleteTextView) findViewById(R.id.name);
        mDescription = (AutoCompleteTextView) findViewById(R.id.description);
        mCodeSession = (EditText) findViewById(R.id.code_session);

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = year + "-" + String.format("%02d", (month+1)) + "-" + String.format("%02d", day);
                mDateSession.setText(selectedDate);
                dateFormatted=year+""+String.format("%02d", (month+1))+""+String.format("%02d", day);
            }
        });
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                // +1 because january is zero

                final String selectedDate = String.format("%02d", hour) + ":" +String.format("%02d", minutes);
                mTimeSession.setText(selectedDate);
                timeFormatted=String.format("%02d", hour)+""+String.format("%02d", minutes);
            }
        });
        newFragment.show(this.getFragmentManager(), "datePicker");
    }



    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**


     private boolean isEmailValid(String email) {
     //TODO: Replace this with your own logic
     return email.contains("@");
     }

     private boolean isPasswordValid(String password) {
     //TODO: Replace this with your own logic
     return password.length() > 4;
     }

     /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("menu", "before case");
        Log.e("menu", ""+item.getItemId());
        switch (item.getItemId()) {

            case R.id.save_session:
                Log.e("menu", "after case");
                saveSession();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveSession() {
        UserApp user = getUserFromPrefs();
        Log.e("account user", user.toString());
        Log.e("network", String.valueOf(NetworkUtilities.isConnected(this)));

        if(NetworkUtilities.isConnected(this)){
            showProgress(true);
            EventSession session= new EventSession();
            session.setDescription(this.mDescription.getText().toString());
            session.setDateSession(this.mDateSession.getText().toString()+"T"+this.mTimeSession.getText()+":00");
            session.setId(dateFormatted+timeFormatted+eventId+mCodeSession.getText().toString());
            session.setName(this.mNameSession.getText().toString().toUpperCase());
            session.setEvent(eventId);
            Call<EventSession> call = RestClientImpl.getClientLogin().saveSession(session);
            call.enqueue(new Callback<EventSession>() {
                @Override
                public void onResponse(Call<EventSession> call, Response<EventSession> response) {
                    EventSession session = response.body();
                    if(response.code()==201){
                        Toast toast = SafeToast.makeText(getApplicationContext(),"Sesión " +
                                session.getName() + "Guardada.", SafeToast.LENGTH_SHORT );
                        toast.show();
                        saveSuccess();
                    }else{
                        Log.e("Response code", ""+response.code());
                        Log.e("Response code", ""+response.body());
                        Snackbar snackbar = Snackbar
                                .make(mLoginFormView, "Ha ocurrido un error" , Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<EventSession> call, Throwable t) {
                    Log.e("Error Duke", t.getMessage());
                    Snackbar snackbar = Snackbar
                            .make(mLoginFormView, "Ha ocurrido un error" , Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }else{
            Snackbar snackbar = Snackbar
                    .make(mLoginFormView, "Sin Conexion a internet" , Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

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



    public void saveSuccess(){
        Intent in = new Intent("LOADSESSIONS");
        in.putExtra("event",eventId);
        sendBroadcast(in);
        this.finish();
    }

    @Override
    public void onDestroy(){
        Intent in = new Intent("LOADSESSIONS");
        in.putExtra("event",eventId);
        sendBroadcast(in);
        super.onDestroy();

    }
}
