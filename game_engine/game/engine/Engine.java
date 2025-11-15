/*
 * Copyright (c) 2008 University of Szeged
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package game.engine;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.google.gson.reflect.TypeToken;

import game.engine.ui.Drawable;
import game.engine.utils.ActionTask;
import game.engine.utils.ConstructionTask;
import game.engine.utils.Pair;
import game.engine.utils.StringBufferOutputStream;
import game.engine.utils.TimeOutTask;
import game.engine.utils.Utils;

public final class Engine {
  
  private static boolean isDebug;
  private static boolean isZipFrames;
  private final double fps;
  private final boolean isDrawable;
  
  // game related objects
  private final Game<Player<Action>, Action> game;
  private final Player<Action>[] players;
  private final List<Pair<Integer, Action>>[] prevActions;
  private final long[] remainingTimes;
  private final long[][] playerRemainingTimes;
  
  // to hide standard out and error
  private static final PrintStream defaultOut = System.out;
  private static final PrintStream defaultErr = System.err;
  private static final StringBuffer sbOut = new StringBuffer();
  private static final StringBuffer sbErr = new StringBuffer();
  private static final PrintStream userOut = new PrintStream(new StringBufferOutputStream(sbOut));
  private static final PrintStream userErr = new PrintStream(new StringBufferOutputStream(sbErr));

  // for time measuring
  private static final ActionTask actionTask = new ActionTask();
  private static final ExecutorService service = Executors.newCachedThreadPool();
  
  // for logging
  private final Type logType;
  private final String ofName;
  private PrintWriter os;
  private BufferedReader is;
  private boolean isReplay = false;
  private static final String LOGEND = "LOGEND";
  private ZipOutputStream zipFrames = null;
  
  static {
    ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
  }

  /**
   * Creates an engine with the specified parameters.
   * @param fps debug parameter (frames per second)
   * @param gameClass class of game will be player
   * @param params parameters of the specified game
   * @throws Exception file IO, GSON, reflection
   */
  @SuppressWarnings("unchecked")
  public Engine(double fps, String gameClass, String[] params) throws Exception {
    long postfix = System.nanoTime() % (long)1E9;
    Engine.isZipFrames = false;
    Engine.isDebug = 0.0 != fps;
    File f = new File(gameClass);
    ofName = "gameplay_" + postfix + ".data";
    if (f.exists()) {
      // TODO: uncomment to save frames into zip
      //Engine.isZipFrames = 0.0 < fps;
      zipFrames = Engine.isZipFrames ? new ZipOutputStream(new FileOutputStream(gameClass + ".zip")) : null;
      // read game from file to replay
      is = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf8"));
      gameClass = Utils.getGson().fromJson(is.readLine(), String.class);
      params = Utils.getGson().fromJson(is.readLine(), String[].class);
      isReplay = true;
    } else if (!isDebug) {
      //create file for game replay
      os = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ofName), "utf8"), true);
      os.println(Utils.getGson().toJson(gameClass));
      os.println(Utils.getGson().toJson(params));
    }
    
    if (!isDebug) {
      System.setOut(userOut);
      System.setErr(userErr);
    }
    
    // construct game, and the game initializes itself
    game = (Game<Player<Action>, Action>) Class.forName(gameClass).getConstructor(PrintStream.class, boolean.class, String[].class).newInstance(new Object[] {defaultErr, isReplay, params});
    long timeout = game.getTimeout();
    // create type for gson logging
    logType = TypeToken.getParameterized(Pair.class, new Type[] {game.getActionClass(), Long.class}).getType();
    // construct players
    List<Pair<Constructor<? extends Player<Action>>, Object[]>> playerConstructors = game.getPlayerConstructors();
    if (playerConstructors != null) {
      List<Pair<? extends Player<Action>, Long>> playersAndTimes = new LinkedList<Pair<? extends Player<Action>, Long>>();
      for (Pair<Constructor<? extends Player<Action>>, Object[]> playerConstructor : playerConstructors) {
        Pair<? extends Player<Action>, Long> constructed = construct(timeout, playerConstructor.first, playerConstructor.second);
        if (isReplay) {
          String line = is.readLine();
          Pair<Object, Long> result = Utils.getGson().fromJson(line, TypeToken.getParameterized(Pair.class, new Type[] {Object.class, Long.class}).getType());
          constructed = new Pair<Player<Action>, Long>(constructed.first, result.second);
        } else if (!isDebug) {
          os.println(Utils.getGson().toJson(new Pair<String, Long>(playerConstructor.first.getDeclaringClass().getCanonicalName(), constructed.second)));
        }
        playersAndTimes.add(constructed);
      }
      game.setPlayers(playersAndTimes);
    }
    players = game.getPlayers();
    // collections for players to can follow the game flow
    prevActions = new List[players.length];
    remainingTimes = new long[players.length];
    playerRemainingTimes = new long[players.length][players.length];
    for (int i = 0; i < players.length; i++) {
      prevActions[i] = new LinkedList<Pair<Integer, Action>>();
      remainingTimes[i] = game.getRemainingTime(players[i]);
      for (int j = 0; j < playerRemainingTimes.length; j++) {
        playerRemainingTimes[j][i] = remainingTimes[i];
      }
    }
    
    this.isDrawable = game instanceof Drawable && 0 < fps;
    this.fps = fps < 0.0 ? -fps : fps;
    
    if (isDebug) {
      defaultOut.println("GAME: " + gameClass);
      defaultOut.println("PARAMETERS: " + Arrays.toString(params));
    }
  }

  /**
   * Loops the game and draws it, until game is finished.
   * @throws Exception file IO, replay
   */
  public void play() throws Exception {
    // GUI related variable definitions and dependency checks
    Frame gameFrame = null;
    ByteArrayOutputStream stream = null;
    int frameIdx = 0;
    
    // print table
    if (isDebug) {
      defaultOut.println(game);
      if (isDrawable) {
        Drawable drawable = (Drawable) game;
        gameFrame = drawable.getFrame();
      }
    }
    
    long elapsed;
    // play the game, to while not finished:
    // get current player from game
    // get next action from current player (sends previous actions and remaining times)
    // send back action and elapsed time to the game
    while (!game.isFinished()) {
      // get next player and its remaining play time
      Player<Action> currentPlayer = game.getNextPlayer();
      if (currentPlayer == null) {
        defaultErr.println("CURRENT PLAYER IS NULL: " + currentPlayer);
        break;
      }
      remainingTimes[currentPlayer.getColor()] = game.getRemainingTime(currentPlayer);
      for (int i = 0; i < remainingTimes.length; i++) {
        playerRemainingTimes[currentPlayer.getColor()][i] = remainingTimes[i];
      }
      
      // print player statistics
      if (isDebug) {
        defaultOut.println("CURRENT: " + currentPlayer + " SCORE: " + game.getScore(currentPlayer) + " REM.TIME: " + remainingTimes[currentPlayer.getColor()] + " ns");
      }

      Pair<Action, Long> result = null;
      List<Pair<Integer, Action>> prevAction = prevActions[currentPlayer.getColor()];
      if (isReplay) {
        // we are in replay mode, get the action from log file
        String line = is.readLine();
        if (line.equals(LOGEND)) {
          // log is finished, game has to be finished
          break;
        }
        result = Utils.getGson().fromJson(line, logType);
      } else {
        // timer task for getting action from player, runs at most the specified remaining time
        actionTask.setParams(currentPlayer, prevAction, playerRemainingTimes[currentPlayer.getColor()]);
        result = timeOutTask(actionTask, remainingTimes[currentPlayer.getColor()] + 1);
      }
      Action currentAction = result.first;
      elapsed = result.second;
      
      // clean previous actions for current player and set action for other players
      prevAction.clear();
      for (int i = 0; i < prevActions.length; i++) {
        if (i != currentPlayer.getColor()) {
          prevActions[i].add(new Pair<Integer, Action>(currentPlayer.getColor(), currentAction));
        }
      }
      
      // log current action
      if (!isReplay && !isDebug) {
        // to file
        os.println(Utils.getGson().toJson(result));
      }
      if (isDebug) {
        // to standard out
        defaultOut.println("ACTION: " + currentAction);
        defaultOut.println("ELAPSED TIME: " + elapsed + " ns");
        if (!game.isValid(currentAction)) {
          defaultOut.println("ACTION: " + currentAction + " IS NOT VALID!!!");
        }
      }

      // sets the player's action
      game.setAction(currentPlayer, currentAction, elapsed);

      // draw table
      if (isDebug) {
        // to standard out
        defaultOut.println(game);
        // to GUI
        if (isDrawable) {
          gameFrame.repaint();
          if (Engine.isZipFrames) {
            if (stream == null) {
              stream = new ByteArrayOutputStream();
            }
            BufferedImage img = new BufferedImage(gameFrame.getWidth(), gameFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
            gameFrame.paint(img.getGraphics());
            stream.reset();
            ImageIO.write(img, "png", stream);
            zipFrames.putNextEntry(new ZipEntry(String.format("frame_%08d.png", frameIdx)));
            zipFrames.write(stream.toByteArray());
            zipFrames.closeEntry();
            //System.out.println(String.format("frame_%08d.png", frameIdx));
            frameIdx ++;
          }
        }
        // sleep to get the required fps
        try {
          Thread.sleep((long)(1000.0 * 1.0 / fps));
        } catch (InterruptedException e) {
          e.printStackTrace(defaultErr);
        }
      }
    }
    
    // game finished, clean up
    service.shutdown();
    if (isDebug && isDrawable) {
      //gameApplication.close();
    }
    
    System.setOut(defaultOut);
    System.setErr(defaultErr);
    if (isReplay) {
      is.close();
    } else if (!isDebug) {
      os.println(LOGEND);
      os.close();
      defaultOut.println("logfile: " + ofName);
    }
    if (Engine.isZipFrames) {
      zipFrames.close();
    }
    
    // print final scores and remaining times
    for (int i = 0; i < players.length; i++) {
      defaultOut.println(i + " " + players[i] + " " + (players[i] == null ? i + " " : "") + game.getScore(players[i]) + " " + game.getRemainingTime(players[i]));
    }
    
  }

  /**
   * Entry point of the program.
   * @param args command line arguments
   * @throws Exception {@link Engine} construction
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("required parameters for the engine are:");
      System.err.println("\t- debug/fps      : integer debug parameter (0: no debug, 0 != : game speed (frames per sec, logs frames to zip for negative values))");
      System.err.println("\t- game class     : class of the game to be run");
      System.err.println("\t- game parameters: the parameters of the specified game");
      System.exit(1);
    }
    double fps = Double.parseDouble(args[0]);
    String gameClass = args[1];
    String[] params = Arrays.copyOfRange(args, 2, args.length);
    Engine engine = null;
    try {
      engine = new Engine(fps, gameClass, params);
    } catch (Exception e) {
      e.printStackTrace(defaultErr);
      service.shutdown();
      System.exit(-1);
    }

    try {
      engine.play();
    } catch (Exception e) {
      e.printStackTrace(defaultErr);
      service.shutdown();
      System.exit(1);
    }
    System.exit(0);
  }

  /**
   * Runs the specified task with the specified timeout and returns its result.
   * @param <R> result type
   * @param task to be run
   * @param timeout maximal running time
   * @return result of the task
   */
  public static final <R> Pair<R, Long> timeOutTask(TimeOutTask<R> task, long timeout) {
    Future<R> future = service.submit(task);
    R result = null;
    long elapsed = 0;
    try {
      result = future.get(timeout + 1, TimeUnit.NANOSECONDS);
      elapsed = task.getElapsed();
    } catch (TimeoutException e) {
      defaultOut.println("TIME HAS RUN OUT!!!");
      elapsed = timeout + 1;
    } catch (Throwable e) {
      e.printStackTrace(defaultErr);
      elapsed = timeout + 1;
    } finally {
      future.cancel(true);
    }
    if (!isDebug && (sbOut.length() > 0 || sbErr.length() > 0)) {
      elapsed = timeout + 1;
      cleanOut();
    }
    return new Pair<R, Long>(result, elapsed);
  }

  /**
   * Constructs an object by the specified constructor using the specified 
   * parameters and returns the object as the result.
   * @param <R> type of result object
   * @param timeout maximal construction time
   * @param constructor constructor of the object
   * @param params constructor parameters
   * @return result object
   */
  public static final <R> Pair<R, Long> construct(long timeout, Constructor<R> constructor, Object... params) {
    ConstructionTask<R> task = new ConstructionTask<R>();
    task.setConstructor(constructor, params);
    Pair<R, Long> result = timeOutTask(task, timeout + 1);
    return result;
  }
  
  /**
   * Prints the user's out and err channel to the standard out and err channel, 
   * respectively, and cleans the user's channels.
   */
  private static final void cleanOut() {
    defaultErr.println("Writing is forbidden!");
    defaultErr.println("USER.OUT:\n" + sbOut);
    defaultErr.println("USER.ERR:\n" + sbErr);
    sbOut.delete(0, sbOut.length());
    sbErr.delete(0, sbErr.length());
  }

}
