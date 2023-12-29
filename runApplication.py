#!/usr/bin/env python3

import logging
import pyotp
import dbus
import dbus.exceptions
import dbus.mainloop.glib
import dbus.service
import RPi.GPIO as GPIO
import time
import firebase

from ble import (
    Advertisement,
    Characteristic,
    Service,
    Application,
    find_adapter,
    Descriptor,
    Agent,
)


import array
from enum import Enum


#Boucle principale
MainLoop = None
try:
    from gi.repository import GLib

    MainLoop = GLib.MainLoop
except ImportError:
    import gobject as GObject

    MainLoop = GObject.MainLoop

#Getsion des loggers
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logHandler = logging.StreamHandler()
filelogHandler = logging.FileHandler("logs.log")
formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
logHandler.setFormatter(formatter)
filelogHandler.setFormatter(formatter)
logger.addHandler(filelogHandler)
logger.addHandler(logHandler)
####################

#Tableau de référencement des utilisateurs récuprérer sur la bdd --> firebase.get_mac_token_dict() 
USERID = {"48:02:86:7E:E2:EA":"JBSWY3DPEHPK3PXP"} # <--Tableau de test 

#Boucle principale d'événement dBus
mainloop = None

#Recupération des services BlueD
BLUEZ_DEVICE_IFACE = 'org.bluez.Device1'
BLUEZ_SERVICE_NAME = "org.bluez"
GATT_MANAGER_IFACE = "org.bluez.GattManager1"
LE_ADVERTISEMENT_IFACE = "org.bluez.LEAdvertisement1"
LE_ADVERTISING_MANAGER_IFACE = "org.bluez.LEAdvertisingManager1"
DBUS_PROP_IFACE = "org.freedesktop.DBus.Properties"
###############################@

#Class des erreurs des services dBus utilisés
class InvalidArgsException(dbus.exceptions.DBusException):
    _dbus_error_name = "org.freedesktop.DBus.Error.InvalidArgs"


class NotSupportedException(dbus.exceptions.DBusException):
    _dbus_error_name = "org.bluez.Error.NotSupported"


class NotPermittedException(dbus.exceptions.DBusException):
    _dbus_error_name = "org.bluez.Error.NotPermitted"


class InvalidValueLengthException(dbus.exceptions.DBusException):
    _dbus_error_name = "org.bluez.Error.InvalidValueLength"


class FailedException(dbus.exceptions.DBusException):
    _dbus_error_name = "org.bluez.Error.Failed"
##############################################

#Definition des fonction de log
def register_app_cb():
    logger.info("GATT application registered")


def register_app_error_cb(error):
    logger.critical("Failed to register application: " + str(error))
    mainloop.quit()
#################################
    

#Service BLE
class BlueDService(Service):
   #UUID du service pour son identification
    BLUED_SVC_UUID = "12634d89-d598-4874-8e86-7d042ee07ba7"

    def __init__(self, bus, index):
        Service.__init__(self, bus, index, self.BLUED_SVC_UUID, True)
        #Ajout de 
        self.add_characteristic(DoorControlCharacteristic(bus, 0, self))
#############
    
