package com.redfrog.api.pattern;

public class BasicPatternArea implements PatternArea {

    private int startX;
    private int startY;

    private int endX;
    private int endY;


    public BasicPatternArea(int startX, int startY, int endX, int endY)
    {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }


    public BasicPatternArea() {}


    @Override
    public PatternArea setStartX(int x) {
        startX = x;
        return this;
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public PatternArea setStartY(int y) {
        startY = y;
        return this;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public PatternArea setEndX(int x) {
        endX = x;
        return this;
    }

    @Override
    public int getEndX() {
        return endX;
    }

    @Override
    public PatternArea setEndY(int y) {
        endY = y;
        return this;
    }

    @Override
    public int getEndY() {
        return endY;
    }

    @Override
    public PatternArea setStart(int x, int y) {
        setStartX(x);
        setStartY(y);
        return this;
    }

    @Override
    public PatternArea setEnd(int x, int y) {
        setEndX(x);
        setEndY(y);
        return this;
    }

    @Override
    public PatternArea set(int startX, int startY, int endX, int endY) {
        setStart(startX, startY);
        setEnd(endX, endY);
        return this;
    }
}
