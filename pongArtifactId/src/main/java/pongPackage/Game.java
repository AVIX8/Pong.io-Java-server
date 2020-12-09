package pongPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOClient;

public class Game {
    public boolean running = false;
    public Map<UUID, SocketIOClient> sockets = new HashMap<>();
    public Map<UUID, Player> players = new HashMap<>();
    public ArrayList<UUID> disconnected = new ArrayList<>();
    
    public Game() {
        // this.world = new World(this);
        // this.intervalId;
    }
    
    public void emitAllSockets(String event, Object data) {
        for (UUID socketID : sockets.keySet()) {
            sockets.get(socketID).sendEvent(event, data);
        }
      }
    
    public void lobbyUpdate() {
        ArrayList<SerializedPlayerForLobbyUpdate> serializedData = new ArrayList<SerializedPlayerForLobbyUpdate>();
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
        emitAllSockets("msg", String.format("%s отключился от игры.", players.get(socket.getSessionId()).name));
        if (running) {
          disconnected.add(socket.getSessionId());
        } else {
            sockets.remove(socket.getSessionId());
            players.remove(socket.getSessionId());
            lobbyUpdate();
        }
    }
    
    public void setPlayerData(SocketIOClient socket, HashMap data) {
        if (players.containsKey(socket.getSessionId()) && players.get(socket.getSessionId()).set(data)) {
          lobbyUpdate();
        
          boolean f = players.size() != 0;
          for (UUID playerID : players.keySet()) {
            if (!players.get(playerID).ready) {
              f = false;
              break;
            }
          }
          if (f) {
            System.out.println("Game Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            // this.start();
          }
        } else {
          socket.sendEvent("msg", "Не удалось выполнить операцию");
        }
      }
    
    //   tick() {
    //     let data = this.world.update();
    //     this.emitAllSockets("worldUpdate", data.worldUpdate);
    //     this.emitAllSockets("scoreUpdate", data.scoreUpdate)
    //     Object.keys(this.players).forEach((playerID) => {
    //       if (this.players[playerID]?.score > 100) {
    //         this.finish(this.players[playerID])
    //       }
    //     });
    //   }
    
    //   start() {
    //     this.running = true
    //     Object.keys(this.players).forEach((playerID) => {
    //       this.players[playerID].score = 0
    //       this.players[playerID].ready = false
    //     });
    //     this.world.setPlayers(this.players);
    //     this.emitAllSockets("gameStart", this.world.getInfo());
    //     setTimeout(() => {
    //       this.intervalId = setInterval(() => {
    //         this.tick();
    //       }, 17);
    //     }, 5000);
    //   }
    
    //   finish(winner) {
    //     this.running = false
    //     this.disconnected.forEach(id => {
    //       delete this.sockets[id];
    //       delete this.players[id];
    //     })
    //     this.lobbyUpdate();
    //     this.emitAllSockets("gameFinish", { winner });
    //     clearInterval(this.intervalId);
    //   }   
}
