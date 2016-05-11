package nl.microsoft.bizmilesapp.azuredemo;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chvugrin on 11-5-2016.
 */
public class BmaUtils {

    protected static final String TAG = "bma-utils";


    protected static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    protected static boolean isMailAddresValid(String mailAddress){
        boolean result = false;
        try {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            CharSequence inputStr = mailAddress;

            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                result = true;
            }
        } catch(Exception e){ //catch (AddressException e) {
            Log.e(TAG, "invalid email address "+mailAddress+", exception: "+e.getCause());
        }
        return result;
    }

}
