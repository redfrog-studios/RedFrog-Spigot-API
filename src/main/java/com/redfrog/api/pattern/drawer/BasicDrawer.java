package com.redfrog.api.pattern.drawer;

import com.redfrog.api.pattern.PatternArea;
import com.redfrog.api.pattern.buffering.BasicPatternBuffer;
import com.redfrog.api.pattern.buffering.PatternBuffer;

public class BasicDrawer {

    public static PatternBuffer drawRect(PatternArea area)
    {
        PatternBuffer outBuffer = new BasicPatternBuffer(area);

        for (int x = area.getStartX(); x <= area.getEndX(); x++)
        {
            for (int y = area.getStartY(); y <= area.getEndY(); y++)
            {
                outBuffer.setAbsoluteObject(x, y, true);
            }
        }

        return outBuffer;
    }


    public static PatternBuffer drawTriangle(PatternArea area)
    {
        PatternBuffer outBuffer = new BasicPatternBuffer(area);

        int width = area.getEndX() + 1 - area.getStartX();
        int centerX = width / 2;
        int height = area.getEndY() + 1 - area.getStartY();
        int halfWidth = (width - 1) / 2;
        int totalXMult = halfWidth / (height - 1);
        int curXMult = 0;

        for (int y = 0; y < height; y++)
        {
            curXMult = totalXMult * y;
            for (int x = centerX - curXMult; x <= centerX + curXMult; x++)
            {
                outBuffer.setRelativeObject(x, y, true);
            }
        }

        return outBuffer;
    }
}
