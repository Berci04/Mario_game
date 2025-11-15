package game.mario;

import java.awt.Frame;
import java.awt.Image;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import game.engine.Action;
import game.engine.Game;
import game.engine.ui.Drawable;
import game.engine.ui.GameFrame;
import game.engine.ui.GameObject;
import game.engine.utils.Pair;
import game.engine.utils.Utils;
import game.mario.players.DummyPlayer;
import game.mario.players.HumanPlayer;
import game.mario.players.RightUpPlayer;
import game.mario.ui.MarioCanvas;
import game.mario.utils.Mario;
import game.mario.utils.MarioState;

/**
 * A very simplified implementation of the classical Mario game.
 * Here pipes cannot be used an there are no enemies.
 * The goal is to reach the end of the map and maximize the scores (by coins, surprises and taken distance).
 * @see <a href=https://en.wikipedia.org/wiki/Super_Mario_Bros.>Super Mario Bros.</a>
 * <a href=http://julian.togelius.com/Dahlskog2014Linear.pdf>Map Generation</a>
 * <a href=https://web.archive.org/web/20130807122227/http://i276.photobucket.com/albums/kk21/jdaster64/smb_playerphysics.png>Physics</a>
 * <a href=https://www.mariowiki.com/World_1-1_(Super_Mario_Bros.)>Colors</a>
 * <a href=https://github.com/ahmetcandiroglu/Super-Mario-Bros>Images</a>
 */
public class MarioGame implements Game<MarioPlayer, Direction>, Drawable {
  /** possible map columns */
  private static final String[] MAP_ELEMTS = new String[] {
    "----#--------",
    "----#-------=",
    "--------#---=",
    "-------------",
    "------------=",
    "-----------==",
    "----------===",
    "---------====",
    "--------=====",
    "--------?---=",
    "-------======",
    "----?---#---=",
    "----?---?---=",
    "--------C---=",
    "-------C----=",
    "----------PP=",
    "---------PPP=",
    "--------PPPP="
  };
  /** weights of random selection of map columns */
  private static final int[] MAP_PROBS = new int[] {
      2,14,9,15,117,6,5,5,6,7,1,4,5,15,15,6,2,14
  };
  /** the width of the map */
  public static final int W = 100;
  /** the height of the map */
  public static final int H = 13;
  /** maximum number of iterations (actions) */
  public static final int MAX_ITER = 1000;
  
  /** represents moving right */
  public static final int RIGHT = 0;
  /** represents jump */
  public static final int UP = 1;
  /** represents moving left */
  public static final int LEFT = 2;
  /** array of possible directions */
  public static final int[] DIRECTIONS = new int[] {RIGHT, UP, LEFT};
  /** string representations of the directions */
  public static final String[] DIRECTION_STRINGS = new String[] {"R", "U", "L"};
  
  /** represents empty part of the map */
  public static final int EMPTY = 0;
  /** represents the walls on the map */
  public static final int WALL = 1;
  /** represents pipes on the map */
  public static final int PIPE = 2;
  /** represents 'surprise' (need to be hit) on the map */
  public static final int SURPRISE = 3;
  /** represents the coins on the map */
  public static final int COIN = 4;
  /** represents the Mario on the map */
  public static final int MARIO = 5;
  
  /** price value for the distance has been taken by Mario */
  public static final double DISTANCE_PRICE = 10.0;
  /** price of a coin */
  public static final double COIN_PRICE = 100.0;
  /** price of 'surprise' has been hit */
  public static final double SURPRISE_PRICE = 500.0;
  
  /** vertical acceleration - jump */
  public static final double AI = 0.7;
  /** maximal vertical speed */
  public static final double MAX_VI = 2.0;
  /** automatic vertical acceleration - falling */
  public static final double RELEASEI = 0.05;
  /** horizontal acceleration - moving */
  public static final double AJ = 0.15;
  /** maximal vertical speed */
  public static final double MAX_VJ = 0.2;
  /** vertical deceleration */
  public static final double RELEASEJ = 0.15;
  
  /** value - string tile (map parts) mapping */
  public static final HashMap<Integer, String> TILES;
  /** string - value tile (map parts) mapping */
  public static final HashMap<String, Integer> TILESMAP;
  
  static {
    TILES = new HashMap<Integer, String>();
    TILES.put(EMPTY, " ");
    TILES.put(WALL, "#");
    TILES.put(PIPE, "P");
    TILES.put(SURPRISE, "?");
    TILES.put(COIN, "C");
    TILES.put(MARIO, "M");
    TILESMAP = new HashMap<String, Integer>();
    TILESMAP.put(" ", EMPTY);
    TILESMAP.put("-", EMPTY);
    TILESMAP.put("#", WALL);
    TILESMAP.put("=", WALL);
    TILESMAP.put("P", PIPE);
    TILESMAP.put("?", SURPRISE);
    TILESMAP.put("C", COIN);
    TILESMAP.put("M", MARIO);
  }
  
