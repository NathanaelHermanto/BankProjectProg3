package FormatierteAusgabe;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class FormatierteAusgabe {
    public static void main(String[] args) throws IOException {
        File file = new File("output.txt");
        FileOutputStream out = new FileOutputStream(file);
        FileInputStream in = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        //1
        String s = Integer.parseInt(reader.readLine()) + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //2
        s = String.format("%010d", Integer.parseInt(reader.readLine())) + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //3
        int i = Integer.parseInt(reader.readLine());
        Formatter formatter = new Formatter();
        writer.write(formatter.format(Locale.US, "%+,d", i) + System.lineSeparator());
        writer.flush();

        //4
        float f = Float.parseFloat(reader.readLine());
        formatter = new Formatter();
        writer.write(formatter.format(Locale.US, "%,f", f) + System.lineSeparator());
        writer.flush();

        //5
        f = Float.parseFloat(reader.readLine());
        formatter = new Formatter();
        writer.write(String.valueOf(formatter.format(Locale.US, "%+.2f", f)) + System.lineSeparator());
        writer.flush();

        //6
        f = Float.parseFloat(reader.readLine());
        s = String.format("%6.3e", f) + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //7
        f = Float.parseFloat(reader.readLine());
        formatter = new Formatter();
        writer.write(String.valueOf(formatter.format(Locale.US, "%.3f", f)) + System.lineSeparator());
        writer.flush();

        //8
        f = Float.parseFloat(reader.readLine());
        s = String.format("%f", f * 100)+ "%" + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //9
        Date heute = new Date();
        s = new SimpleDateFormat("EEEE-dd-MMM-yyyy").format(heute) + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //10
        s = new SimpleDateFormat("EEEE-dd-MMM-yyyy", Locale.ITALY).format(heute) + System.lineSeparator();
        writer.write(s);
        writer.flush();

        //11
        LocalTime jetzt = LocalTime.now();
        s = String.format("%tr", jetzt) + System.lineSeparator();
        writer.write(s);
        writer.flush();
    }
}
