package com.team1816.lib.autopath;
import edu.wpi.first.math.geometry.Translation2d;

import java.util.ArrayList;

public class Bresenham {
    // function for line generation using Bresenham's line drawing algorithm
    public static boolean drawLine(FieldMap map, int x1, int y1, int x2, int y2, boolean applyToMap) {
        boolean collided = false;
        int dx, dy, i, e;
        int incx, incy, inc1, inc2;
        int x, y;
        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        x = x1; y = y1;
        if (dx > dy) {
            if(applyToMap)
                collided = !map.drawPixel(x, y) || collided;
            else
                collided = map.checkPixelHasObjectOrOffMap(x, y) || collided;
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            for (i = 0; i < dx; i++)
            {
                if (e >= 0)
                {
                    y += incy;
                    e += inc1;
                }
                else
                    e += inc2;
                x += incx;
                if(applyToMap)
                    collided = !map.drawPixel(x, y) || collided;
                else
                    collided = map.checkPixelHasObjectOrOffMap(x, y) || collided;
            }
        }
        else
        {
            if(applyToMap)
                collided = !map.drawPixel(x, y) || collided;
            else
                collided = map.checkPixelHasObjectOrOffMap(x, y) || collided;
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            for (i = 0; i < dy; i++)
            {
                if (e >= 0)
                {
                    x += incx;
                    e += inc1;
                }
                else
                    e += inc2;
                y += incy;
                if(applyToMap)
                    collided = !map.drawPixel(x, y) || collided;
                else
                    collided = map.checkPixelHasObjectOrOffMap(x, y) || collided;
            }
        }
        return collided;
    }

    public static Translation2d lineReturnCollision(FieldMap map, int x1, int y1, int x2, int y2) {
        int dx, dy, i, e;
        int incx, incy, inc1, inc2;
        int x, y;
        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        x = x1; y = y1;
        if (dx > dy) {
            if(map.checkPixelHasObjectOrOffMap(x, y))
                return new Translation2d(x, y);
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            for (i = 0; i < dx; i++)
            {
                if (e >= 0)
                {
                    y += incy;
                    e += inc1;
                }
                else
                    e += inc2;
                x += incx;
                if(map.checkPixelHasObjectOrOffMap(x, y))
                    return new Translation2d(x, y);
            }
        }
        else
        {
            if(map.checkPixelHasObjectOrOffMap(x, y))
                return new Translation2d(x, y);
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            for (i = 0; i < dy; i++)
            {
                if (e >= 0)
                {
                    x += incx;
                    e += inc1;
                }
                else
                    e += inc2;
                y += incy;
                if(map.checkPixelHasObjectOrOffMap(x, y))
                    return new Translation2d(x, y);
            }
        }
        return null;
    }

    public static int[] lineReturnCollisionInverted(FieldMap map, int x1, int y1, int x2, int y2, boolean startOnCollision) {
        boolean collisionStartFound = startOnCollision;
        int dx, dy, i, e;
        int incx, incy, inc1, inc2;
        int x, y;

        boolean changedX = false;
        boolean changedY = false;

        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        x = x1; y = y1;
        if (dx > dy) {
            if(map.checkPixelHasObjectOrOffMap(x, y))
                collisionStartFound = true;
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            for (i = 0; i < dx; i++)
            {
                if (e >= 0)
                {
                    y += incy;
                    e += inc1;
                    changedY = true;
                }
                else
                    e += inc2;
                x += incx;
                if(collisionStartFound && !map.checkPixelHasObjectOrOffMap(x, y))
                    return new int[]{x-incx, changedY ? y-incy: y};
                changedY = false;
            }
        }
        else
        {
            if(map.checkPixelHasObjectOrOffMap(x, y))
                collisionStartFound = true;
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            for (i = 0; i < dy; i++)
            {
                if (e >= 0)
                {
                    x += incx;
                    e += inc1;
                    changedX = true;
                }
                else
                    e += inc2;
                y += incy;
                if(collisionStartFound && !map.checkPixelHasObjectOrOffMap(x, y))
                    return new int[]{changedX ? x-incx: x, y-incy};
                changedX = false;
            }
        }
        return null;
    }