  private final int startStage = 5;
  private int iter = 0;
  
  private final MarioPlayer[] players;
  private final long[] remainingTimes;
  private final double[] scores;
  private int currentPlayer;
  private boolean isFinished;
  
  private final MarioState state;
  private final MarioCanvas canvas;
  private final int canvasRatio = 3;
  
  private final PrintStream errStream;
  private final boolean isReplay;
  private final long seed;
  private final long timeout;
  private final String[] playerClassNames;
  public MarioGame(PrintStream errStream, boolean isReplay, String[] params) {
    this.errStream = errStream;
    this.isReplay = isReplay;
    if (params.length != 3) {
      errStream.println("required parameters for the game are:");
      errStream.println("\t- random seed            : controls the sequence of the random numbers");
      errStream.println("\t- timeout                : play-time for a player in milliseconds");
      errStream.println("\t- player class           : player class");
      System.exit(1);
    }
    
    //canvas = new MarioCanvas(H, W, this);
    canvas = new MarioCanvas(H, canvasRatio * H, this);
    
    seed = Long.parseLong(params[0]);
    timeout = Long.parseLong(params[1]) * 1000000;
    playerClassNames = new String[]{params[2]};
    
    players = new MarioPlayer[playerClassNames.length];
    remainingTimes = new long[players.length];
    scores = new double[players.length];
    currentPlayer = 0;
    isFinished = false;
    
    for (int i = 0; i < players.length; i++) {
      remainingTimes[i] = timeout;
      scores[i] = 0.0;
    }
    
    // generate a random map
    int sumProbs = 0;
    for (int p : MAP_PROBS) {
      sumProbs += p;
    }
    int[][] map = new int[H][W];
    Mario mario = new Mario(11, startStage);
    Random random = new Random(seed);
    // the first stages are empty
    for (int j = 0; j < 2*startStage; j++) {
      String selected = MAP_ELEMTS[4];
      for (int i = 0; i < selected.length(); i++) {
        map[i][j] = TILESMAP.get(String.valueOf(selected.charAt(i)));
      }
    }
    // the rest stages are randomly selected
    for (int j = 2*startStage; j < W; j++) {
      double rand = random.nextDouble();
      double sump = 0.0;
      int idx = -1;
      while (sump / sumProbs < rand) {
        idx++;
        sump += MAP_PROBS[idx];
      }
      String selected = MAP_ELEMTS[idx];
      for (int i = 0; i < selected.length(); i++) {
        map[i][j] = TILESMAP.get(String.valueOf(selected.charAt(i)));
      }
    }
    // put Mario on the map
    map[(int)mario.i][(int)mario.j] = MARIO;
    
    // create game state
    state = new MarioState(map, mario);
  }
  /**
   * Applies the specified direction action on the game state.
   * @param direction to be applied
   */
  public final void next(Direction direction) {
    boolean applied = state.apply(direction);
    isFinished = !applied || MAX_ITER <= iter;
    iter ++;
    scores[currentPlayer] = state.score;
  }
  @Override
  public String toString() {
    return state + ", ITER: " + iter;
  }
  /**
   * Returns a string representation of the specified map.
   * @param map to be printed
   * @return string representation of the map
   */
  public static final String map2str(int[][] map) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length; j++) {
        sb.append(TILES.get(map[i][j]));
      }
      sb.append("\n");
    }
    return sb.toString();
  }
  @Override
  public long getTimeout() {
    return timeout;
  }
  @Override
  public List<Pair<Constructor<? extends MarioPlayer>, Object[]>> getPlayerConstructors() throws Exception {
    List<Pair<Constructor<? extends MarioPlayer>, Object[]>> result = new LinkedList<Pair<Constructor<? extends MarioPlayer>, Object[]>>();
    for (int idx = 0; idx < playerClassNames.length; idx++) {
      Class<? extends MarioPlayer> clazz = Class.forName(DummyPlayer.class.getName()).asSubclass(MarioPlayer.class);
      if (isReplay) {
        errStream.println("Game is in replay mode, Player: " + idx + " is the DummyPlayer, but was: " + playerClassNames[idx]);
      } else {
        clazz = Class.forName(playerClassNames[idx]).asSubclass(MarioPlayer.class);
      }
      Random r = new Random(seed);
      result.add(new Pair<Constructor<? extends MarioPlayer>, Object[]>(clazz.getConstructor(int.class, Random.class, MarioState.class), new Object[] {idx, r, new MarioState(state)}));
    }
    return result;
  }
  @Override
  public void setPlayers(List<Pair<? extends MarioPlayer, Long>> playersAndTimes) throws Exception {
    int idx = 0;
    for (Pair<? extends MarioPlayer, Long> playerAndTime : playersAndTimes) {
      players[idx] = playerAndTime.first;
      remainingTimes[idx] -= playerAndTime.second;
      if (players[idx] instanceof HumanPlayer) {
        remainingTimes[idx] = Long.MAX_VALUE - playerAndTime.second - 10;
      }
      
      // check color hacking
      if (players[idx] != null && players[idx].color != idx) {
        int color = players[idx].color;
        remainingTimes[idx] = 0;
        Field field = MarioPlayer.class.getDeclaredField("color");
        field.setAccessible(true);
        field.set(players[idx], idx);
        field.setAccessible(false);
        errStream.println("Illegal color (" + color + ") was set for player: " + players[idx]);
      }
      idx ++;
    }
    currentPlayer = 0;
  }
  @Override
  public MarioPlayer[] getPlayers() {
    return players;
  }
  @Override
  public MarioPlayer getNextPlayer() {
    return players[currentPlayer];
  }
  @Override
  public boolean isValid(Direction action) {
    return action == null || action.direction == RIGHT || action.direction == UP || action.direction == LEFT;
  }
  @Override
  public void setAction(MarioPlayer player, Direction action, long time) {
    if (!isValid(action)) {
      action = null;
    }
    remainingTimes[currentPlayer] -= time;
    if (remainingTimes[currentPlayer] <= 0) {
      errStream.println("GAME OVER!");
      isFinished = true;
      return;
    }
    next(action);
  }
  @Override
  public long getRemainingTime(MarioPlayer player) {
    return remainingTimes[player.color];
  }
  @Override
  public boolean isFinished() {
    return isFinished;
  }
  @Override
  public double getScore(MarioPlayer player) {
    return scores[player.color];
  }
  @Override
  public Class<? extends Action> getActionClass() {
    return Direction.class;
  }
  @Override
  public Frame getFrame() {
    String iconPath = "/game/engine/ui/resources/icon-game.png";
    try {
      Clip clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(Utils.class.getResource("/game/mario/ui/resources/main_theme.wav")));
      clip.start();
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
    return new GameFrame("Mario", iconPath, canvas);
  }
  @Override
  public List<GameObject> getGameObjects() {
    Image[] images = Utils.getImages("/game/mario/ui/resources/", new String[]{
        "100_bg",
        "brick", "pipe", "coin", "surprise",
        "mario_r", "mario_l",
    });
    LinkedList<GameObject> gos = new LinkedList<GameObject>();
    gos.add(new GameObject(0-iter, 0, state.map[0].length * canvas.multiplier, (state.map.length-1) * canvas.multiplier, images[0]));
    for (int i = 0; i < state.map.length; i++) {
      for (int j = 0; j < canvasRatio*state.map.length; j++) {
        int offset = Math.min(Math.max(0, (int)(state.mario.j - canvasRatio*state.map.length/2.0)), W-canvasRatio*state.map.length-1);
        if (state.map[i][j+offset] == WALL) {
          gos.add(new GameObject(canvas.multiplier*j, canvas.multiplier*i, canvas.multiplier, canvas.multiplier, images[1]));
        }
        if (state.map[i][j+offset] == PIPE) {
          gos.add(new GameObject(canvas.multiplier*j, canvas.multiplier*i, canvas.multiplier, canvas.multiplier, images[2]));
        }
        if (state.map[i][j+offset] == SURPRISE) {
          gos.add(new GameObject(canvas.multiplier*j, canvas.multiplier*i, canvas.multiplier, canvas.multiplier, images[4]));
        }
        if (state.map[i][j+offset] == COIN) {
          gos.add(new GameObject(canvas.multiplier*j, canvas.multiplier*i, canvas.multiplier, canvas.multiplier, images[3]));
        }
        gos.add(new GameObject((int)(canvas.multiplier*(state.mario.j-offset)), (int)(canvas.multiplier*state.mario.i), canvas.multiplier, canvas.multiplier,  state.mario.vj < 0 ? images[6] : images[5]));
      }
    }
    return gos;
  }
  /**
   * Only for testing
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    long seed = System.nanoTime();
    MarioGame game = new MarioGame(System.err, false, new String[]{
        "" + seed,
        "1000",
        "game.mario.players.RightUpPlayer"});
    Random r = new Random(seed);
    MarioPlayer player = new RightUpPlayer(0, r, game.state);
    while (!game.isFinished) {
      //game.next(new Direction(RIGHT));
      game.next(player.getDirection(0));
      System.out.println(game.toString());
      Thread.sleep(16);
    }
  }
  
}