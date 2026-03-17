package game.core;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import ui.Paintable;
import game.terrain.*;

import java.util.ArrayList;
import java.util.List;

public class Grid implements Paintable {
    private final int size;
    private final Cell[][] cells;
    private Point mousePos;

    public Grid(int s) {
        size = s;
        cells = new Cell[size][size];
        IntStream.range(0, size).forEach(i -> IntStream.range(0, size)
                .forEach(j -> cells[i][j] = new Cell(colToLabel(i), j, 10 + Cell.size * i, 10 + Cell.size * j)));
    }

    public Optional<Cell> findByWorldCoords(int x, int y) {
        int half = size / 2;

        int col = half + x;
        int row = half - y;

        if (!inBounds(col, row)) {
            return Optional.empty();
        }
        return cellAtColRow(col, row);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setMousePos(Point mousePos) {
        this.mousePos = mousePos;
    }

    public int getSize() {
        return size;
    }

    public Boolean inBounds(int column, int row) {
        return column >= 0 && column < size && row >= 0 && row < size;
    }

    private char colToLabel(int col) {
        return (char) (col + Character.valueOf('A'));
    }

    private int labelToCol(char col) {
        return (int) (col - Character.valueOf('A'));
    }

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j].setMousePos(mousePos);
                cells[i][j].paint(g);
            }
        }
    }

    public Optional<Cell> cellAtColRow(int c, int r) {
        if (c >= 0 && c < cells.length && r >= 0 && r < cells[c].length) {
            return Optional.of(cells[c][r]);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Cell> cellAtColRow(char c, int r) {
        return cellAtColRow(labelToCol(c), r);
    }

    public Optional<Cell> cellAtPoint(Point p) {
        return java.util.Arrays.stream(cells)
                .flatMap(java.util.Arrays::stream)
                .filter(c -> c.contains(p))
                .findFirst();
    }

    public Cell getRandomCell(Random random) {
        int column = random.nextInt(size);
        int row = random.nextInt(size);
        return cells[column][row];
    }

    public int getTotalCells() {
        return size * size;
    }

    public List<Cell> getAllCells() {
        List<Cell> list = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                list.add(cells[i][j]);
            }
        }
        return list;
    }

    public boolean surroundByType(Cell cell, Class<? extends Terrain> targetClass) {
        return java.util.Arrays.stream(Direction.values())
                .allMatch(dir -> {
                    int nx = cell.getColumn() + dir.dx;
                    int ny = cell.getRow() + dir.dy;
                    return inBounds(nx, ny)
                            && cellAtColRow(nx, ny)
                                    .map(c -> targetClass.isInstance(c.getTerrain()))
                                    .orElse(false);
                });
    }

    public Optional<Cell> getEmptyNeighbor(Cell cell, Random rng) {
        int col = cell.getColumnAsInt();
        int row = cell.getRow();

        List<Cell> neighbors = new ArrayList<>();
        List<int[]> dirs = List.of(
                new int[] { 1, 0 }, new int[] { -1, 0 },
                new int[] { 0, 1 }, new int[] { 0, -1 });

        for (int[] d : dirs) {
            cellAtColRow(col + d[0], row + d[1])
                    .filter(Cell::isEmpty)
                    .ifPresent(neighbors::add);
        }

        if (neighbors.isEmpty())
            return Optional.empty();
        return Optional.of(neighbors.get(rng.nextInt(neighbors.size())));
    }
}
