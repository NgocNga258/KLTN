#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include "src/iotc/common/string_buffer.h"
#include "src/iotc/iotc.h"

//SoftwareSerial portOne(3, 1);
SoftwareSerial portTwo(13, 15);
#define Led1_PIN 5
#define Led2_PIN 4

//#define WIFI_SSID "Nhat Nga"
//#define WIFI_PASSWORD "nhatnga123"
#define WIFI_SSID "FETEL@E103B_SV"
#define WIFI_PASSWORD "bmmtnE103bSV"

const char* SCOPE_ID = "0ne00A3DA8A";
const char* DEVICE_ID = "esp8266";
const char* DEVICE_KEY = "l38q+ZhA8DqqX6SbMKj9EIksmtzW6lma6ULxkHd8nTc=";

void on_event(IOTContext ctx, IOTCallbackInfo* callbackInfo);
#include "src/connection.h"

void on_event(IOTContext ctx, IOTCallbackInfo* callbackInfo) {
  // ConnectionStatus
  if (strcmp(callbackInfo->eventName, "ConnectionStatus") == 0) { // so sanh su kien dc goi co bang ConnectionStatus hay khong
    LOG_VERBOSE("Is connected ? %s (%d)",
                callbackInfo->statusCode == IOTC_CONNECTION_OK ? "YES" : "NO",
                callbackInfo->statusCode);
    isConnected = callbackInfo->statusCode == IOTC_CONNECTION_OK;
    return;
  }

  // payload buffer doesn't have a null ending.
  // add null ending in another buffer before print
  AzureIOT::StringBuffer buffer;
  if (callbackInfo->payloadLength > 0) {
    buffer.initialize(callbackInfo->payload, callbackInfo->payloadLength);
  }

  LOG_VERBOSE("- [%s] event was received. Payload => %s\n",
              callbackInfo->eventName, buffer.getLength() ? *buffer : "EMPTY");

  if (strcmp(callbackInfo->eventName, "Command") == 0) {
    LOG_VERBOSE("- Command name was => %s\r\n", callbackInfo->tag);
    if (strcmp(callbackInfo->tag, "TurnOnLed1") == 0)
    {
      portTwo.print(1);
      digitalWrite(Led1_PIN, HIGH);
    }
    else if (strcmp(callbackInfo->tag, "TurnOffLed1") == 0)
    {
      portTwo.print(0);
      digitalWrite(Led1_PIN, LOW);
    }

    else if (strcmp(callbackInfo->tag, "TurnOnLed2") == 0)
    {
      portTwo.print(3);
      digitalWrite(Led2_PIN, HIGH);
    }
    else if (strcmp(callbackInfo->tag, "TurnOffLed2") == 0)
    {
      portTwo.print(2);
      digitalWrite(Led2_PIN, LOW);
    }  
  }
}

void setup() {
  Serial.begin(115200);
  //portOne.begin(115200);
  portTwo.begin(115200);  
  pinMode(Led1_PIN, OUTPUT);
  pinMode(Led2_PIN, OUTPUT);    
  digitalWrite(Led1_PIN, LOW);
  digitalWrite(Led2_PIN, LOW);

  connect_wifi(WIFI_SSID, WIFI_PASSWORD); // ket noi wifi 
  connect_client(SCOPE_ID, DEVICE_ID, DEVICE_KEY); // ket noi giua thiet bi nhung voi Azure IoT qua id

  if (context != NULL) {
    lastTick = 0;  // set timer in the past to enable first telemetry a.s.a.p 
  }
  
}

void loop() {

  int status1 = digitalRead(Led1_PIN);
  int status2 = digitalRead(Led2_PIN);
  
  if (isConnected) {    
    unsigned long ms = millis();

    if (ms - lastTick > 2500) {  // send telemetry every 5 seconds
      char msg[64] = {0};
      int pos = 0, errorCode = 0;      
       
      lastTick = ms;
      if (loopId++ % 2 == 0) {  // send telemetry
        // tao ra chuoi tu cac kieu du lieu va tra ve so luong ki tu
        // pos = snprintf(msg, sizeof(msg) - 1, "{\"led1\": %d,\"led2\": %d}", status1, status2); 
        // errorCode = iotc_send_telemetry(context, msg, pos);    
      }  
      msg[pos] = 0;
      if (errorCode != 0) {
        LOG_ERROR("Sending message has failed with error code %d", errorCode);
      }
    }  
    
    iotc_do_work(context);  // do background work for iotc
  } 
  else {
    iotc_free_context(context);
    context = NULL;
    connect_client(SCOPE_ID, DEVICE_ID, DEVICE_KEY);
  }
}
