package pongPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class World {
    private ArrayList<Ball> BALLS = new ArrayList<>();
    private ArrayList<Player> PLAYERS = new ArrayList<>();
    private double r = 10;
    private int ballsN = 0;
    private int playersN = 0;
    private double paddleSize = 0;
    private double ballRadius = 0;

    public World(){}

    public void setPlayers(HashMap<UUID, Player> players) {
        BALLS.clear();
        PLAYERS.clear();
        playersN = players.size();
        ballsN = (int)Math.ceil(playersN/2f) ;
        
        
        double step = Math.PI*2 / playersN;
        paddleSize = 2*Math.sin(step/2)*r/3;
        ballRadius = paddleSize/9;
        double angle = 0;
        for (UUID playerID : players.keySet()) {
            players.get(playerID).setPaddle(paddleSize, angle, step, r);
            PLAYERS.add(players.get(playerID));
            angle+=step;
        }

        for (int i = 0; i < ballsN; i++) {
            Ball ball = new Ball(0,0,ballRadius);
            ball.vel.x = Physics.random(0.1,0.2);
            ball.vel.y = Physics.random(0.1,0.2);
            BALLS.add(ball);
        }
    }

    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> info = new HashMap<>();
        info.put("paddleSize", paddleSize);
        info.put("ballRadius", ballRadius);
        info.put("players", PLAYERS);
        info.put("balls", BALLS);
        return info;
    }

    private HashMap<String, Object> getScoreObject(Player p) {
        HashMap<String, Object> newScore = new HashMap<>();
        newScore.put("id", p.id);
        newScore.put("score", p.score);
        return newScore;
    }

    public HashMap<String, Object> update() {
        HashMap<String, ArrayList<Vector>> worldUpdate = new HashMap<>();
        worldUpdate.put("balls", new ArrayList<Vector>());
        worldUpdate.put("players", new ArrayList<Vector>());

        ArrayList<HashMap<String, Object>> scoreUpdate = new ArrayList<>();
        
        for (int bi = 0; bi < ballsN; bi++) {
            BALLS.get(bi).vel = BALLS.get(bi).vel.mult(1.001);
			for (int i = bi + 1; i < BALLS.size(); i++) {
                if (Physics.collDetBB(BALLS.get(bi), BALLS.get(i))) {
                    Physics.collResBB(BALLS.get(bi), BALLS.get(i));
                    Physics.penResBB(BALLS.get(bi), BALLS.get(i));
                }
            }



            for (int pi = 0; pi < playersN; pi++) {
                if (Physics.collDetBW(BALLS.get(bi), PLAYERS.get(pi))) {
                    Physics.collResBW(BALLS.get(bi), PLAYERS.get(pi));
                    Physics.penResBW(BALLS.get(bi), PLAYERS.get(pi));
                    BALLS.get(bi).owner = PLAYERS.get(pi);
                    PLAYERS.get(pi).score += 1;
                    scoreUpdate.add(getScoreObject(PLAYERS.get(pi)));
                }
            }

            if (BALLS.get(bi).pos.mag() > r) {
                double angle = Math.atan2(BALLS.get(bi).pos.y, BALLS.get(bi).pos.x);
                if (angle<0) angle = Math.PI*2 + angle;
                double index = angle / (Math.PI*2/playersN);
                Player p = PLAYERS.get((int)Math.floor(index));
                
                p.score -= 5;
                scoreUpdate.add(getScoreObject(p));
                
                if (BALLS.get(bi).owner != null) {
                    BALLS.get(bi).owner.score += 5;
                    scoreUpdate.add(getScoreObject(BALLS.get(bi).owner));
                    BALLS.get(bi).owner = null;
                }
                BALLS.get(bi).pos = new Vector(0,0);
                BALLS.get(bi).vel = BALLS.get(bi).vel.mult(-0.7);
            }
            
            worldUpdate.get("balls").add(BALLS.get(bi).pos);
            BALLS.get(bi).reposition();
        }
        
        for (Player player : PLAYERS) {
            player.move(r);
            worldUpdate.get("players").add(player.pos);
        }

        HashMap<String, Object> ret = new HashMap<>();
        ret.put("worldUpdate", worldUpdate);
        ret.put("scoreUpdate", scoreUpdate);
        
        return ret;
    }
}