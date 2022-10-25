import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AlphaVantageDemo {
  public static void main(String []args) throws ParseException {
    //the API key needed to use this web service.
    //Please get your own free API key here: https://www.alphavantage.co/
    //Please look at documentation here: https://www.alphavantage.co/documentation/
    String apiKey = "W0M1JOKC82EZEQA8";
    String stockSymbol = "GOOG"; //ticker symbol for Google
    URL url = null;

    try {
      /*
      create the URL. This is the query to the web service. The query string
      includes the type of query (DAILY stock prices), stock symbol to be
      looked up, the API key and the format of the returned
      data (comma-separated values:csv). This service also supports JSON
      which you are welcome to use.
       */
      url = new URL("https://www.alphavantage"
        + ".co/query?function=TIME_SERIES_DAILY"
        + "&outputsize=full"
        + "&symbol"
        + "=" + stockSymbol + "&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or "
        + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();
    Map<Date, Double> dateSet = new HashMap();

    try {
      /*
      Execute this query. This returns an InputStream object.
      In the csv format, it returns several lines, each line being separated
      by commas. Each line contains the date, price at opening time, highest
      price for that date, lowest price for that date, price at closing time
      and the volume of trade (no. of shares bought/sold) on that date.

      This is printed below.
       */
      in = url.openStream();
      int b;

      Scanner sc = new Scanner(in).useDelimiter("\n");

      sc.next();
      while (sc.hasNext()) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
        String result = sc.next();
        String[] arr = result.split(",");

        try {
          dateSet.put(formatter.parse(arr[0]), Double.valueOf(arr[4]));
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + stockSymbol);
    }
    SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);

    try {
      System.out.println(dateSet.get(formatter.parse("2022-10-21")));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
//    System.out.println("Return value: ");
//    System.out.println(output.toString());

//    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
//    Date firstDate = sdf.parse("12/31/2017");
//    Date secondDate = sdf.parse("01/11/2019");
//
//    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
//    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
//    System.out.println(diff);
  }
}
