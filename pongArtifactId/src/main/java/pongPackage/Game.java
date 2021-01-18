package pongPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.corundumstudio.socketio.SocketIOClient;

public class Game {
  private boolean running = false;
  private HashMap<UUID, SocketIOClient> sockets = new HashMap<>();
  private HashMap<UUID, Player> players = new HashMap<>();
  private ArrayList<UUID> disconnected = new ArrayList<>();
  private World world;
  private Timer timer;

  public Game() {
    this.world = new World();
  }

  private void emitAllSockets(String event, Object data) {
    for (UUID socketID : sockets.keySet()) {
      sockets.get(socketID).sendEvent(event, data);
    }
  }

  private void lobbyUpdate() {
    ArrayList<HashMap<String, Object>> serializedData = new ArrayList<>();
    for (UUID playerID : players.keySet()) {
      serializedData.add(players.get(playerID).serializeForLobbyUpdate());
    }
    emitAllSockets("lobbyUpdate", serializedData);
  }

  public void addPlayer(SocketIOClient socket) {
    if (running) {
      socket.sendEvent("msg", "Игра уже началась.");
    } else {
      sockets.put(socket.getSessionId(), socket);
      players.put(socket.getSessionId(), new Player(socket));
      lobbyUpdate();
    }

  }

  public void removePlayer(SocketIOClient socket) {
    if (!players.containsKey(socket.getSessionId())) return;
    emitAllSockets("msg", String.format("%s отключился от игры.", players.get(socket.getSessionId()).name));
    if (running) {
      disconnected.add(socket.getSessionId());
    } else {
      sockets.remove(socket.getSessionId());
      players.remove(socket.getSessionId());
      lobbyUpdate();
    }
  }

  public void setPlayerControl(SocketIOClient socket, String key, boolean pressed) {
    if (players.containsKey(socket.getSessionId())) {
      players.get(socket.getSessionId()).setControl(key, pressed);
    }
  }

  public void setPlayerData(SocketIOClient socket, HashMap<String, Object> data) {
    if (players.containsKey(socket.getSessionId()) && players.get(socket.getSessionId()).set(data)) {
      lobbyUpdate();

      boolean f = players.size() > 2;
      for (UUID playerID : players.keySet()) {
        if (!players.get(playerID).ready) {
          f = false;
          break;
        }
      }
      if (f) {
        start();
      }
    } else {
      socket.sendEvent("msg", "Не удалось выполнить операцию");
    }
  }

  private void tick() {
    HashMap<String, Object> data = this.world.update();
    this.emitAllSockets("worldUpdate", data.get("worldUpdate"));
    this.emitAllSockets("scoreUpdate", data.get("scoreUpdate"));

    for (UUID playerID : players.keySet()) {
      if (players.get(playerID).score > 100) {
        this.finish(players.get(playerID));
      }
    }
  }

  private void start() {
    running = true;
    for (UUID playerID : players.keySet()) {
      players.get(playerID).score = 0;
      players.get(playerID).ready = false;
    }

    this.world.setPlayers(this.players);
    this.emitAllSockets("gameStart", this.world.getInfo());

    timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        tick();
      }
    };

    timer.scheduleAtFixedRate(task, 5000l, 17l);
  }

  private void finish(Player winner) {
    this.running = false;
    for (UUID id : disconnected) {
      sockets.remove(id);
      players.remove(id);
    }
    lobbyUpdate();
    HashMap<String, Object> data = new HashMap<>();
    data.put("winner", winner);
    emitAllSockets("gameFinish", data);
    timer.cancel();
  }
}
