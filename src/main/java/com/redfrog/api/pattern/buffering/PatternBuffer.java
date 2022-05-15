package com.redfrog.api.pattern.buffering;

public interface PatternBuffer {


    public int getWidth();

    public int getHeight();

    public int getStartX();

    public int getStartY();

    // Sets an object with positions that will be adjusted to be inside the area.
    public void setAbsoluteObject(int x, int y, Object obj);

    // Sets an object with positions inside the area.
    public void setRelativeObject(int x, int y, Object obj);

    // Returns the object of positions that will be adjusted to be inside the area.
    public Object getAbsoluteObject(int x, int y);

    // Returns the object with positions inside the area.
    public Object getRelativeObject(int x, int y);

    public Object[][] getRelativeData();

    public int getCount();
}
