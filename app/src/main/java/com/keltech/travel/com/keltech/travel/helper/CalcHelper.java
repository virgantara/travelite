package com.keltech.travel.com.keltech.travel.helper;

import com.keltech.travel.modules.Constants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by ASUS on 8/28/2016.
 */
public final class CalcHelper {

    public static String formatWaktu(int detik){
        return String.format("%d jam, %02d min",
                TimeUnit.SECONDS.toHours(detik),
                TimeUnit.SECONDS.toMinutes(detik) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(detik))
        );


    }

    public static String formatAngka(double angka){
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(sym);

        return df.format(angka);
    }

    public static String getFormattedHargaTotal(int tipe, int jarak, int dur) {
        double distKm = ((double) jarak) / 1000;
        double durasi = ((double) dur) / 60;

        int harga10km = 0;
        int hargePerKm = 0;
        switch (tipe) {
            case Constants.FROM_BIKE:
                harga10km = 10000;
                hargePerKm = 1500;
                break;
            case Constants.FROM_TAXI:
                harga10km = 25000;
                hargePerKm = 3000;
                break;
        }

        double dtDist = 10 - distKm;

        double hargaTotal = 0.0;
        if (dtDist > 0.0) {
            hargaTotal = harga10km;
        } else {
            hargaTotal = harga10km + (Math.abs(dtDist) * hargePerKm);
        }

        String str = String.format("%,d", Math.round(hargaTotal)).replace(',','.');

        return "Rp "+str;
    }
}
