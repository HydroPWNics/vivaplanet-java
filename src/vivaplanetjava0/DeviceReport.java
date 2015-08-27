/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vivaplanetjava0;

/**
 *
 * @author Shane
 */
public class DeviceReport
{
    private String DeviceID;

    public String getDeviceID() { return this.DeviceID; }
    public void setDeviceID(String DeviceID) { this.DeviceID = DeviceID; }
    
    private String Address;

    public String getAddress() { return this.Address; }
    public void setAddress(String Address) { this.Address = Address;; }

    private Sensors[]  DeviceSensors;

    public Sensors[] getDeviceSensors() { return this.DeviceSensors; }
    public void setDeviceSensors(Sensors[] DeviceSensors) { this.DeviceSensors = DeviceSensors; }
    
}
