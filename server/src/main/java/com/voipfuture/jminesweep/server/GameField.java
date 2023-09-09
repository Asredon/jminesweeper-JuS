package com.voipfuture.jminesweep.server;

import java.util.Arrays;
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
        public boolean isMarked = false;
        public int surroundingBombs = 0;
        public int x;
        public int y;
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
        public void setMarked(boolean isMarked) {
            this.isMarked = isMarked;
        }
        public boolean isEmptyTile() {
            return !isBomb && surroundingBombs == 0;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public String display() {
            if (isMarked) {
                return "M";
            }
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

        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < columns; x++) {
                if (rng.nextFloat() <= diffMod) {
                    Tile bomb = new Tile();
                    bomb.setBomb();
                    bomb.setX(x);
                    bomb.setY(y);
                    field[x+(y * columns)] = bomb;
                } else {
                    Tile tile = new Tile();
                    tile.setX(x);
                    tile.setY(y);
                    field[x+(y * columns)] = tile;
                }
            }
        }
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < columns; x++) {
                if (!field[x+(y * columns)].isBomb) {
                    field[x+(y * columns)].setBombCount(getBombCountFor(x, y));
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
        return (x < columns && x >= 0 && y < rows && y >= 0) ? field[(x + (y * columns))].hasBomb() : 0;
    }

    public boolean revealTile(int x,int y) {
        Tile tile = field[x + (y * columns)];
        tile.reveal();
        if (tile.isEmptyTile()) {
            revealAdjacentEmptyTiles(tile);
        }
        return tile.isBomb;
    }

    private void revealAdjacentEmptyTiles(Tile tile) {
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < columns; x++) {
               if (field[ (tile.getX() + x) + ((tile.getY() + y) * columns)].isEmptyTile()) {
                   field[ (tile.getX() + x) + ((tile.getY() + y) * columns)].reveal();
               } else if (!field[ (tile.getX() + x) + ((tile.getY() + y) * columns)].isEmptyTile() && !field[ (tile.getX() + x) + ((tile.getY() + y) * columns)].isBomb) {
                   field[ (tile.getX() + x) + ((tile.getY() + y) * columns)].reveal();
               }
            }
        }
    }

    public void markTile(int x, int y) {
        field[x + (y * columns)].setMarked(true);
    }

    public void unMarkTile(int x, int y) {
        field[x + (y * columns)].setMarked(false);
    }

    public String getFieldAsString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < columns; y++) {
            builder.append(Arrays.toString(field)
                    .replaceAll("\\[","")
                    .replaceAll("]", "")
                    .replaceAll(",", "")
                    .replaceAll(" ", "")
                    .substring(y*columns, (y*columns)+columns))
                    .append("\n");
        }
        return builder.toString();
    }
}
