package id.rajaopak.gorgon.enums;

public enum FilterState {

    ALL,
    PLAYER,
    STATE;

    private String object;

    FilterState() {

    }

    public FilterState setObject(String object) {
        this.object = object;
        return this;
    }

    public String getObject() {
        return object;
    }
}
