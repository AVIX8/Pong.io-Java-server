package pongPackage;

public class LobbyData {

    private String name;
    private String color;
    private boolean ready;

    public LobbyData() {
    }

    public LobbyData(String name, String color, boolean ready) {
        super();
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public boolean getReady() {
        return ready;
    }
    public void setReady(Boolean ready) {
        this.ready = ready;
    }
    
}