#Class de la caractéristique du service (ecriture du token)
class DoorControlCharacteristic(Characteristic):
    uuid = "4116f8d2-9f66-4f58-a53d-fc7440e7c14e"
    description = b"Open the door with your id"

    def __init__(self, bus, index, service):
        Characteristic.__init__(
            self, bus, index, self.uuid, ["encrypt-write"], service,
        )

        self.value = [0xFF] #Valeur de départ

    #Récupération des informations des appreils connecter au service
    def print_device_characteristics(self):
        bus = dbus.SystemBus()
        manager = dbus.Interface(bus.get_object("org.bluez", "/"), "org.freedesktop.DBus.ObjectManager")
        objects = manager.GetManagedObjects()

        for path, interfaces in objects.items():
            if "org.bluez.Device1" in interfaces:
                device = dbus.Interface(bus.get_object("org.bluez", path), "org.freedesktop.DBus.Properties")
                characteristics = device.GetAll("org.bluez.Device1")
                return characteristics
                
    #Fonction appeler lors de l'écriture sur la caractéristique
    def WriteValue(self, value, options):
        id = bytes(value).decode("utf-8")

        #Utilisation de l'adresse MAC pour récupérer le token de l'utilisateur
        Address=self.print_device_characteristics()['Address']
        logger.info("Token:" + id+" received from "+Address)
        
        #Vérification du token
        if(pyotp.TOTP(USERID[Address]).verify(id)):
            logger.info("Door Opened")
            GPIO.setmode(GPIO.BCM)
            GPIO.setup(18, GPIO.OUT)
            GPIO.output(18, GPIO.HIGH)
            # Attendre 5 secondes
            time.sleep(5)
            # Éteidre la LED
            GPIO.output(18, GPIO.LOW)
            GPIO.cleanup()
            logger.info("Door Close")
        else:
            logger.info("Door Not Opened")
##########################################################        

#Class de l'annonce du service
class BlueDAdvertisement(Advertisement):
    def __init__(self, bus, index):
        Advertisement.__init__(self, bus, index, "peripheral")
        self.add_manufacturer_data(
            0xFFFF, [0x70, 0x74],
        )
        self.add_service_uuid(BlueDService.BLUED_SVC_UUID)

        self.add_local_name("BlueD")
        self.include_tx_power = True
################################

#Redefinciton des fonction de log
def register_ad_cb():
    logger.info("Advertisement registered")


def register_ad_error_cb(error):
    logger.critical("Failed to register advertisement: " + str(error))
    mainloop.quit()
###################################

#Chemin de l'agent dBUs
AGENT_PATH = "/com/blued/agent"

#Fonction principale
def main():

    #Configuartion de la boucle principale d'événement dBus
    global mainloop
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

    # Obtentiion du system dBus
    bus = dbus.SystemBus()

    # Récupération du controlleur bluetooth
    adapter = find_adapter(bus)

    if not adapter:
        logger.critical("GattManager1 interface not found")
        return

    #Controlle de l'adapteur bluetooth via une interface dBus
    adapter_obj = bus.get_object(BLUEZ_SERVICE_NAME, adapter)
    adapter_props = dbus.Interface(adapter_obj, "org.freedesktop.DBus.Properties")

    # Activation du bluetooth
    adapter_props.Set("org.bluez.Adapter1", "Powered", dbus.Boolean(1))

    #Controlle des services BLE via une interface dBus
    service_manager = dbus.Interface(adapter_obj, GATT_MANAGER_IFACE)
    ad_manager = dbus.Interface(adapter_obj, LE_ADVERTISING_MANAGER_IFACE)

    #Lancement de l'annonce du service
    advertisement = BlueDAdvertisement(bus, 0)
    obj = bus.get_object(BLUEZ_SERVICE_NAME, "/org/bluez")

    #Ajout du service BLE
    app = Application(bus)
    app.add_service(BlueDService(bus, 2))


    #Enregistrement de l'agent dBus
    agent_manager = dbus.Interface(obj, "org.bluez.AgentManager1")
    agent_manager.RegisterAgent(AGENT_PATH, "NoInputNoOutput")


    #Enregistrement de l'advertiser ble
    ad_manager.RegisterAdvertisement(
        advertisement.get_path(),
        {},
        reply_handler=register_ad_cb,
        error_handler=register_ad_error_cb,
    )

    logger.info("Registering GATT application...")

    #Enregistrement du service lié à l'advertiser
    service_manager.RegisterApplication(
        app.get_path(),
        {},
        reply_handler=register_app_cb,
        error_handler=[register_app_error_cb],
    )
    agent_manager.RequestDefaultAgent(AGENT_PATH)

    #Lancement de la boucle principale d'événement dBus
    mainloop = MainLoop()
    mainloop.run()

if __name__ == "__main__":
    main()




