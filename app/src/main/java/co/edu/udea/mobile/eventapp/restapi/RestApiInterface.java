package co.edu.udea.mobile.eventapp.restapi;

import java.util.List;

import co.edu.udea.mobile.eventapp.dto.Attendance;
import co.edu.udea.mobile.eventapp.dto.AttendanceShow;
import co.edu.udea.mobile.eventapp.dto.Event;
import co.edu.udea.mobile.eventapp.dto.EventSession;
import co.edu.udea.mobile.eventapp.dto.UserApp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApiInterface {

    @GET("users")
    Call<List<UserApp>> login(@Query("user") String user, @Query("passw") String password);
    @GET("events/")
    Call<List<Event>> getEvents(@Query("owner") int owner);
    @GET("sessions/")
    Call<List<EventSession>> getSessions(@Query("event") int event);
    @Headers({"Accept: application/json",})
    @POST("events/")
    Call<Event> saveEvent(@Body Event event);
    @Headers({"Accept: application/json",})
    @POST("sessions/")
    Call<EventSession> saveSession(@Body EventSession session);
    @Headers({"Accept: application/json",})
    @POST("attendance/")
    Call<Attendance> saveAttendance(@Body Attendance assist);
    @GET("getAttendanceByUser")
    Call<List<AttendanceShow>> getAssistByUser(@Query("usr") int attendant);
    @GET("getAttendanceByUser")
    Call<List<AttendanceShow>> getAssistBySession(@Query("session") String session);
}