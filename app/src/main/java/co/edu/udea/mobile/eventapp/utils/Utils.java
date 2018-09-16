package co.edu.udea.mobile.eventapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AW 13
 */

public class Utils {

    public static Date getDateFromString(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateFormated = formatter.parse(date);
            return dateFormated;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
}
