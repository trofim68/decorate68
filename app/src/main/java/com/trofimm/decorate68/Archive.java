package com.trofimm.decorate68;
public class Archive {
    public String id_archive;
    public String customer_archive;
    public String comment_archive;
    public String contact_archive;
    public String box_archive;
    public String cost_archive;
    public String precost_archive;
    public String fDate_archive;
    public String lDate_archive;

    public Archive() {
    }

    public Archive(String id_archive,
                   String customer_archive,
                   String comment_archive,
                   String contact_archive,
                   String box_archive,
                   String cost_archive,
                   String precost_archive,
                   String fDate_archive,
                   String lDate_archive) {
        this.id_archive = id_archive;
        this.customer_archive = customer_archive;
        this.comment_archive = comment_archive;
        this.contact_archive = contact_archive;
        this.box_archive = box_archive;
        this.cost_archive = cost_archive;
        this.precost_archive = precost_archive;
        this.fDate_archive = fDate_archive;
        this.lDate_archive = lDate_archive;
    }
}
