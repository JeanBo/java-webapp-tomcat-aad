package nl.microsoft.bizmilesapp.azuredemo;

/**
 * Created by chvugrin on 29-4-2016.
 */
public class Ride {

    @com.google.gson.annotations.SerializedName("startaddress")
    private String startAddress;
    @com.google.gson.annotations.SerializedName("stopaddress")
    private String stopAddress;
    @com.google.gson.annotations.SerializedName("kilometers")
    private double kilometers;
    @com.google.gson.annotations.SerializedName("id")
    private String mId;


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

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public Ride() {

    }

    @Override
    public String toString() {
        return getStartAddress();
    }
}
