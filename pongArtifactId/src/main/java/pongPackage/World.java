// package pongPackage;

// public class World {
// constructor(game){
//     this.game = game;
//     this.BALLS = [];
//     this.PLAYERS = [];
//     this.ballsN = 0;
//     this.playersN = 0;
//     this.r = 10;
//     }

//     setPlayers(players) {
//     this.BALLS = [];
//     this.PLAYERS = [];
//     this.playersN = Object.keys(players).length;
//     this.ballsN = Math.ceil(this.playersN/2);
    
    
//     let d = Math.PI*2 / this.playersN
//     this.paddleSize = 2*Math.sin(d/2)*this.r/3
//     this.ballRadius = this.paddleSize/9;
//     let a = 0
//     for (let p of Object.values(players)) {
//         p.setPaddle(this.paddleSize, a, d, this.r)
//         this.PLAYERS.push(p);
//         a+=d
//     }

//     for (let i = 0; i < this.ballsN; i++) {
//         let ball = new Ball(0,0,this.ballRadius);
//         ball.vel.x = random(0.1,0.2);
//         ball.vel.y = random(0.1,0.2);
//         this.BALLS.push(ball);
//     }
//     }

//     getInfo() {
//     return {
//         'paddleSize': this.paddleSize,
//         'ballRadius': this.ballRadius,
//         'players': this.PLAYERS,
//         'balls': this.BALLS
//     }
//     }

//     update() {
//     let worldUpdate = {
//         balls: [],
//         players: [],
//     };
//     let scoreUpdate = [];
//     this.BALLS.forEach((b, index) => {
//         b.vel = b.vel.mult(1.001)
//         for (let i = index + 1; i < this.BALLS.length; i++) {
//         if (collDetBB(b, this.BALLS[i])) {
//             collResBB(b, this.BALLS[i]);
//             penResBB(b, this.BALLS[i]);
            
//         }
//         }

//         this.PLAYERS.forEach((p) => {
//         if (collDetBW(b, p)) {
//             collResBW(b, p);
//             penResBW(b, p);
//             b.owner = p;
//             p.score += 1;
//             scoreUpdate.push({ id: p.id, score: p.score })
//         }
//         });

//         if (b.pos.mag() > this.r) {
//         let angle = Math.atan2(b.pos.y, b.pos.x)
//         if (angle<0) angle = Math.PI*2 + angle
//         let index = angle / (Math.PI*2/this.playersN)
//         let p = this.PLAYERS[Math.floor(index)]
//         p.score -= 5
//         scoreUpdate.push({ id: p.id, score: p.score })
//         if (b.owner) {
//             b.owner.score += 5;
//             scoreUpdate.push({ id: b.owner.id, score: b.owner.score })
//             b.owner = null;
//         }
//         b.pos = new Vector(0,0)
//         b.vel = b.vel.mult(-0.7)
//         }
        
//         worldUpdate.balls.push(b.serialized())
//         b.reposition();
//     });
    
//     this.PLAYERS.forEach((p) => {
//         p.move(this.r)
//         worldUpdate.players.push(p.serialized())
//     });
//     return {worldUpdate, scoreUpdate};
//     }
// }


// function random(min, max) {
//     return Math.random() * (max - min) + min;
//   }
  
//   function closestPointBW(b, w) {
//     let wallEndToBall = b.pos.subtr(w.end);
//     if (Vector.scalar(w.unit(), wallEndToBall) > 0) {
//       return w.end;
//     }
//     let ballToWallStart = w.start.subtr(b.pos);
//     let closestDist = Vector.scalar(w.unit(), ballToWallStart);
//     if (closestDist > 0) {
//       return w.start;
//     }
//     let closestVect = w.unit().mult(closestDist);
//     return w.start.subtr(closestVect);
//   }
  
//   function collDetBB(b1, b2) {
//     if (b1.r + b2.r >= b1.pos.subtr(b2.pos).mag()) {
//       return true;
//     }
//     return false;
//   }
  
//   function collDetBW(b, w) {
//     let ballToClosest = closestPointBW(b, w).subtr(b.pos);
//     if (ballToClosest.mag() < b.r) {
//       return true;
//     }
//     return false;
//   }
  
//   function penResBB(b1, b2) {
//     let distVec = b1.pos.subtr(b2.pos);
//     let penDepth = b1.r + b2.r - distVec.mag();
//     let penRes = distVec.unit().mult(penDepth / 2);
//     b1.pos = b1.pos.add(penRes);
//     b2.pos = b2.pos.add(penRes.mult(-1));
//   }
  
//   function penResBW(b, w) {
//     let penVect = b.pos.subtr(closestPointBW(b, w));
//     b.pos = b.pos.add(penVect.unit().mult(b.r - penVect.mag()));
//   }
  
//   function collResBB(b1, b2) {
//     let normal = b1.pos.subtr(b2.pos).unit();
//     let relVel = b1.vel.subtr(b2.vel);
//     let sepVel = Vector.scalar(relVel, normal);
//     let newSepVel = -sepVel;
//     let sepVelVec = normal.mult(newSepVel);
  
//     b1.vel = b1.vel.add(sepVelVec);
//     b2.vel = b2.vel.add(sepVelVec.mult(-1));
//   }
  
//   function collResBW(b, w) {
//     let normal = b.pos.subtr(closestPointBW(b, w)).unit();
//     let sepVel = Vector.scalar(b.vel, normal);
//     let newSepVel = -sepVel;
//     let vsepDiff = sepVel - newSepVel;
//     b.vel = b.vel.add(normal.mult(-vsepDiff));
//   }