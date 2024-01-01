# BlueD
##### Appareil pour l'ouverture de porte via le Bluetooth Low Energy (BLE)
## Inspiration du projet
https://punchthrough.com/creating-a-ble-peripheral-with-bluez/

Le fichier ble.py n'a pas été réalisé par nos soins, nous remercions son créateur.

## Lancement du programme
Il est nécessaire d'avoir un Raspberry Pi, de préférence le modèle 3, avec Pi OS Lite installé dessus.
La version de python 3 doit être installer ainsi que les dépendances suivantes.
`sudo apt install python3`

`pip install RPi.GPIO`
`pip install dbus-python` et/ou `pip install dbus-next`
`pip install firebase`
`pip install pyoyp`



Pour lancer l'application :<br>
`python runApplication.py`


## Erreur connue
### Firebase
Il est possible que le code disponible dans le fichier firebase.py ne soit pas fonctionnel à cause d'un problème d'identification avec la base de données. Pour cela, il est nécessaire de changer les informations contenues dans le fichier **projetsi.json**.
