package com.example.martin.nextflight.managers;

import android.content.Context;
import android.util.Log;

import com.example.martin.nextflight.elements.Flight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileManager {

    private static ArrayList<Flight> flightsList = null;
    public static final String FLIGHT_FILE = "myFlights";

    public static void startFileManager(Context context) {
        if (flightsList == null){
            flightsList = new ArrayList<>();
            FileInputStream in = null;
            try {
                in = context.openFileInput(FLIGHT_FILE);
                ObjectInputStream objin = new ObjectInputStream(in);
                flightsList = (ArrayList<Flight>) objin.readObject();
                Log.v("Hello",flightsList.size() + "");
            }catch(FileNotFoundException e){
                Log.v("Hello","hello");
                e.printStackTrace();
            }catch(IOException e){
                Log.v("Hello","hello");
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                Log.v("Hello","hello");
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

    public static boolean checkFlight(Flight f){
        return flightsList.contains(f);
    }

    public static void addFlight(Flight f,Context context){
        flightsList.add(f);
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(FLIGHT_FILE,Context.MODE_PRIVATE);
            ObjectOutputStream objout = new ObjectOutputStream(out);
            objout.writeObject(flightsList);
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

    public static void removeFlight(Flight f, Context context){
        flightsList.remove(f);
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(FLIGHT_FILE,Context.MODE_PRIVATE);
            ObjectOutputStream objout = new ObjectOutputStream(out);
            objout.writeObject(flightsList);
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

    public static ArrayList<Flight> getAllFlights(){
        return flightsList;
    }
}
