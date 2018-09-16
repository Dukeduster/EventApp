package co.edu.udea.mobile.eventapp.restapi;


import co.edu.udea.mobile.eventapp.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementacion de Cliente Rest de la Interfaz RestApiInterface
 */

public class RestClientImpl  {
    private static RestApiInterface restApiInterface;

    public static RestApiInterface getClientLogin() {
        String API_URL = BuildConfig.IPAPP;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        restApiInterface = retrofit.create(RestApiInterface.class);
        return restApiInterface;
    }


}
