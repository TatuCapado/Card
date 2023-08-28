package models;
import models.Client;

import java.io.Serializable;

public class Card implements Serializable{
    private int card_id;
    private Client person;
    private float card_money;
    private String pass_type;
    private float pass_price;
    private String expireDate;
    private int client_id;
    private int line_validation;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public Client getPerson() {
        return person;
    }

    public void setPerson(Client person) {
        this.person = person;
    }

    public float getCard_money() {
        return card_money;
    }

    public void setCard_money(float card_money) {
        this.card_money = card_money;
    }

    public String getPass_type() {
        return pass_type;
    }

    public void setPass_type(String pass_type) {
        this.pass_type = pass_type;
    }

    public float getPass_price() {
        return pass_price;
    }

    public void setPass_price(float pass_price) {
        this.pass_price = pass_price;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getLine_validation() {
        return line_validation;
    }

    public void setLine_validation(int line_validation) {
        this.line_validation = line_validation;
    }
}
