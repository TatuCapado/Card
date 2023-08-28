package models;

public class Transport {
    private int id;
    private int line;
    private String type;
    private float charge;

    public Transport(int id, int line, String type, float charge) {
        this.id = id;
        this.line = line;
        this.type = type;
        this.charge = charge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }
}
