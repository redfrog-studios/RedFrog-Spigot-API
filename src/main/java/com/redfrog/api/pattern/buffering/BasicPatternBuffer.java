package com.redfrog.api.pattern.buffering;

import com.redfrog.api.pattern.PatternArea;

public class BasicPatternBuffer implements PatternBuffer {

    private int width;
    private int height;

    private PatternArea area;
    private Object[][] data;

    public BasicPatternBuffer(PatternArea area) {
        this.area = area;
        this.width = area.getEndX() - area.getStartX();
        this.height = area.getEndY() - area.getStartY();

        if (this.width < 0)
            this.width = 0;

        if (this.height < 0)
            this.height = 0;

        width++;
        height++;

        this.data = new Object[width][height];
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getStartX() {
        return area.getStartX();
    }

    @Override
    public int getStartY() {
        return area.getStartY();
    }

    // Sets an object with positions that will be adjusted to be inside the area.
    @Override
    public void setAbsoluteObject(int x, int y, Object obj) {

        x -= area.getStartX();
        y -= area.getStartY();

        setRelativeObject(x, y, obj);
    }


    // Sets an object with positions inside the area.
    @Override
    public void setRelativeObject(int x, int y, Object obj) {

        if (x < width && x >= 0 && y < height && y >= 0) {
            if (obj != null)
                data[x][y] = true;
        }
    }


    // Returns the object of positions that will be adjusted to be inside the area.
    @Override
    public Object getAbsoluteObject(int x, int y) {

        x -= area.getStartX();
        y -= area.getStartY();

        return getRelativeObject(x, y);
    }


    // Returns the object with positions inside the area.
    @Override
    public Object getRelativeObject(int x, int y) {

        if (x < width && x >= 0 && y < height && y >= 0) {
            return data[x][y];
        }

        return null;
    }


    @Override
    public Object[][] getRelativeData() {
        return data;
    }

    @Override
    public int getCount() {
        int count = 0;

        for (int x = 0; x < data.length; x++)
        {
            for (int y = 0; y < data[x].length; y++)
            {
                if (data[x][y] != null)
                    count++;
            }
        }

        return count;
    }
}
