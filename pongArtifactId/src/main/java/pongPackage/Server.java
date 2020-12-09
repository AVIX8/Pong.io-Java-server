package pongPackage;

import com.corundumstudio.socketio.listener.*;

import java.util.HashMap;
import java.util.UUID;

// import com.corundumstudio.socketio.protocol.JsonSupport;
// import com.google.gson.Gson;
// import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.*;

public class Server {
    public static HashMap<UUID, Game> gameBySocketID = new HashMap<>();
    public static HashMap<String, Game> gameByRoom = new HashMap<>();
    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        // config.setHostname("localhost");
        // config.setJsonSupport(new JacksonJsonSupport());
        config.setPort(4000);
        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socket) {
                System.out.println("connect " + socket.getSessionId());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socket) {
                System.out.println("disconnect " + socket.getSessionId());
                if (gameBySocketID.containsKey(socket.getSessionId())) {
                    gameBySocketID.get(socket.getSessionId()).removePlayer(socket);
                }
            }
        });

        server.addEventListener("joinRoom", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socket, String data, AckRequest ackRequest) {
                System.out.println("JoinRoom " + data);
                socket.joinRoom(data);
                if (server.getRoomOperations(data).getClients().toArray().length == 1) {
                    gameByRoom.put(data, new Game());
                }
                gameBySocketID.put(socket.getSessionId(), gameByRoom.get(data));
                gameBySocketID.get(socket.getSessionId()).addPlayer(socket);
                
            }
        });

        server.addEventListener("setPlayerData", new HashMap<String, Object>().getClass(), new DataListener<HashMap<String, Object>>() {
            @Override
            public void onData(SocketIOClient socket, HashMap<String, Object> data, AckRequest ackRequest) {
                gameBySocketID.get(socket.getSessionId()).setPlayerData(socket, data);
            }
        });

        server.addEventListener("keydown", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socket, String key, AckRequest ackRequest) {
                gameBySocketID.get(socket.getSessionId()).setPlayerControl(socket, key, true);
            }
        });
        server.addEventListener("keyup", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socket, String key, AckRequest ackRequest) {
                gameBySocketID.get(socket.getSessionId()).setPlayerControl(socket, key, false);
            }
        });

        server.addEventListener("ping_", Number.class, new DataListener<Number>() {
            @Override
            public void onData(SocketIOClient socket, Number data, AckRequest ackRequest) {
                socket.sendEvent("pong_", data);
            }
        });


        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}