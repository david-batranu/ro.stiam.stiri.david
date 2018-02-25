package ro.stiam.stiri.david;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FormatRoDate {
    public static String format(String json_date) throws ParseException {
        Date date = parse(json_date);
        Date now = new Date();

        int delta = (int) (now.getTime() - date.getTime()) / 1000;
        if (delta < 0){
            return "acum cateva secunde";
        }
        else if (delta < 120){
            return "acum un minut";
        }
        else if (delta < 3300){
            int min = delta / 60;
            return "acum " + min + " minute";
        }
        else if (delta < 7200){
            return "acum o ora";
        }
        else if (delta < 72000){
            int hours = delta / 3600;
            return "acum " + hours + " ore";
        }

        delta = delta / 3600;
        if (delta < 48){
            return "ieri";
        }
        else if (delta < 160){
            int days = delta / 24;
            return "acum " + days + " zile";
        }
        else if (delta < 336){
            return "saptamana trecuta";
        }
        else if (delta < 720){
            int weeks = delta / 24 / 7;
            return "acum " + weeks + " saptamani";
        }

        delta = delta / 24;
        if (delta < 60){
            return "acum o luna";
        }
        else if (delta < 360){
            int months = delta / 30;
            return "acum " + months + " luni";
        }
        else if (delta < 720){
            return "acum un an";
        }
        else if (delta > 720){
            int years = delta / 360;
            return "acum " + years + " ani";
        }
        return "invalid date";
    }

    public static Date parse( String input ) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        //this is zero time so we need to add that TZ indicator for
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }

        return df.parse( input );

    }

    public static String toString( Date date ) {

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        TimeZone tz = TimeZone.getTimeZone( "UTC" );

        df.setTimeZone( tz );

        String output = df.format( date );

        int inset0 = 9;
        int inset1 = 6;

        String s0 = output.substring( 0, output.length() - inset0 );
        String s1 = output.substring( output.length() - inset1, output.length() );

        String result = s0 + s1;

        result = result.replaceAll( "UTC", "+00:00" );

        return result;

    }
}
