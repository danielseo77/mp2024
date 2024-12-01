package com.example.fortress;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Arrays;

public class Terrain {

    private boolean[][] terrainArray; // 지형 정보 저장
    private int width;
    private int height;
    private final int slope = 5;

    public Terrain(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.terrainArray = bitmapToArray(bitmap);
    }

    private boolean[][] bitmapToArray(Bitmap bitmap) {
        boolean[][] array = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bitmap.getPixel(x, y);
                array[y][x] = (color & 0xFFFFFF) == 0x000000;
            }
        }
        return array;
    }

    public int getTerrain(int x) { // 주어진 x좌표에 대한 y값
        for (int y = 0; y < height; y++) {
            if (terrainArray[y][x]) {
                return y;
            }
        }
        return -1;
    }

    public int getTerrainSlope(int x, int tankY) { // 경사 여부
        int nextY = tankY + Tank.TankSizeY - slope;
        int y;
        if (terrainArray[nextY][x]) { return -1; }
        else {
            for (y= nextY; y < height; y++) {
                if (terrainArray[y][x]) {
                    return y;
                }
            }
        }
        return -1;
    }
    public void print() {
        Log.d("Terrain", "width : " + width + " height : " + height + " terrainArray : " + terrainArray[400][400]);
    }
}
