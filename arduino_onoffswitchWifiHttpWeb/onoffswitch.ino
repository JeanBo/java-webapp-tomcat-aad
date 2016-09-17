#include <EEPROM.h>
#include <SPI.h>
#include <WiFi.h>


char ssid[] = "YOUR WIFI SSID";      //  your network SSID (name)
char pass[] = "YOUR PASS!";   // your network password
IPAddress ip(192, 168, 1, 104);
int LAMP1_PIN = 8;
int RESET_PIN = 5;
  
int status = WL_IDLE_STATUS;
int counter = 0;
int loopCounter = 0;

WiFiServer server(80);


WiFiServer startWifiServer(){
  
  
  //  Setting static IP
  WiFi.config(ip);

  status = WiFi.begin(ssid, pass);
  
  // attempt to connect to Wifi network:
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to Network named: ");
    Serial.println(ssid);                   // print the network name (SSID);

    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
    status = WiFi.begin(ssid, pass);
    // wait 10 seconds for connection:
    delay(10000);
  }
  server.begin();
  return server;
}



void setup() {
  
  Serial.println("Setup...");

  
  digitalWrite(RESET_PIN, HIGH);
  delay(200);

  pinMode(RESET_PIN, OUTPUT);
  pinMode(LAMP1_PIN, OUTPUT);

  //start serial connection
  Serial.begin(9600);


  startWifiServer();
  
  /* check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);       // don't continue
  }  
  */
  printWifiStatus(); 
  delay(200);
}



void loop() {

  WiFiClient client = server.available();   // listen for incoming clients
  loopCounter++;

  Serial.println(loopCounter);  
  if(loopCounter>=200){
    loopCounter=0;
    softReset();
  }
  delay(1000);
  
  if (!client) {
    return;
  }else{
    //  This means there is a Client
    while(client.connected()){
      
      if(client.available()){
          String request = client.readStringUntil('\r');  
          client.flush();
         
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
          }else if (request.indexOf("/RESET") != -1){
            delay(100);
            client.stop();
            softReset();
          }
      }      
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

void softReset(){
  // Clearing memory
  for(int i=0; i< EEPROM.length(); i++){
    EEPROM.write(i,0);
  }
  setup();
//  asm volatile ("  jmp 0");
}

void hardReset(){
  digitalWrite(RESET_PIN,LOW);
}

