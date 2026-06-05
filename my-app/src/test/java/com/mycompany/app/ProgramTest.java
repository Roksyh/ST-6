package com.mycompany.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

class ProgramTest {

    @BeforeAll
    static void nastroitHeadlessRezhim() {
        System.setProperty("java.awt.headless", "true");
    }

    static String zahvatitVyvod(Runnable r) {
        var buf = new ByteArrayOutputStream();
        var old = System.out;
        System.setOut(new PrintStream(buf));
        try { r.run(); } finally { System.setOut(old); }
        return buf.toString();
    }

    static void bezVyvoda(Runnable r) {
        zahvatitVyvod(r);
    }

    static int intBezVyvoda(java.util.function.IntSupplier s) {
        var buf = new ByteArrayOutputStream();
        var old = System.out;
        System.setOut(new PrintStream(buf));
        try { return s.getAsInt(); } finally { System.setOut(old); }
    }

    @Test
    void novayaIgraImeetSostoyaniePlaying() {
        var game = new Game();
        assertEquals(State.PLAYING, game.state);
    }

    @Test
    void novayaIgraImeetPustuiuDosku() {
        var game = new Game();
        for (char c : game.board) assertEquals(' ', c);
    }

    @Test
    void pervyiIgrokEtoXVtoroiEtoO() {
        var game = new Game();
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);
    }

    @Test
    void xVyigryvaetNaStroka1() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(new char[]{' ',' ',' ','X','X','X',' ',' ',' '}));
    }

    @Test
    void xVyigryvaetNaStroka2() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(new char[]{' ',' ',' ',' ',' ',' ','X','X','X'}));
    }

    @Test
    void xVyigryvaetNaStolbets2() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(new char[]{' ',' ','X',' ',' ','X',' ',' ','X'}));
    }

    @Test
    void xVyigryvaetPoObratnoyDiagonali() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(new char[]{' ',' ','X',' ','X',' ','X',' ',' '}));
    }

    @Test
    void oVyigryvaetNaStroka0() {
        var game = new Game();
        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(new char[]{'O','O','O',' ',' ',' ',' ',' ',' '}));
    }

    @Test
    void oVyigryvaetNaStroka1() {
        var game = new Game();
        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(new char[]{' ',' ',' ','O','O','O',' ',' ',' '}));
    }

    @Test
    void oVyigryvaetNaStolbets1() {
        var game = new Game();
        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(new char[]{' ','O',' ',' ','O',' ',' ','O',' '}));
    }

    @Test
    void polnayaDoskaEtoNichya() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.DRAW, game.checkState(new char[]{'X','O','X','O','O','X','X','X','O'}));
    }

    @Test
    void doskaWithPustymPolemEtoPlaying() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(State.PLAYING, game.checkState(new char[]{'X','O',' ','O',' ','X','X',' ','O'}));
    }

    @Test
    void vozmozhnyeHodyNakhodiatPustyeKletki() {
        var game = new Game();
        var moves = new ArrayList<Integer>();
        game.generateMoves(new char[]{' ','X','X','O',' ','O','X',' ','O'}, moves);
        assertEquals(Arrays.asList(0, 4, 7), moves);
    }

    @Test
    void vozmozhnyeHodyNaPustoyDoskeRavno9() {
        var game = new Game();
        var moves = new ArrayList<Integer>();
        game.generateMoves(game.board, moves);
        assertEquals(9, moves.size());
    }

    @Test
    void vozmozhnyeHodyNaPolnoyDoskeRavno0() {
        var game = new Game();
        var moves = new ArrayList<Integer>();
        game.generateMoves(new char[]{'X','O','X','O','O','X','X','X','O'}, moves);
        assertEquals(0, moves.size());
    }

    @Test
    void pobeditelPoluchaetPolozhitelnyyInf() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(Game.INF, game.evaluatePosition(
            new char[]{' ',' ',' ','X','X','X','O','O',' '}, game.player1));
    }

    @Test
    void proigravshiyPoluchaetOtritsatelnyyInf() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(-Game.INF, game.evaluatePosition(
            new char[]{' ',' ',' ','X','X','X','O','O',' '}, game.player2));
    }

    @Test
    void nichyaPoluchaetNol() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(0, game.evaluatePosition(
            new char[]{'X','O','X','O','O','X','X','X','O'}, game.player1));
    }

    @Test
    void prodolzhayushcheesyaPolozhenieDaetMinusOdin() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(-1, game.evaluatePosition(
            new char[]{' ','X',' ',' ',' ',' ',' ',' ',' '}, game.player1));
    }

    @Test
    void minimaxNakhoditPobednyiHodDlyaX() {
        var game = new Game();
        char[] board = {'X',' ','X','O','O',' ',' ',' ',' '};
        int move = intBezVyvoda(() -> game.MiniMax(board, game.player1));
        assertEquals(2, move);
    }

    @Test
    void minimaxNeIzmenyaetIsxodnuiuDosku() {
        var game = new Game();
        char[] board = {'X',' ',' ',' ','O',' ',' ',' ',' '};
        char[] copy = board.clone();
        bezVyvoda(() -> game.MiniMax(board, game.player1));
        assertArrayEquals(copy, board);
    }

    @Test
    void minimaxSbrasyvaetQPosleZaversheniya() {
        var game = new Game();
        char[] board = {'X','O','X','O',' ','X','O','X','O'};
        bezVyvoda(() -> game.MiniMax(board, game.player1));
        assertEquals(0, game.q);
    }

    @Test
    void minMoveNaPobedeDlyaXReturnsPolozhitelnyyInf() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(Game.INF, game.MinMove(
            new char[]{' ',' ',' ','X','X','X','O','O',' '}, game.player1));
    }

    @Test
    void minMoveNaPobedeDlyaOReturnsOtritsatelnyyInf() {
        var game = new Game();
        game.symbol = 'O';
        assertEquals(-Game.INF, game.MinMove(
            new char[]{' ',' ',' ','O','O','O','X','X',' '}, game.player1));
    }

    @Test
    void maxMoveNaPobedeDlyaXReturnsPolozhitelnyyInf() {
        var game = new Game();
        game.symbol = 'X';
        assertEquals(Game.INF, game.MaxMove(
            new char[]{' ',' ',' ','X','X','X','O','O',' '}, game.player1));
    }

    @Test
    void maxMoveNaPobedeDlyaOReturnsOtritsatelnyyInf() {
        var game = new Game();
        game.symbol = 'O';
        assertEquals(-Game.INF, game.MaxMove(
            new char[]{' ',' ',' ','O','O','O','X','X',' '}, game.player1));
    }

    @Test
    void kletkaImeetPravilnyeNomerStrokiStolbets() {
        var cell = new TicTacToeCell(4, 2, 0);
        assertEquals(4, cell.getNum());
        assertEquals(0, cell.getRow());
        assertEquals(2, cell.getCol());
    }

    @Test
    void novayaKletkaImeetPustoiMarker() {
        assertEquals(' ', new TicTacToeCell(1, 0, 1).getMarker());
    }

    @Test
    void novayaKletkaAktivna() {
        assertTrue(new TicTacToeCell(0, 0, 0).isEnabled());
    }

    @Test
    void setMarkerObnovlyaetZnakVKletke() {
        var cell = new TicTacToeCell(3, 1, 1);
        cell.setMarker("X");
        assertEquals('X', cell.getMarker());
        assertEquals("X", cell.getText());
    }

    @Test
    void setMarkerDeaktiviraetKletku() {
        var cell = new TicTacToeCell(2, 0, 1);
        cell.setMarker("O");
        assertFalse(cell.isEnabled());
    }

    @Test
    void vyvodCharDoskiSoderzhitPravilnyeSimvoly() {
        String out = zahvatitVyvod(() -> Utility.print(new char[]{'O','X',' ',' ','O',' ','X',' ','O'}));
        assertTrue(out.contains("O-X-"));
    }

    @Test
    void vyvodIntDoskiSoderzhitChisla() {
        String out = zahvatitVyvod(() -> Utility.print(new int[]{9,8,7,6,5,4,3,2,1}));
        assertTrue(out.contains("3-2-1-"));
    }

    @Test
    void vyvodSpisakHodovSoderzhitIndeksy() {
        var list = new ArrayList<>(Arrays.asList(1, 3, 5));
        String out = zahvatitVyvod(() -> Utility.print(list));
        assertTrue(out.contains("1-3-5-"));
    }

    @Test
    void panelSoderzhitDevyatKletok() {
        assertEquals(9, new TicTacToePanel(new GridLayout(3, 3)).getComponentCount());
    }

    @Test
    void klikNaKletkuUstanovitMetkuX() {
        var panel = new TicTacToePanel(new GridLayout(3, 3));
        var cell = (TicTacToeCell) panel.getComponent(2);
        bezVyvoda(cell::doClick);
        assertEquals('X', cell.getMarker());
    }

    @Test
    void posleKlikaIgrokaOtvechaetBot() {
        var panel = new TicTacToePanel(new GridLayout(3, 3));
        bezVyvoda(() -> ((TicTacToeCell) panel.getComponent(4)).doClick());
        long filled = 0;
        for (int i = 0; i < 9; i++) {
            if (((TicTacToeCell) panel.getComponent(i)).getMarker() != ' ') filled++;
        }
        assertTrue(filled >= 2);
    }
}
