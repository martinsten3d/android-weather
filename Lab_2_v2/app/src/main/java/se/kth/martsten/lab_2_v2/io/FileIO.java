package se.kth.martsten.lab_2_v2.io;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.kth.martsten.lab_2_v2.model.Weather;

/**
 * Class for serializing/saving and deserializing/loading weather objects
 */
public class FileIO {

    /**
     * Saves a serialized version of a weather object.
     * @param context the current application context.
     * @param weather the weather object that should be serialized and saved
     */
    public static void saveFile(Context context, Weather weather) {
        try (FileOutputStream fos = context.openFileOutput("weather.ser", Context.MODE_PRIVATE)) {
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(weather);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads a deserialized version of a weather object from device storage.
     * @param context the current application context.
     * @return a weather object.
     */
    public static Weather loadFile(Context context) {
        try (FileInputStream fis = context.openFileInput("weather.ser")) {
            ObjectInputStream is = new ObjectInputStream(fis);
            return (Weather) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return new Weather();
    }
}
