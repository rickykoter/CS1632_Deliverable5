import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Pinning tests for the SlowLifeGUI program that tests methods from
 * both the MainPanel class and Cell class where seen fit.
 * These tests are compatible with both the original and refactored code
 * without modification.
 *
 * Created by Richard Kotermanski
 */
public class PinningTests {
    private MainPanel mp;

    @Before
    public void setUp() throws Exception {
        mp = new MainPanel(5);
    }

    /**
     * With all cells dead in the grid, run continuously for 2 seconds
     * to ensure final state is reached (runContinous()) before stopping stop()
     * via a separate thread. The final state of the grid should be all dead (false).
     * @throws Exception
     */
    @Test
    public void runContinuousTestAllFalse() throws Exception {
        Cell[][] f = new Cell[5][5];
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] = new Cell(false);
            }
        }
        mp.setCells(f);
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                mp.stop();
            }
        }, 2, TimeUnit.SECONDS);

        mp.runContinuous();


        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if(c[i][j]) fail();
            }
        }
    }


    /**
     * With only the 3 cells in the top left corner alive, run continuously for 2 seconds
     * to ensure final state is reached (runContinous()) before stopping stop()
     * via a separate thread. The final state of the grid should be only the 4 cells
     * in the top, left corner forming a square.
     * @throws Exception
     */
    @Test
    public void runContinuousTestCornerAlive() throws Exception {
        Cell[][] f = new Cell[5][5];

        /*
        Pattern of starting grid:
        XX...
        X....
        .....
        .....
        .....
         */
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if((i == 0 && (j == 0 || j == 1)) || (i== 1 && j == 0)){
                    f[i][j] = new Cell(true);
                } else {
                    f[i][j] = new Cell(false);
                }
            }
        }
        mp.setCells(f);
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                mp.stop();
            }
        }, 2, TimeUnit.SECONDS);
        mp.runContinuous();

        /*
        Pattern of resulting grid:
        XX...
        XX...
        .....
        .....
        .....
         */
        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if((i == 0 && (j == 0 || j == 1)) || (i == 1 && (j == 0 || j == 1))){
                    if(!c[i][j]) fail();
                } else {
                    if(c[i][j]) fail();
                }
            }
        }
    }


    /**
     * With only the 4 cells forming a plus shape in the center of the grid alive, run continuously for 2 seconds
     * to ensure final state is reached (runContinous()) before stopping stop()
     * via a separate thread. The final state of the grid should be all dead(false) due to cell interactions that wrap
     * around the grid.
     * @throws Exception
     */
    @Test
    public void runContinuousTestPlusShapeAlive() throws Exception {
        Cell[][] f = new Cell[5][5];

        /*
        Pattern of starting grid:
        .....
        ..X..
        .XXX.
        ..X..
        .....
         */
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if((i == 2 && (j == 1 || j == 2 || j == 3)|| (i==1 && j == 2) || (i == 3 && j == 2))){
                    f[i][j] = new Cell(true);
                } else {
                    f[i][j] = new Cell(false);
                }
            }
        }
        mp.setCells(f);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                mp.stop();
            }
        }, 2, TimeUnit.SECONDS);
        mp.runContinuous();

        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if(c[i][j]) fail();
            }
        }
    }


    /**
     * The convertToInt function returns the integer passed in when
     * the input parameter is greater than or equal to zero, so if zero
     * is passed in, zero should be returned.
     */
    @Test
    public void convertToIntTestZero(){
        Method method;

        try {
            Class[] argTypes = new Class[] { int.class };
            method = MainPanel.class.getDeclaredMethod("convertToInt", argTypes);
            method.setAccessible(true);
            MainPanel mp2 = new MainPanel(15);
            Object returnValue = method.invoke(mp2, 0);
            int result = ((Integer) returnValue).intValue();

            assertEquals(0, result);
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }

    }

    /**
     * The convertToInt function returns the integer passed in when
     * the input parameter is greater than or equal to zero, so if 2
     * is passed in, 2 should be returned.
     */
    @Test
    public void convertToIntTestGreaterThanZero(){
        Method method;

        try {
            Class[] argTypes = new Class[] { int.class };
            method = MainPanel.class.getDeclaredMethod("convertToInt", argTypes);
            method.setAccessible(true);
            MainPanel mp2 = new MainPanel(15);
            Object returnValue = method.invoke(mp2, 2);
            int result = ((Integer) returnValue).intValue();

            assertEquals(2, result);
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    /**
     * The convertToInt function returns the integer passed in when
     * the input parameter is greater than or equal to zero, so if a negative
     * value (Integer.MIN_VALUE) is passed in, an exception should occur
     * (NumberFormatException).
     */
    @Test
    public void convertToIntTestNegative(){
        Method method;

        try {
            Class[] argTypes = new Class[] { int.class };
            method = MainPanel.class.getDeclaredMethod("convertToInt", argTypes);
            method.setAccessible(true);
            MainPanel mp2 = new MainPanel(15);
            Object returnValue = method.invoke(mp2, Integer.MIN_VALUE);
            int result = ((Integer) returnValue).intValue();

            fail("No number format exception occurred!");
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            //passes
            return;
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    /**
     * The convertToInt function returns the integer passed in when
     * the input parameter is greater than or equal to zero, so if Integer.MAX_VALUE
     * is passed in, Integer.MAX_VALUE should be returned.
     */
    @Test
    public void convertToIntTestMaxInt(){
        Method method;

        try {
            Class[] argTypes = new Class[] { int.class };
            method = MainPanel.class.getDeclaredMethod("convertToInt", argTypes);
            method.setAccessible(true);
            MainPanel mp2 = new MainPanel(15);
            Object returnValue = method.invoke(mp2, Integer.MAX_VALUE);
            int result = ((Integer) returnValue).intValue();

            assertEquals(Integer.MAX_VALUE, result);
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }


    /**
     * With some cells alive and some dead on the board, the backup function should store the current state of the grid
     * and not be changed by iterating the board to a new state(run()); After running once, the _backupCells field
     * should be that of the previous, starting grid with some true (alive) and false (dead).
     * @throws Exception
     */
    @Test
    public void backupTestPattern() throws IllegalAccessException {
        mp = new MainPanel(5);
        Cell[][] f = new Cell[5][5];

        /*
        Pattern of starting and resulting grid:
        .....
        ..X..
        .XXX.
        .X...
        .....
        */
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if((i == 2 && (j == 1 || j == 2 || j == 3))|| (i==1 && j == 2) || (i == 3 && j == 1)){
                    f[i][j] = new Cell(true);
                } else {
                    f[i][j] = new Cell(false);
                }
            }
        }
        mp.setCells(f);

        mp.backup();
        mp.run();

        Field field = null;
        try {
            field = MainPanel.class.getDeclaredField("_backupCells");
        } catch (NoSuchFieldException e) {
            fail();
        }
        field.setAccessible(true);
        try {
            boolean[][] c = (boolean[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if((i == 2 && (j == 1 || j == 2 || j == 3))|| (i==1 && j == 2) || (i == 3 && j == 1)){
                        if(!c[i][j]) fail();
                    } else {
                        if(c[i][j]) fail();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            fail();
        } catch (ClassCastException e){
            Cell[][] c = (Cell[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if((i == 2 && (j == 1 || j == 2 || j == 3))|| (i==1 && j == 2) || (i == 3 && j == 1)){
                        if(!c[i][j].getAlive()) fail();
                    } else {
                        if(c[i][j].getAlive()) fail();
                    }
                }
            }
        }
    }


    /**
     * With all cells alive on the board, the backup function should store the current state of the grid
     * and not be changed by iterating the board to a new state(run()); After running once, the _backupCells field
     * should be that of the previous, starting grid with all true values(alive).
     * @throws Exception
     */
    @Test
    public void backupTestAllTrue() throws IllegalAccessException {
        mp = new MainPanel(5);
        Cell[][] f = new Cell[5][5];
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] = new Cell(true);
            }
        }


        mp.setCells(f);
        mp.backup();
        mp.run();

        Field field = null;
        try {
            field = MainPanel.class.getDeclaredField("_backupCells");
        } catch (NoSuchFieldException e) {
            fail();
        }
        field.setAccessible(true);
        try {
            boolean[][] c = (boolean[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if(!c[i][j]) fail();
                }
            }
        } catch (IllegalAccessException e) {
            fail();
        } catch (ClassCastException e){
            Cell[][] c = (Cell[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if(!c[i][j].getAlive()) fail();
                }
            }
        }
    }


    /**
     * With all cells dead on the board, the backup function should store the current state of the grid
     * and not be changed by iterating the board to a new state(run()); After running once, the _backupCells field
     * should be that of the previous, starting grid with all false values(dead).
     * @throws Exception
     */
    @Test
    public void backupTestAllFalse() throws IllegalAccessException {
        mp = new MainPanel(5);

        mp.backup();
        mp.run();

        Field field = null;
        try {
            field = MainPanel.class.getDeclaredField("_backupCells");
        } catch (NoSuchFieldException e) {
            fail();
        }
        field.setAccessible(true);
        try {
            boolean[][] c = (boolean[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if(c[i][j]) fail();
                }
            }
        } catch (IllegalAccessException e) {
            fail();
        } catch (ClassCastException e){
            Cell[][] c = (Cell[][]) field.get(mp);
            for(int i = 0; i < c.length; i++){
                for(int j = 0; j < c[i].length; j++){
                    if(c[i][j].getAlive()) fail();
                }
            }
        }

    }


    /**
     * With some cells alive and some dead on the board and after iterating the board to a new state(run()) once,
     * the undo function should revert the grid to the original, backed up state of the grid.
     * @throws Exception
     */
    @Test
    public void undoTestPattern() throws Exception {
        mp = new MainPanel(5);
        Cell[][] f = new Cell[5][5];
        /*
        Pattern of starting and resulting grid:
        .....
        ..X..
        .XXX.
        .X...
        .....
        */
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                if((i == 2 && (j == 1 || j == 2 || j == 3))|| (i==1 && j == 2) || (i == 3 && j == 1)){
                    f[i][j] = new Cell(true);
                } else {
                    f[i][j] = new Cell(false);
                }
            }
        }
        mp.setCells(f);
        mp.run();
        mp.undo();

        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if((i == 2 && (j == 1 || j == 2 || j == 3))|| (i==1 && j == 2) || (i == 3 && j == 1)){
                    if(!c[i][j]) fail();
                } else {
                    if(c[i][j]) fail();
                }
            }
        }
    }


    /**
     * With all cells originally alive on the board and after iterating the board to a new state(run()) once,
     * the undo function should revert the grid to the previous, backed up state where all cells are alive (true).
     * @throws Exception
     */
    @Test
    public void updateTestAllTrue() {
        mp = new MainPanel(5);
        Cell[][] f = new Cell[5][5];
        for(int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                f[i][j] = new Cell(true);
            }
        }


        mp.setCells(f);
        mp.run();
        mp.undo();

        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if(!c[i][j]) fail();
            }
        }
    }


    /**
     * With all cells originally dead on the board and after iterating the board to a new state(run()) once,
     * the undo function should revert the grid to the previous, backed up state where all cells are dead (false).
     * @throws Exception
     */
    @Test
    public void undoTestAllFalse() throws Exception {
        mp = new MainPanel(5);

        mp.run();
        mp.undo();

        boolean[][] c = mp.convertToBoolean(mp.getCells());
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].length; j++){
                if(c[i][j]) fail();
            }
        }
    }
    /**
     * Setting a cell alive with true as a parameter for setAlive should
     * result in the text of the cell being an 'X'.
     */
    @Test
    public void cellSetAliveTestTrue(){
        Cell c = new Cell();
        c.setAlive(true);
        assertEquals("X", c.getText());
    }


    /**
     * Setting a cell alive with false as a parameter for setAlive should
     * result in the text of the cell being a space character: ' '.
     */
    @Test
    public void cellSetAliveTestFalse(){
        Cell c = new Cell();
        c.setAlive(false);
        assertEquals(" ", c.getText());
    }


    /**
     * Setting a cell alive and dead with true or false, respectively, as a parameters for setAlive in the sequence
     * alive, dead, then alive results in the text of the cell being an 'X' at the end of the sequence.
     */
    @Test
    public void cellSetAliveTestSequence(){
        Cell c = new Cell(); //starts false
        c.setAlive(true);
        c.setAlive(false);
        c.setAlive(true);

        assertEquals("X", c.getText());
    }


    /**
     * The getAlive function returns false if the default constructor is used and
     * the cell's setAlive function has not been called.
     */
    @Test
    public void  cellGetAliveTestDefault(){
        Cell c = new Cell();
        assertFalse(c.getAlive());
    }


    /**
     * The getAlive function returns false if false is used as a parameter for the constructor is used and
     * the cell's setAlive function has not been called.
     */
    @Test
    public void  cellGetAliveTestFalse(){
        Cell c = new Cell(false);
        assertFalse(c.getAlive());
    }


    /**
     * The getAlive function returns true if true is used as a parameter for the constructor is used and
     * the cell's setAlive function has not been called.
     */
    @Test
    public void  cellGetAliveTestTrue(){
        Cell c = new Cell(true);
        assertTrue(c.getAlive());
    }

    /**
     * The getAlive function returns false if the cell's setAlive function is called most recently with false as a
     * parameter even if the constructor was given true as a parameter.
     */
    @Test
    public void  cellGetAliveTestSettingFalse(){
        Cell c = new Cell(true);
        c.setAlive(false);
        assertFalse(c.getAlive());
    }


    /**
     * The toString method returns the string "X" when a cell is set alive
     * by the constructor.
     */
    @Test
    public void cellToStringTestAlive(){
        Cell c = new Cell(true);
        assertEquals("X", c.toString());
    }


    /**
     * The toString method returns the string "." when a cell is set dead
     * by the constructor.
     */
    @Test
    public void cellToStringTestDead(){
        Cell c = new Cell(false);
        assertEquals(".", c.toString());
    }


    /**
     * The toString method returns the string "X" when a cell is first set dead
     * by the constructor then changed to alive by the setAlive function.
     */
    @Test
    public void cellToStringTestAliveToDead(){
        Cell c = new Cell(false);
        c.setAlive(true);
        assertEquals("X", c.toString());
    }


    /**
     * The toString method returns the string "." when a cell is first set alive
     * by the constructor then changed to dead by the setAlive function.
     */
    @Test
    public void cellToStringTestDeadToAlive(){
        Cell c = new Cell(true);
        c.setAlive(false);
        assertEquals(".", c.toString());
    }

}