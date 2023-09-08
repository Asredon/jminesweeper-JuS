package com.voipfuture.jminesweep.server;

import java.util.Random;
import com.voipfuture.jminesweep.shared.Difficulty;

public class GameField {

    final int rows;
    final int columns;
    final Tile[] field;
    final Difficulty difficulty;

    static class Tile {
        private boolean isBomb = false;
        public boolean isHidden = true;
        public int surroundingBombs = 0;
        public int hasBomb() {
            return isBomb ? 1 : 0;
        }

        public void setBomb() {
            isBomb = true;
        }

        public void reveal() {
            isHidden = false;
        }

        public void setBombCount(int surroundingBombs) {
            this.surroundingBombs = surroundingBombs;
        }

        public String display() {
            if (isHidden) {
                return "?";
            }
            if (isBomb) {
                return "B";
            }
            if (surroundingBombs == 0) {
                return ".";
            }
            return String.valueOf(surroundingBombs);
        }

        @Override
        public String toString() {
            return display();
        }
    }

    public GameField(int columns, int rows, Difficulty difficulty) {
        this.columns = columns;
        this.rows = rows;
        this.field = new Tile[columns * rows];
        this.difficulty = difficulty;
        setBombs();
    }

    private void setBombs() {
        Random rng = new Random();

        float diffMod = switch (difficulty) {
            case EASY -> 0.05f;
            case MEDIUM -> 0.25f;
            case HARD -> 0.45f;
        };

        for(int x = 0; x < columns; x++) {
            for(int y = 0; y < rows; y++) {
                if (rng.nextFloat() <= diffMod) {
                    Tile bomb = new Tile();
                    bomb.setBomb();
                    bomb.reveal();
                    field[x+(y * rows)] = bomb;
                } else {
                    Tile tile = new Tile();
                    tile.reveal();
                    field[x+(y * rows)] = tile;
                }
            }
        }

        for(int x = 0; x < columns; x++) {
            for(int y = 0; y < rows; y++) {
                if (!field[x+(y * rows)].isBomb) {
                    field[x+(y * rows)].setBombCount(getBombCountFor(x, y));
                }
            }
        }
    }
    public int getBombCountFor(int x, int y) {
        return countBomb(x-1, y) + countBomb(x+1, y) +
                countBomb(x, y-1) + countBomb(x, y+1) +
                countBomb(x-1, y-1) + countBomb(x-1, y+1) +
                countBomb(x+1, y-1) + countBomb(x+1, y+1);
    }
    private int countBomb(int x, int y) {
        return (x < columns && x >= 0 && y < rows && y >= 0) ? field[(x + (y * rows))].hasBomb() : 0;
    }
}
