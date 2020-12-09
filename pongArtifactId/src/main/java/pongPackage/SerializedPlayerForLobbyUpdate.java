package pongPackage;
import java.util.UUID;

public class SerializedPlayerForLobbyUpdate {
    public UUID id;
    public String name;
    public String color;
    public boolean ready;
    
    public SerializedPlayerForLobbyUpdate(UUID id, String name, String color, boolean ready) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.ready = ready;
    }
}
