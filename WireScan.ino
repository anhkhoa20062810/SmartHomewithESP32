#include <WiFi.h>
#include <WebServer.h>
#include <ESP32Servo.h>
#include <DHT.h>

// ================= WIFI =================
const char* ssid = "Thu";
const char* password = "anhkhoa123";

WebServer server(80);

// ================= DHT11 =================
#define DHTPIN 15
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// ================= LED =================
#define LED_ROOM1 4
#define LED_ROOM2 5

// ================= FAN =================
#define FAN_PIN 18

// ================= SERVO =================
Servo servo1;
Servo servo2;
Servo doorServo;

#define SERVO1_PIN 19
#define SERVO2_PIN 21
#define DOOR_SERVO_PIN 22

int fanSpeed = 0;

void setup() {

  Serial.begin(115200);

  pinMode(LED_ROOM1, OUTPUT);
  pinMode(LED_ROOM2, OUTPUT);
  pinMode(FAN_PIN, OUTPUT);

  servo1.attach(SERVO1_PIN);
  servo2.attach(SERVO2_PIN);
  doorServo.attach(DOOR_SERVO_PIN);

  dht.begin();

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting WiFi...");
  }

  Serial.println("WiFi Connected");
  Serial.println(WiFi.localIP());

}
  Serial.println("WiFi Connected");
  Serial.println(WiFi.localIP());
  
  // ================= API =================

  server.on("/led1/on", []() {
    digitalWrite(LED_ROOM1, HIGH);
    server.send(200, "text/plain", "LED1 ON");
  });

  server.on("/led1/off", []() {
    digitalWrite(LED_ROOM1, LOW);
    server.send(200, "text/plain", "LED1 OFF");
  });

  server.on("/led2/on", []() {
    digitalWrite(LED_ROOM2, HIGH);
    server.send(200, "text/plain", "LED2 ON");
  });
  server.on("/led2/off", []() {
    digitalWrite(LED_ROOM2, LOW);
    server.send(200, "text/plain", "LED2 OFF");
  });

  // ================= SERVO 1 =================

  server.on("/servo1", []() {
    servo1.write(90);
    delay(1000);
    servo1.write(0);
    server.send(200, "text/plain", "Servo1 Done");
  });

  // ================= SERVO 2 =================

  server.on("/servo2", []() {
    servo2.write(70);
    delay(1000);
    servo2.write(0);
    server.send(200, "text/plain", "Servo2 Done");
  });
  // ================= DOOR =================

  server.on("/door/open", []() {
    doorServo.write(90);
    delay(3000);
    doorServo.write(0);
    server.send(200, "text/plain", "Door Opened");
  });

  // ================= FAN =================

  server.on("/fan", []() {

    if (server.hasArg("speed")) {

      fanSpeed = server.arg("speed").toInt();

      fanSpeed = constrain(fanSpeed, 0, 255);

      analogWrite(FAN_PIN, fanSpeed);

      server.send(200, "text/plain", "Fan Speed Updated");

    } else {

      server.send(400, "text/plain", "Missing speed");
    }
  });
// ================= SENSOR =================

  server.on("/sensor", []() {

    float t = dht.readTemperature();
    float h = dht.readHumidity();

    String json = "{";
    json += "\"temperature\":" + String(t) + ",";
    json += "\"humidity\":" + String(h);
    json += "}";

    server.send(200, "application/json", json);
  });

  server.begin();
}
void loop() {
  server.handleClient();
}