package com.ips.project_si.model;

public class User {
    private String nom,prenom,addresse,telephone,password,email, id , mac_adress;


    public User()
    {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(String email, String nom, String prenom, String addresse, String telephone, String password)
    {
        this.nom = nom;
        this.prenom = prenom;
        this.addresse = addresse;
        this.telephone = telephone;
        this.password = password;
        this.email=email;
    }

    public User(String nom, String prenom, String addresse, String telephone, String password, String email, String id, String mac_adress) {
        this.nom = nom;
        this.prenom = prenom;
        this.addresse = addresse;
        this.telephone = telephone;
        this.password = password;
        this.email = email;
        this.id = id;
        this.mac_adress = mac_adress;
    }


    public String getMac_adress() {
        return mac_adress;
    }

    public void setMac_adress(String mac_adress) {
        this.mac_adress = mac_adress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getPrenom()
    {
        return prenom;
    }

    public void setPrenom(String prenom)
    {
        this.prenom = prenom;
    }

    public String getAddresse()
    {
        return addresse;
    }

    public void setAddresse(String addresse)
    {
        this.addresse = addresse;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
