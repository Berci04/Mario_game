///Nicknevem, Vezeteknev.Keresztnev@stud.u-szeged.hu
import java.util.Random;
import game.mario.Direction;
import game.mario.MarioGame;
import game.mario.MarioPlayer;
import game.mario.utils.MarioState;

public class Agent extends MarioPlayer {

    // Keresési mélység (Time-safe érték)
    private static final int MAX_SEARCH_DEPTH = 9;

    // --- ÚJ VÁLTOZÓK A BERAGADÁS ELLEN ---
    private double lastX = 0;       // Hol voltunk az előző körben?
    private int stuckCounter = 0;   // Hány köre állunk egy helyben?

    public Agent(int color, Random random, MarioState state) {
        super(color, random, state);
    }

    @Override
    public Direction getDirection(long remainingTime) {
        // 1. IDŐZÍTÉS
        long startTime_ns = System.nanoTime();
        long timeBudget_ns = Math.max(5_000_000, remainingTime / 100);
        long deadline_ns = startTime_ns + timeBudget_ns;

        // 2. BERAGADÁS ÉRZÉKELÉSE (ANTI-FREEZE)
        // Ha a vízszintes pozíció (j) alig változott az előző körhöz képest...
        if (Math.abs(state.mario.j - lastX) < 0.5) {
            stuckCounter++;
        } else {
            stuckCounter = 0; // Ha mozgunk, nullázzuk a számlálót
        }
        lastX = state.mario.j;

        // Ha már 15 képkocka (kb. negyed másodperc) óta egy helyben toporgunk:
        // KÉNYSZERÍTETT MENEKÜLÉS!
        if (stuckCounter > 15) {
            // Ha földön vagyunk, UGRÁS!
            if (!state.isInAir) {
                return new Direction(MarioGame.UP);
            }
            // Ha levegőben vagyunk, akkor JOBBRA (hogy átvigye a lendületet)
            else {
                return new Direction(MarioGame.RIGHT);
            }
        }

        // --- NORMÁL KERESÉS (HA NINCS BERAGADVA) ---

        // Vészhelyzet: ha nagyon kevés az idő, csak fussunk
        if (remainingTime < 2_000_000) {
            return new Direction(MarioGame.RIGHT);
        }

        Direction bestDirection = null;
        double maxHeuristic = Double.NEGATIVE_INFINITY;

        Direction[] possibleMoves = new Direction[] {
                new Direction(MarioGame.RIGHT),
                new Direction(MarioGame.UP),
                new Direction(MarioGame.LEFT),
                null
        };

        for (Direction dir : possibleMoves) {
            MarioState childState = new MarioState(state);

            if (childState.apply(dir)) {
                double value = simulate(childState, MAX_SEARCH_DEPTH - 1, deadline_ns);

                // Kis véletlen zaj, hogy egyenlő pontszámnál ne mindig ugyanazt válassza
                // Ez is segít kimozdulni a holtpontról
                value += random.nextDouble() * 0.1;

                if (value > maxHeuristic) {
                    maxHeuristic = value;
                    bestDirection = dir;
                }
            }
        }

        return (bestDirection != null) ? bestDirection : new Direction(MarioGame.RIGHT);
    }

    private double simulate(MarioState currentState, int depth, long deadline_ns) {
        if (depth == 0 || System.nanoTime() > deadline_ns) {
            return evaluateState(currentState);
        }

        // Egyszerűsített Nyaláb-keresés (csak a logikus irányokba)
        Direction[] moves = new Direction[] {
                new Direction(MarioGame.RIGHT),
                new Direction(MarioGame.UP)
        };

        double bestVal = Double.NEGATIVE_INFINITY;

        for (Direction dir : moves) {
            MarioState nextState = new MarioState(currentState);
            boolean alive = nextState.apply(dir);

            double val;
            if (!alive) {
                val = -1000000; // Halál büntetése
            } else {
                val = simulate(nextState, depth - 1, deadline_ns);
            }

            if (val > bestVal) bestVal = val;
        }
        return bestVal;
    }

    // --- JAVÍTOTT HEURISZTIKA ---
    private double evaluateState(MarioState s) {
        double heuristic = 0;

        // 1. Távolság (Még mindig a legfontosabb)
        heuristic += s.mario.j * 10.0;

        // 2. SEBESSÉG BÜNTETÉS (Ez a kulcs a fagyás ellen!)
        // Ha a földön van és nem mozog vízszintesen, az nagyon rossz!
        if (!s.isInAir && Math.abs(s.mario.vj) < 1.0) {
            heuristic -= 500.0; // Büntetés a tétlenségért
        }

        // Jutalmazzuk a sebességet
        heuristic += s.mario.vj * 20.0;

        // 3. Pontszám
        heuristic += s.score * 0.5;

        // 4. Magasság
        // A pályán lévő lyukak miatt jobb nem a legalsó szinten lenni
        if (s.mario.i > 12) {
            heuristic -= 200.0; // Veszélyzóna
        }

        return heuristic;
    }
}