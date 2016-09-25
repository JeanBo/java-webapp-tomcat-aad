#include <Adafruit_SleepyDog.h>
#include <EEPROM.h>
#include <SPI.h>
#include <WiFi.h>
#include <TimeLib.h>
//#include <SD.h>


char ssid[] = "YOUR SSID";      //  your network SSID (name)
char pass[] = "XXXX";   // your network password
//IPAddress ip(192, 168, 1, 104);
//File logFile;

int LAMP1_PIN = 8;
int RESET_PIN = 5;
  
int LOOPLIMIT = 100;
int counter = 0;
int loopCounter = 0;


WiFiServer server(80);


int startWifiServer(){
    
  //  Setting static IP, changed to DHCP by configging router to assign proper IP to this MAC
  //WiFi.config(ip);
  int status = WL_IDLE_STATUS;
  int retryCounter = 0;
  
  // attempt to connect to Wifi network:
  while (status != WL_CONNECTED) {
    retryCounter++;
    Serial.print("Attempting to connect to Network, attempt nr: ");
    Serial.print(retryCounter);
    Serial.println("");
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
    // If status still not connected after 5 times --> RESET
    if(retryCounter>=5 && status != WL_CONNECTED ){
      wdReset();
    }
    // wait 2 seconds for connection:
    delay(2000);
  }
  Serial.println("Starting the (web) server ");
  server.begin();
  return status;
}



void setup() {

  Serial.begin(9600);

  Serial.println("Setup...");
  Serial.println("Firmware version: "));
  Serial.println(WiFi.firmwareVersion());

  byte mac[6];  
  WiFi.macAddress(mac);
  Serial.print("MAC: ");
  Serial.print(mac[5],HEX);
  Serial.print(":");
  Serial.print(mac[4],HEX);
  Serial.print(":");
  Serial.print(mac[3],HEX);
  Serial.print(":");
  Serial.print(mac[2],HEX);
  Serial.print(":");
  Serial.print(mac[1],HEX);
  Serial.print(":");
  Serial.println(mac[0],HEX);



  //  Setup of pins  
  digitalWrite(RESET_PIN, HIGH);
  delay(200);

  pinMode(RESET_PIN, OUTPUT);
  pinMode(LAMP1_PIN, OUTPUT);

  //  Setup of SD Card
  /*  SD Card init
  if (!SD.begin(4)) {
    Serial.println("initialization of SD Card failed!");
    return;
  }
  */
  //  Wifishield
  startWifiServer();
  printWifiStatus(); 
  delay(200);

}


void loop() {

  WiFiClient client = server.available();   // listen for incoming clients
  
  //  Every n (LOOPLIMIT) amount of seconds do this check
  if(loopCounter>=LOOPLIMIT){
    loopCounter=0;              //  Setting the counter back to 0 again
    startWifiServer();
  }
  

  //  Only enter this code if there is a client request comming in
  if (!client) {
    delay(1000);    //  Waiting 1 second
    loopCounter++;
    Serial.println("loopcounter: ");
    Serial.print(loopCounter);
    return;
  }else{
    //  This means there is a Client
    while(client.connected()){
      
      if(client.available()){
          String request = client.readStringUntil('\r');  
         
          if (request.indexOf("/LAMP1=AAN") != -1) {
            if (digitalRead(LAMP1_PIN) != HIGH){
              counter++;                
              Serial.println("Putting Lamp 1 ON...");
              Serial.println(counter);
              digitalWrite(LAMP1_PIN, HIGH);
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/LAMP1=UIT") != -1){
            if (digitalRead(LAMP1_PIN) != LOW){
              counter++;
              Serial.println("Putting Lamp 1 OFF...");
              Serial.println(counter);
              digitalWrite(LAMP1_PIN, LOW);
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/CHECK") != -1){
            client.println("HTTP/1.1 200 OK");
            client.println("Content-Type: text/html");
            client.println("Connection: close");
            client.println();
            client.println("<!DOCTYPE HTML>");
            client.println("<html>");
            client.println("OK");
            client.println("</html>");
            delay(100);
            //break;
            client.stop();
          }else if (request.indexOf("/RESET") != -1){
            delay(100);
            client.stop();
            wdReset();
          }
      }
      client.flush();
    }
  }
}


void printWifiStatus() {

  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
  // print where to go in a browser:
  Serial.print("To see this page in action, open a browser to http://");
  Serial.println(ip);

}

void wdReset(){
  int countdownMS = Watchdog.enable(1000);
  Serial.print("Get ready, the watchdog will reset ");
  // Clearing memory
  for(int i=0; i< EEPROM.length(); i++){
    EEPROM.write(i,0);
  }
  Watchdog.reset();
}

/*
void softReset(){
  asm volatile ("  jmp 0");
}
*/

void hardReset(){
  digitalWrite(RESET_PIN,LOW);
}
