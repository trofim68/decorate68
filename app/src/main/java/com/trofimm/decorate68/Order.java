package com.trofimm.decorate68;

public class Order {
    public String id_order;
    public String customer;
    public String comment;
    public String contact;
    public String korobox;
    public String cost;
    public String preCost;
    public String firstDate;
    public String lastDate;

    public Order() {
    }

    public Order (String id_order,
                  String customer,
                  String comment,
                  String contact,
                  String korobox,
                  String cost,
                  String preCost,
                  String firstDate,
                  String lastDate){
        this.id_order = id_order;
        this.customer = customer;
        this.comment = comment;
        this.contact = contact;
        this.korobox = korobox;
        this.cost = cost;
        this.preCost = preCost;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }
}
