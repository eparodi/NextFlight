package com.example.martin.nextflight.managers;

import android.content.Context;

import com.example.martin.nextflight.MainActivity;
import com.example.martin.nextflight.elements.Flight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Martin on 27/11/2016.
 */

public class SettingsManager {

    private static String currency = null;
    public static final String CURRENCY_FILE = "currency";
    public static Context settings_context;

    public static void startSettingsManager(Context context) {
        if (currency == null){
            FileInputStream in = null;
            try {
                in = context.openFileInput(CURRENCY_FILE);
                ObjectInputStream objin = new ObjectInputStream(in);
                currency = (String) objin.readObject();
                if (currency == null || currency.equals("")) {
                    setCurrency("Dólares", context);
                }
                settings_context = context;
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }finally{
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void setCurrency(String new_currency, Context context){
        currency = new_currency;

        FileOutputStream out = null;

        try {
            out = context.openFileOutput(CURRENCY_FILE,Context.MODE_PRIVATE);
            ObjectOutputStream objout = new ObjectOutputStream(out);
            objout.writeObject(currency);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getCurrency() {
        return currency;
    }

}
