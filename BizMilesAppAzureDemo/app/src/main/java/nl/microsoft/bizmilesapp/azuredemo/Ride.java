package nl.microsoft.bizmilesapp.azuredemo;

import android.location.Location;

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by chvugrin on 29-4-2016.
 */
public class Ride {

    @com.google.gson.annotations.SerializedName("startaddress")
    private String startAddress;
    @com.google.gson.annotations.SerializedName("stopaddress")
    private String stopAddress;
    @com.google.gson.annotations.SerializedName("kilometers")
    private float kilometers;
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    @com.google.gson.annotations.SerializedName("starttime")
    private Time started_at;
    @com.google.gson.annotations.SerializedName("stoptime")
    private Time stopped_at;


    public Time getStarted_at() {
        return started_at;
    }

    public void setStarted_at(Time started_at) {
        this.started_at = started_at;
    }

    public Time getStopped_at() {
        return stopped_at;
    }

    public void setStopped_at(Time stopped_at) {
        this.stopped_at = stopped_at;
    }


    public String getId() {
        return mId;
    }
    public final void setId(String id) {
        mId = id;
    }


    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(float kilometers) {
        this.kilometers = kilometers;
    }


    public Ride() {

    }

    @Override
    public String toString() {
        return getStartAddress();
    }


}
