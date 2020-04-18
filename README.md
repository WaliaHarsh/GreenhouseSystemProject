# GreenhouseSystemProject

Refer to Software App SRS and the Technical Report for full insight to the project.

Greenhouse System Scale Android App

The Model is built to keep data of the Temperature readings as well as the Moisture level readings of the plants so the user can retrieve it from the cloud server. It will also allow the user to provide water to their nursery if the Moisture level is below the ideal percentage. The main advantage of this model is that it is cloud-based and the user will be able to monitor and nourish the plants at their convenience. So, the user will be able to access the data from any location. It makes the product different from others because of its automatic irrigation feature which waters the plants whenever the moisture level drops below a certain limit.

The android app code is provided in the repository.

The product key for each individual product comes hardcoded in the product as in the python code. 
The following will be for the product key"walia176x"
The pyhton code to be ran on the raspberri pi is as follows:

Code:
```
#!/usr/bin/python

import os
import time
import math
import pyrebase
import RPi.GPIO as GPIO
import dht11
import datetime
import sys                      # Import sys module
from time import sleep          # Import sleep from time
import Adafruit_GPIO.SPI as SPI # Import Adafruit GPIO_SPI Module
import Adafruit_MCP3008         # Import Adafruit_MCP3008
from firebase import firebase
import Adafruit_DHT


def databaseinit():
    global db
    config = {
        "apiKey": "AAAAEkVfQgw:APA91bF_E8d7VLDsLdtEjUVLMQFaipVQkya1faEooA2rrEZvMTxgGkwDH4PTzGU3mguGW2BG9l18Ro_KneyvQr44rU5KxUjM9HrWugr5UGsVfa9vt22vcp7ZFnO2dz60q3D7Lg1mI7OK",
        "authDomain": "greenhousesystemprojectceng.firebaseapp.com",
        "databaseURL": "https://greenhousesystemprojectceng.firebaseio.com/userdata/walia176x",
        "storageBucket": "greenhousesystemprojectceng.appspot.com"
      }
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()

def databasewrite(temperature, humidity, time1):
    seconds = time1.time()
    a = {"name": "Pyrebase", 'temperature': str(temperature),
         'humidity': str(val),
         'timestamp':str(time.time()),"message": "Dhyey"}
    try:
      db.child("data").push(a)
      print("Database writing success!")
    except:
      print ("Database cannot be written, check the permission on database")

#firebase= firebase.FirebaseApplication('https://ghs-db.firebaseio.com/',None)

sensor = 11
pin = 4

SPI_TYPE = 'HW'
dly = 1         # Delay of 2000ms ( second)
analogPort = 0

# Software SPI Configuration
CLK     = 11    # Set the Serial Clock  11
MISO    = 9    # Set the Master Input/Slave Output pin 9
MOSI    = 10    # Set the Master Output/Slave Input pin 10
CS      = 8    # Set the Slave Select 8

# Hardware SPI Configuration
HW_SPI_PORT = 0 # Set the SPI Port. Raspi has two.
HW_SPI_DEV  = 0 # Set the SPI Device

# Instantiate the mcp class from Adafruit_MCP3008 module and set it to 'mcp'.
if (SPI_TYPE == 'HW'):
    # Use this for Hardware SPI
    mcp = Adafruit_MCP3008.MCP3008(spi=SPI.SpiDev(HW_SPI_PORT, HW_SPI_DEV))
elif (SPI_TYPE == 'SW'):
    # Use this for Software SPI
    mcp = Adafruit_MCP3008.MCP3008(clk = CLK, cs = CS, miso = MISO, mosi = MOSI)

# Soil Moisture levels
dryAmount=72.0;
hydrateAmount=45.0;



if __name__ == "__main__":
  try:
    while True:
      databaseinit()
      # initialize GPIO
      GPIO.setwarnings(False)
      GPIO.setmode(GPIO.BCM)

      # read data using pin 22
      #instance = dht11.DHT11(pin=4)
      humidity,temperature = Adafruit_DHT.read_retry(sensor,pin)
      delay = 2
      GPIO.setup(6,GPIO.OUT)
      GPIO.output(6,GPIO.HIGH)

    # Read the value from the MCP3008 on the pin we specified in analogPort
      val = mcp.read_adc(analogPort)
      val = (100-(val*0.9765625)/10)
      print("Test Statement--1")
   
      #resultT = instance.read()
      #if resultT.is_valid():
      print("Timestamp: " + str(datetime.datetime.now()))
      #print("Temperature: %-3.1f C" % resultT.temperature)
      print('Temp={0:0.1f}*  Humidity={1:0.1f}%'.format(temperature, humidity))
      print("Soil Moisture: %-3.1f %%" % val)
     
      print("Test Statement")
     
      if val <= dryAmount:
        GPIO.output(6,GPIO.LOW)
        print("Water Pump turning On and Watering the plant")
        time.sleep(10)
        waterFlag=1;
        print("Water pump turning off")
     
     
      if val<=hydrateAmount and waterFlag==0:
        GPIO.output(6,GPIO.LOW)
        print("Water Pump turning On andHydrating the Plant")
        time.sleep(5)
        print("Water pump turning off")
        waterFlag=2
       
      #Making sure that time intervals are same
      if waterFlag==2:
        time.sleep(4)
       
      GPIO.cleanup()
      #1h between measurements
      time.sleep(10)
     
#      data = {
#          'Temperature': str(resultT.temperature),
#         'soilMoisture': str(val),
#          'timestamp':str(time.time())
#          }

      #temperature = resultT.temperature
      soilMoisture = val

      #store the readings in variable and convert it into string and using firbase.post then data will be posted to databse of firebase
     
      #print(result)

      waterFlag=0
     
      databasewrite(temperature, humidity, (datetime.datetime.now()))
     
      print("All OFF..... Good Bye!!!")
      #os.system('clear')
      #result = firebase.post('/ghs-db/Values',data)


  except KeyboardInterrupt:
    print("QUIT")
    GPIO.cleanup()
    os.system('clear')
    print("Cleanup")
    
    
```
