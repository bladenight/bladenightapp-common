package de.greencity.bladenightapp.network.messages;

public class HandshakeClientMessage {

    public HandshakeClientMessage(String deviceId, int clientBuild, String phoneManufacturer, String phoneModel, String androidRelease) {
        this.did = deviceId;
        this.man = phoneManufacturer;
        this.mod = phoneModel;
        this.rel = androidRelease;
        this.setClientBuildNumber(clientBuild);
    }
    public String getDevideId() {
        return did;
    }
    public void setDeviceId(String deviceId) {
        this.did = deviceId;
    }
    public String getPhoneManufacturer() {
        return man;
    }
    public void setPhoneManufacturer(String phoneManufacturer) {
        this.man = phoneManufacturer;
    }
    public String getPhoneModel() {
        return mod;
    }
    public void setPhoneModel(String phoneModel) {
        this.mod = phoneModel;
    }
    public String getAndroidRelease() {
        return rel;
    }
    public void setAndroidRelease(String androidRelease) {
        this.rel = androidRelease;
    }
    public int getClientBuildNumber() {
        return bui;
    }
    public void setClientBuildNumber(int buildNumber) {
        this.bui = buildNumber;
    }

    private String did;
    private String man;
    private String mod;
    private String rel;
    private int bui;
}
