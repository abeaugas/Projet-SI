import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate("projetsi.json")
firebase_admin.initialize_app(cred,{
    'databaseURL': 'https://projetsi-df034-default-rtdb.firebaseio.com'})


def get_mac_token_dict():
    # Obtenir une référence à la base de données des utilisateurs
    users_ref = db.reference('users')

    # Obtenir tous les utilisateurs
    users = users_ref.get()

    # Création du  dictionnaire vide pour stocker les adresses MAC et les tokens
    mac_token_dict = {}

    # Parcouir chaque utilisateur et récupérez leur adresse MAC et leur token
    for user_id, user_details in users.items():
        if 'salle_c1' in user_details['access'].values():
            mac_address = user_details['mac_address']
            token = user_details['token']  
            mac_token_dict[mac_address] = token

    # Afficher le dictionnaire des adresses MAC et des tokens
    print(mac_token_dict)
    return mac_token_dict



