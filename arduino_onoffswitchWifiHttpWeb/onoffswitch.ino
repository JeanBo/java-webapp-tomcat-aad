#include <SPI.h>
#include <WiFi.h>


char ssid[] = "YOUR WIFI ID";
char pass[] = "PASSWORD";
int keyIndex = 0;           
// static lan address
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
  }
  
  counter=0;
  while(client && (!client.available())){
    counter++;
    delay(1000);
    Serial.println("Client not available yet, counter: ");
    Serial.println(counter);

    if(counter >= 120 ){
      counter=0;
      startWifiServer(); 
      break;
    }
  }
        
  String request = client.readStringUntil('\r');
  client.flush();

  if (request.indexOf("/LAMP1=AAN") != -1) {
    if (digitalRead(8) != HIGH){
      Serial.println("Putting Lamp 1 ON...");
      digitalWrite(8, HIGH);                     
    }
  } else if (request.indexOf("/LAMP1=UIT") != -1){
    if (digitalRead(8) != LOW){
      Serial.println("Putting Lamp 1 OFF...");
      digitalWrite(8, LOW);
    }
  }
  /*
  else if (client) {                             // if you get a client,
    client.println("HTTP/1.1 200 OK");
    client.println("Content-type:text/html");
    client.println();
  
    // the content of the HTTP response follows the header:
    client.print("Click <a href=\"/LAMP1=AAN\">here</a> LAMP 1 AAN<br>");
    client.print("Click <a href=\"/LAMP1=UIT\">here</a> LAMP 1 UIT<br>");
  
    // The HTTP response ends with another blank line:
    client.println();
  }
  */
  // Miliseconds, give time to close connection
  delay(1000);
//  client.stop();

}

void softReset(){
  asm volatile ("  jmp 0");
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


