package pongPackage;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import com.corundumstudio.socketio.SocketIOClient;

public class Player extends Wall {
  public String name = "Игрок";
  public String color = "#f00";
  private double speed = 0.5;
  public boolean ready = false;
  public int score = 0;
  public Vector unitFromCenter;
  public UUID id;
  private Control control = new Control();

  public Player(SocketIOClient socket) {
    super(0, 0, 0, 0);
    this.id = socket.getSessionId();
  }

  public void setControl(String key, boolean pressed) {
    if (key.equals("ArrowRight"))
      control.right = pressed;
    if (key.equals("ArrowLeft"))
      control.left = pressed;
  }

  public void setPaddle(double size, double a, double d, double r) {
    double x1 = r * Math.cos(a);
    double y1 = r * Math.sin(a);
    double x2 = r * Math.cos(a + d);
    double y2 = r * Math.sin(a + d);

    super.start = new Vector(x1, y1);
    super.end = new Vector(x2, y2);
    super.pos = new Vector((x1 + x2) / 2, (y1 + y2) / 2);
    super.size = size / 2;

    this.unitFromCenter = this.pos.unit();
    this.speed = this.size / 8;
    super.reposition();
  }

  public void move(double limit) {
    super.vel = new Vector(0, 0);
    if (control.left && start.mag() < limit) {
      super.vel = unit().mult(-speed);
    }
    if (control.right && end.mag() < limit) {
      super.vel = unit().mult(speed);
    }
    super.reposition();
  }

  public boolean set(HashMap<String, Object> data) {
    if (data.get("name") != null)
      name = data.get("name").toString();
    if (data.get("color") != null)
      color = data.get("color").toString();
    if (data.get("ready") != null)
      ready = Boolean.parseBoolean(data.get("ready").toString());

    Set<String> keys = data.keySet();
    return keys.contains("name") || keys.contains("color") || keys.contains("ready");
  }

  public HashMap<String, Object> serializeForLobbyUpdate() {
    HashMap<String, Object> serializedData = new HashMap<>();
    serializedData.put("id", id);
    serializedData.put("name", name);
    serializedData.put("color", color);
    serializedData.put("ready", ready);
    return serializedData;
  }
}