    public static int[] drawPerpLine(FieldMap map, double startMinRadius, int x1, int y1, int x2, int y2) {
        int midPixelX = (x2-x1)/2+x1;
        int midPixelY = (y2-y1)/2+y1;

        int hold = midPixelX-(y2-y1);
        y2 = midPixelY+(x2-x1);
        x2 = hold;

        x1 = midPixelX;
        y1 = midPixelY;

        int dx, dy, e;
        int incx, incy, inc1, inc2;
        int posX, posY;
        int negX, negY;
        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        posX = x1; posY = y1;
        negX = x1; negY = y1;
        if (dx > dy) {
            if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius){
                if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                    return new int[]{posX, posY};
                }
                if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                    return new int[]{negX, negY};
                }
            }
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            while (map.checkPixelOnMap(posX, posY) || map.checkPixelOnMap(negX, negY))
            {
                if (e >= 0)
                {
                    posY += incy;
                    negY -= incy;
                    e += inc1;
                }
                else
                    e += inc2;
                posX += incx;
                negX -= incx;
                if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius){
                    if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                        return new int[]{posX, posY};
                    }
                    if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                        return new int[]{negX, negY};
                    }
                }
            }
        }
        else
        {
            if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius){
                if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                    return new int[]{posX, posY};
                }
                if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                    return new int[]{negX, negY};
                }
            }
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            while (map.checkPixelOnMap(posX, posY) || map.checkPixelOnMap(negX, negY))
            {
                if (e >= 0)
                {
                    posX += incx;
                    negX -= incx;
                    e += inc1;
                }
                else
                    e += inc2;
                posY += incy;
                negY -= incy;
                if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius) {
                    if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                        return new int[]{posX, posY};
                    }
                    if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                        return new int[]{negX, negY};
                    }
                }
            }
        }
        System.out.println("Error in perping");
        return null;
    }

    public static int[] drawPerpLineMinusOnePixelPositive(FieldMap map, double startMinRadius, int x1, int y1, int x2, int y2) {
        int midPixelX = (x2-x1)/2+x1;
        int midPixelY = (y2-y1)/2+y1;

        int hold = midPixelX-(y2-y1);
        y2 = midPixelY+(x2-x1);
        x2 = hold;

        x1 = midPixelX;
        y1 = midPixelY;

        int dx, dy, e;
        int incx, incy, inc1, inc2;
        int posX, posY;

        boolean changedX = false;
        boolean changedY = false;

        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        posX = x1; posY = y1;
        if (dx > dy) {
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            while (map.checkPixelOnMap(posX, posY))
            {
                if (e >= 0)
                {
                    posY += incy;
                    e += inc1;
                    changedY = true;
                }
                else
                    e += inc2;
                posX += incx;
                if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius){
                    if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                        return new int[]{posX-incx, changedY ? posY-incy : posY};
                    }
                }
                changedY = false;
            }
        }
        else
        {
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            while (map.checkPixelOnMap(posX, posY))
            {
                if (e >= 0)
                {
                    posX += incx;
                    e += inc1;
                    changedX = true;
                }
                else
                    e += inc2;
                posY += incy;
                if(dist(posX, posY, midPixelX, midPixelY) > startMinRadius) {
                    if (!map.checkPixelHasObjectOrOffMap(posX, posY)) {
                        return new int[]{changedX ? posX-incx : posX, posY-incy};
                    }
                }
                changedX = false;
            }
        }
        System.out.println("Error in perping");
        return null;
    }

    public static int[] drawPerpLineMinusOnePixelNegative(FieldMap map, int x1, int y1, int x2, int y2) {
        int midPixelX = (x2-x1)/2+x1;
        int midPixelY = (y2-y1)/2+y1;

        int hold = midPixelX-(y2-y1);
        y2 = midPixelY+(x2-x1);
        x2 = hold;

        x1 = midPixelX;
        y1 = midPixelY;

        int dx, dy, e;
        int incx, incy, inc1, inc2;
        int negX, negY;

        int lastX = x1;
        int lastY = y1;

        dx = x2 - x1;
        dy = y2 - y1;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        incx = 1;
        if (x2 < x1) incx = -1;
        incy = 1;
        if (y2 < y1) incy = -1;
        negX = x1; negY = y1;
        if (dx > dy) {
            e = 2 * dy - dx;
            inc1 = 2 * (dy - dx);
            inc2 = 2 * dy;
            while (map.checkPixelOnMap(negX, negY))
            {
                if (e >= 0)
                {
                    negY -= incy;
                    e += inc1;
                }
                else
                    e += inc2;
                negX -= incx;
                if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                    return new int[]{lastX, lastY};
                } else{
                    lastX = negX;
                    lastY = negY;
                }
            }
        }
        else
        {
            e = 2 * dx - dy;
            inc1 = 2 * (dx - dy);
            inc2 = 2 * dx;
            while (map.checkPixelOnMap(negX, negY))
            {
                if (e >= 0)
                {
                    negX -= incx;
                    e += inc1;
                }
                else
                    e += inc2;
                negY -= incy;
                if (!map.checkPixelHasObjectOrOffMap(negX, negY)) {
                    return new int[]{lastX, lastY};
                } else{
                    lastX = negX;
                    lastY = negY;
                }
            }
        }
        System.out.println("Error in perping");
        return null;
    }

    public static boolean drawQuadrant(FieldMap map, int centerX, int centerY, double radius, boolean first, boolean second, boolean third, boolean fourth){
        boolean collided = false;

        double d = 3.14 - (2 * radius);
        int x = 0;
        double y = radius;

        if(first) {
            collided = map.drawPixel(centerX + x, centerY + (int)y) || collided;
            collided = map.drawPixel(centerX + (int)y, centerY + x) || collided;
        }
        if(second) {
            collided = map.drawPixel(centerX - x, centerY + (int)y) || collided;
            collided = map.drawPixel(centerX - (int)y, centerY + x) || collided;
        }
        if(third) {
            collided = map.drawPixel(centerX - x, centerY - (int)y) || collided;
            collided = map.drawPixel(centerX - (int)y, centerY - x) || collided;
        }
        if(fourth) {
            collided = map.drawPixel(centerX + x, centerY - (int)y) || collided;
            collided = map.drawPixel(centerX + (int)y, centerY - x) || collided;
        }

        while (y >= x)
        {
            // for each pixel we will
            // draw all eight pixels

            x++;

            // check for decision parameter
            // and correspondingly
            // update d, x, y
            if (d > 0)
            {
                y--;
                d = d + 4 * (x - y) + 10;
            }
            else
                d = d + 4 * x + 6;

            if(first) {
                collided = map.drawPixel(centerX + x, centerY + (int)y) || collided;
                collided = map.drawPixel(centerX + (int)y, centerY + x) || collided;
            }
            if(second) {
                collided = map.drawPixel(centerX - x, centerY + (int)y) || collided;
                collided = map.drawPixel(centerX - (int)y, centerY + x) || collided;
            }
            if(third) {
                collided = map.drawPixel(centerX - x, centerY - (int)y) || collided;
                collided = map.drawPixel(centerX - (int)y, centerY - x) || collided;
            }
            if(fourth) {
                collided = map.drawPixel(centerX + x, centerY - (int)y) || collided;
                collided = map.drawPixel(centerX + (int)y, centerY - x) || collided;
            }
        }
        return collided;
    }

    public static boolean drawCircle(FieldMap map, int centerX, int centerY, double radius){
        return drawQuadrant(map, centerX, centerY, radius, true, true, true, true);
    }

    public static double dist(int x1, int y1, int x2, int y2){
        return dist((double)x1, (double)y1, (double)x2, (double)y2);
    }
    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
    }
}