
/* 
 * Name:    arduino-gardenlightning-os 
 * Date:    27 sept 2016
 * Author:  chrisvugrinec@gmail.com
 * Description: This Arduino program switches may led garden lightning ON/ OFF
 *              In my shed I have a relay connnected with my arduino and 2 lamps..
 *              The relay swiches from 5V (arduino) to the led lights in my garden (220v)
 *              On my arduino I have an ethernet shield that is connected to a wireless router
 *              I also have a Arduino Wifi Shield (so I would not need the ethernet...but the Wifi Server 
 *              software is not giving the Stability I desire...keeps going zombie and I have no desire in spending
 *              too much time in debugging this issue, as the time of writing is already an issue for more than 2 years or so
 *              The Ethernet shield and its software is stable...so far so good :) 
*/

#include <SPI.h>
#include <EthernetV2_0.h>

byte mac[] = {  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,2,115);
EthernetServer server(80); 

int ETHERNETSHIELD_PIN = 4;
int LAMP1_PIN = 8;
int LAMP2_PIN = 9;

void setup() {
  Serial.begin(9600);

  //  Initialize Ethernet shield
  pinMode(ETHERNETSHIELD_PIN,OUTPUT);
  digitalWrite(ETHERNETSHIELD_PIN,HIGH);

  //  Initialize lamps
  pinMode(LAMP1_PIN,OUTPUT);
  digitalWrite(LAMP1_PIN,LOW);
  pinMode(LAMP2_PIN,OUTPUT);
  digitalWrite(LAMP2_PIN,LOW);

  //  Start Shield
  Serial.println("Starting w5200");
  Ethernet.begin(mac,ip);
  server.begin();
  Serial.println(Ethernet.localIP());
}

void loop() {
   EthernetClient client = server.available();
   if (!client) {
     return;
   }else{
    if(client.available()){
          String request = client.readStringUntil('\r');  
         
          if (request.indexOf("/LAMP1=AAN") != -1) {
            if (digitalRead(LAMP1_PIN) != HIGH){
              Serial.println("Putting Lamp 1 ON...");
              digitalWrite(LAMP1_PIN, HIGH);
              //giveOutput(client,"LAMP1 ON");
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/LAMP1=UIT") != -1){
            if (digitalRead(LAMP1_PIN) != LOW){
              Serial.println("Putting Lamp 1 OFF...");
              digitalWrite(LAMP1_PIN, LOW);
              //giveOutput(client,"LAMP1 OFF");
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/LAMP=AAN") != -1) {
            if (digitalRead(LAMP2_PIN) != HIGH){
              Serial.println("Putting Lamp 2 ON...");
              digitalWrite(LAMP2_PIN, HIGH);
              //giveOutput(client,"LAMP2 ON");
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/LAMP=UIT") != -1){
            if (digitalRead(LAMP2_PIN) != LOW){
              Serial.println("Putting Lamp 2 OFF...");
              digitalWrite(LAMP2_PIN, LOW);
              //giveOutput(client,"LAMP2 OFF");
              delay(100);
              client.stop();
            }
          }else if (request.indexOf("/CHECK") != -1){
            giveOutput(client,"CHECK OK");
            delay(100);
            //break;
            client.stop();
          }else if (request.indexOf("/RESET") != -1){
            delay(100);
            client.stop();
          }
      }
      client.flush();
   }
}

void giveOutput(EthernetClient client, char *message){
            client.println("HTTP/1.1 200 OK");
            client.println("Content-Type: text/html");
            client.println("Connection: close");
            client.println();
            client.println("<!DOCTYPE HTML>");
            client.println("<html>");
            client.println(*message);
            client.println("</html>");
  
}

