package com.redfrog.api.pattern;

public interface PatternArea {

    public PatternArea setStartX(int x);
    public int getStartX();

    public PatternArea setStartY(int y);
    public int getStartY();


    public PatternArea setEndX(int x);
    public int getEndX();

    public PatternArea setEndY(int y);
    public int getEndY();


    public PatternArea setStart(int x, int y);

    public PatternArea setEnd(int x, int y);

    public PatternArea set(int startX, int startY, int endX, int endY);
}
