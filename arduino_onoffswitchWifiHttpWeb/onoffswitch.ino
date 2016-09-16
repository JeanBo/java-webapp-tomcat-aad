#include <SPI.h>
#include <WiFi.h>


char ssid[] = "put the name of your wifi AP here";      //  your network SSID (name)
char pass[] = "putt your own password here!";   // your network password
int keyIndex = 0;                 // your network key Index number (needed only for WEP)
IPAddress ip(192, 168, 1, 104);
  
int status = WL_IDLE_STATUS;

int counter = 0;
WiFiServer server(80);

void startWifiServer(){
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
  server.begin();                           // start the web server on port 80
}


void cls(){
  Serial.flush();
  for (int i=0; i<10; i++) {
    Serial.println("\n");  
  }
}

void setup() {
  

  //start serial connection
  Serial.begin(9600);

  pinMode(8, OUTPUT);

    startWifiServer();
  
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);       // don't continue
  }  
  
  printWifiStatus(); 
  
}



void loop() {
  WiFiClient client = server.available();   // listen for incoming clients
  if (!client) {
    return;
  }else{
    //  This means there is a Client
    while(client.connected()){
      
      if(client.available()){
          String request = client.readStringUntil('\r');  
          client.flush();
         
          if (request.indexOf("/LAMP1=AAN") != -1) {
            if (digitalRead(8) != HIGH){
              counter++;                
              Serial.println("Putting Lamp 1 ON...");
              Serial.println(counter);
              digitalWrite(8, HIGH);
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/LAMP1=UIT") != -1){
            if (digitalRead(8) != LOW){
              counter++;
              Serial.println("Putting Lamp 1 OFF...");
              Serial.println(counter);
              digitalWrite(8, LOW);
              delay(100);
              client.stop();
            }
          }else{
//            client.stop();
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

  /*
  else{
      
      unsigned long timeout = millis();
      while (client.available() == 0) {
        if (millis() - timeout > 5000) {
          Serial.println(">>> Client Timeout !");
          //printStatus(client,'NOK');
          client.stop();
          return;
        }
    }
    
  }
  
void printStatus(WiFiClient client, char message[]){

    client.println("HTTP/1.1 200 OK");
    client.println("Content-type:text/html");
    client.println();
    client.println(message);    
    // The HTTP response ends with another blank line:
    client.println();
}

void softReset(){
  asm volatile ("  jmp 0");
}



*/

