package pongPackage;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import com.corundumstudio.socketio.SocketIOClient;

public class Player extends Wall {
    public String name = "Игрок";
    public String color = "#f00";
    public boolean ready = false;
    public double speed = 0.5;
    public int score = 0;
    public Vector unitFromCenter;
    public Control control;
    public UUID id;

    public Player(SocketIOClient socket) {
        super(0,0,0,0);
        this.id = socket.getSessionId();

        // socket.on("keydown", (key) => {
        //   if (key == "ArrowRight") this.control.right = true;
        //   if (key == "ArrowLeft") this.control.left = true;
        // });

        // socket.on("keyup", (key) => {
        //   if (key == "ArrowRight") this.control.right = false;
        //   if (key == "ArrowLeft") this.control.left = false;
        // });
      }
    
    public void setPaddle(double size, double a, double d, double r) {
        double x1 = r*Math.cos(a);
        double y1 = r*Math.sin(a);
        double x2 = r*Math.cos(a+d);
        double y2 = r*Math.sin(a+d);

        super.start = new Vector(x1,y1);
        super.end = new Vector(x2,y2);
        super.pos = new Vector((x1+x2)/2, (y1+y2)/2);
        super.size = size/2;

        this.unitFromCenter = this.pos.unit();
        this.speed = this.size / 8;
        super.reposition();
    }
    
    public void move(double limit) {
        super.vel = new Vector(0,0);
        if (this.control.left && this.start.mag() < limit) {
          super.vel = this.unit().mult(-this.speed);
        }
        if (this.control.right && this.end.mag() < limit) {
          super.vel = this.unit().mult(this.speed);
        }
        super.reposition();
      }
    
    public boolean set(HashMap data) {
        if (data.get("name") != null) name = data.get("name").toString();
        if (data.get("color") != null) color = data.get("color").toString();
        if (data.get("ready") != null) ready = Boolean.parseBoolean(data.get("ready").toString());
        
        Set keys = data.keySet();
        return keys.contains("name") || keys.contains("color") || keys.contains("ready");
      }
    
    public SerializedPlayerForLobbyUpdate serializeForLobbyUpdate() {
        return new SerializedPlayerForLobbyUpdate(id, name, color, ready);
    }
    
      public SerializedPos serialized() {
        return new SerializedPos(pos);
      }
}
