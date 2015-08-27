/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * This is a simple demonstration of how to upload data to vivaplanet using
* from a file
* */

package vivaplanetjava0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import java.util.Calendar;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 *
 * @author Shane
 */
public class VivaPlanetJava0 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello, VivaPlanet");
       
        SendData();
        
    }
    
    /**
     *
     * @return
     */
    public static List<String> getSensorData()
    {
       String dataString = null;
        List<String> list = new ArrayList<String>();
        File file = new File("C:\\Users\\Shane\\Desktop\\test.txt"); //change this...
        BufferedReader reader = null;

        try 
        {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            
            while ((text = reader.readLine()) != null) 
            {
                list.add(text);
                //System.out.println(text);
                dataString = text;
            }
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (reader != null) 
                {
                    reader.close();
                }
            } catch (IOException ex) 
            {
                System.out.println("Error 2: " + ex.toString());
            }
        }

        //print out the list
        //System.out.println("Data:" + dataString);      
               
        return list;
    }

    public static void SendData()
    {
        DeviceReport vDeviceReport = new DeviceReport();

        try
        {
           
           vDeviceReport = getDeviceReport(vDeviceReport);
           
           System.out.println("Device Report: ");
           System.out.println("Device ID: " + vDeviceReport.getDeviceID());           
           System.out.println("Device Address: " + vDeviceReport.getAddress());
           Sensors[] sensor = vDeviceReport.getDeviceSensors(); 
           int sensorType = -1;
           //check to see which sensor reported the data
           for(int ii=0; ii<4; ii++) 
           {
            if(sensor[ii] != null)
            {
                sensorType = ii;
            }
           }
           
           //sensor type not is predetermined
           System.out.println("Sensor Type: " + sensor[sensorType].getSensorType());
           System.out.println("Sensor Value: " + sensor[sensorType].getValue());
           System.out.println("Sensor Time: " + sensor[sensorType].getReading());
                   
           NotifyServiceQueue(vDeviceReport);                        

        }
        catch (Exception ex)
        {
            System.out.println("Error 3: " + ex.toString());
        }
        finally
        {
               // put java locking code here and also cleanup code
        }
    }
    public static String DeviceSerialNumer = "17564321";
    private static DeviceReport getDeviceReport(DeviceReport vDeviceReport)
    {
        Sensors[] sensor = new Sensors[4];
        Sensors sensorTemp = new Sensors();
        
        List<String> sensorData = null;
        try
        {
            // Read the string from a file.
            sensorData = getSensorData();
            
        }
        catch (Exception ex)
        {
            System.out.println("Error 4: " + ex.toString());
        }
        
        
        String tempData = sensorData.get(0);
        System.out.println("String: " + tempData);
         
        String[] deviceOutput = tempData.split("\\^");
        
        vDeviceReport.setAddress(deviceOutput[0]);
        vDeviceReport.setDeviceID(DeviceSerialNumer);
        sensorTemp.setSensorType(deviceOutput[3]);
        sensorTemp.setValue(deviceOutput[4]);
        sensorTemp.setReading(deviceOutput[5]);

        switch (deviceOutput[3])
        {
            case "0":
                sensor[0] = sensorTemp; //using the ADC ports to assign sensor type  and the sensor number
                sensor[0].setSensorType("humidity");
                break;
            case "1":
                sensor[1] = sensorTemp; //using the ADC ports to assign sensor type  and the sensor number
                sensor[1].setSensorType("temperature");
                break;
            case "2":
                sensor[2] = sensorTemp; //using the ADC ports to assign sensor type  and the sensor number
                sensor[2].setSensorType("light");
                break;
            case "3":
                sensor[3] = sensorTemp; //using the ADC ports to assign sensor type  and the sensor number
                sensor[3].setSensorType("pH");
                break;
            default:

                break;
        }

        vDeviceReport.setDeviceSensors(sensor);
        
        return vDeviceReport;
    }

    static void NotifyServiceQueue(DeviceReport device)
    {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        URL iurl = null;
      try
        {
            iurl = new URL("https://vivaplanetbusservicedev.servicebus.windows.net/hummingbirdqueue/messages");
            Gson gson = new Gson();

            HttpURLConnection sbRequest = (HttpURLConnection)iurl.openConnection();
            
            //add request header
            sbRequest.setRequestMethod("POST"); // Triggers POST.
            sbRequest.setRequestProperty("Authorization", "SharedAccessSignature sr=https%3a%2f%2fvivaplanetbusservicedev.servicebus.windows.net%2fhummingbirdqueue%2fmessages&sig=Qo1tSbpcwKAsEB06I0WFsFhzZNARXvvkdoLQw4l61Bs%3d&se=1440626350&skn=DevicePolicy");

            //send post request
            sbRequest.setDoOutput(true);
            DataOutputStream output = new DataOutputStream(sbRequest.getOutputStream());
            
            //OutputStream output = sbRequest.getOutputStream();
            String body = gson.toJson(device); 
            //byte[] bytes = Encoding.UTF8.GetBytes(body);
            
            output.write(body.getBytes(charset));
            output.flush();
            output.close();
                   
            int responseCode = sbRequest.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + iurl);

            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(sbRequest.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

        }
        catch (Exception ex)
        {
            System.out.println("Error 7: " + ex.toString());
        }
    }
    
    private static String createToken(String resourceUri, String keyName, String key)
    {
        String charset = "UTF-8"; 
        String sasToken = null;
        Calendar currentTime = Calendar.getInstance(Locale.ENGLISH);
         String expiry = String.valueOf(currentTime.getTimeInMillis() + 3600 );
         try
         {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(charset), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] keyname_inBytes = keyName.getBytes(charset);

            byte[] hmac_pre = sha256_HMAC.doFinal(keyname_inBytes);
            String signature = bytesToHex(hmac_pre);

            sasToken = String.format(Locale.getDefault(), 
                                           "SharedAccessSignature sr={0}&sig={1}&se={2}&skn={3}", 
                                           java.net.URLEncoder.encode(resourceUri,charset),
                                           java.net.URLEncoder.encode(signature,charset), 
                                           expiry, keyName);
         }
         catch(Exception ex) 
         {
               System.out.println("Error 8: " + ex.toString());        
         }
         return sasToken;
    }
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) 
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        
        return new String(hexChars);
    }
}
