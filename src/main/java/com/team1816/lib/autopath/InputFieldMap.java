package com.team1816.lib.autopath;

import com.team1816.lib.autopath.FieldMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InputFieldMap extends FieldMap {
    private final ArrayList<int[]> inputLineX = new ArrayList<>();
    private final ArrayList<int[]> inputLineY = new ArrayList<>();
    private final ArrayList<Integer> inputLineCount = new ArrayList<>();

    private final ArrayList<Integer> inputCircleX = new ArrayList<>();
    private final ArrayList<Integer> inputCircleY = new ArrayList<>();
    private final ArrayList<Double> inputCircleRadius = new ArrayList<>();
    private final ArrayList<boolean[]> inputCircleQuadrants = new ArrayList<>();
    private final ArrayList<Boolean> inputCircleFilled = new ArrayList<>();
    private final ArrayList<Integer> inputCircleCount = new ArrayList<>();

    private final ArrayList<int[]> inputPolygonX = new ArrayList<>();
    private final ArrayList<int[]> inputPolygonY = new ArrayList<int[]>();
    private final ArrayList<Boolean> inputPolygonFilled = new ArrayList<>();
    private final ArrayList<Integer> inputPolygonCount = new ArrayList<>();

    InputFieldMap(int mapLengthX, int mapWidthY){
        super(mapLengthX, mapWidthY);
    }

    public boolean addLineInput(int x1, int y1, int x2, int y2){
        for(int i = 0; i < inputLineX.size(); i++)
            if(inputLineX.get(i)[0] == x1 && inputLineX.get(i)[1] == x2 && inputLineY.get(i)[0] == y1 && inputLineY.get(i)[1] == y2) {
                inputLineCount.set(i, inputLineCount.get(i) + 1);
                return false;
            }
        inputLineX.add(new int[]{x1, x2});
        inputLineY.add(new int[]{y1, y2});
        inputLineCount.add(1);
        return true;
    }

    public boolean addCircleInput(int x, int y, boolean quadrant1, boolean quadrant2, boolean quadrant3, boolean quadrant4, double radius, boolean circleFilled){
        for(int i = 0; i < inputCircleX.size(); i++)
            if(inputCircleX.get(i) == x && inputCircleY.get(i) == y && Arrays.hashCode(inputCircleQuadrants.get(i)) == Arrays.hashCode(new boolean[]{quadrant1, quadrant2, quadrant3, quadrant4}) && inputCircleRadius.get(i) < 1E-6) {
                inputCircleCount.set(i, inputCircleCount.get(i) + 1);
                return false;
            }
        inputCircleX.add(x);
        inputCircleY.add(y);
        inputCircleQuadrants.add(new boolean[]{quadrant1, quadrant2, quadrant3, quadrant4});
        inputCircleRadius.add(radius);
        inputCircleFilled.add(circleFilled);
        inputCircleCount.add(1);
        return true;
    }

    public boolean addPolygonInput(int[] verticesX, int[] verticesY, boolean polygonFilled){
        for(int i = 0; i < inputPolygonX.size(); i++) {
            boolean same = true;
            for (int j = 0; j < inputPolygonX.get(i).length; j++)
                if (!Objects.equals(inputPolygonX.get(i)[j], inputPolygonY.get(i)[j]))
                    same = false;
            if(same){
                inputPolygonCount.set(i, inputPolygonCount.get(i)+1);
                return false;
            }
        }
        inputPolygonX.add(verticesX);
        inputPolygonY.add(verticesY);
        inputPolygonFilled.add(polygonFilled);
        inputPolygonCount.add(1);
        return true;
    }

    @Override
    public boolean drawLine(double x1, double y1, double x2, double y2){
        return drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }
    @Override
    public boolean drawLine(int x1, int y1, int x2, int y2){
        this.addLineInput(x1, y1, x2, y2);

        return super.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawCircle(double centerX, double centerY, double radius, boolean fillCircle) {
        this.drawCircle((int)centerX, (int)centerY, radius, fillCircle);
    }
    @Override
    public void drawCircle(int centerX, int centerY, double radius, boolean fillCircle) {
        this.drawCircleQuadrant(centerX, centerY, true, true, true, true, radius, fillCircle);
    }
    @Override
    public void drawCircleQuadrant(double centerX, double centerY, boolean quadrant1, boolean quadrant2, boolean quadrant3, boolean quadrant4, double radius, boolean fillQuadrant){
        this.drawCircleQuadrant((int)centerX, (int)centerY, quadrant1, quadrant2, quadrant3, quadrant4, radius, fillQuadrant);
    }
    @Override
    public void drawCircleQuadrant(int centerX, int centerY, boolean quadrant1, boolean quadrant2, boolean quadrant3, boolean quadrant4, double radius, boolean fillQuadrant){
        this.addCircleInput(centerX, centerY, quadrant1, quadrant2, quadrant3, quadrant4, radius, fillQuadrant);

        super.drawCircleQuadrant(centerX, centerY, quadrant1, quadrant2, quadrant3, quadrant4, radius, fillQuadrant);
    }

    @Override
    public void drawPolygonDouble(List<Double> verticesX, List<Double> verticesY, boolean fillPolygon){
        ArrayList<Integer> intVerticesX = new ArrayList<>();
        ArrayList<Integer> intVerticesY = new ArrayList<>();

        verticesX.forEach(a -> intVerticesX.add(a.intValue()));
        verticesY.forEach(a -> intVerticesY.add(a.intValue()));

        drawPolygonInteger(intVerticesX, intVerticesY, fillPolygon);
    }
    @Override
    public void drawPolygonInteger(List<Integer> verticesX, List<Integer> verticesY, boolean fillPolygon){
        int[] intVerticesX = new int[verticesX.size()];
        int[] intVerticesY = new int[verticesY.size()];

        for(int i = 0; i < intVerticesX.length; i++)
            intVerticesX[i] = verticesX.get(i);
        for(int i = 0; i < intVerticesY.length; i++)
            intVerticesY[i] = verticesY.get(i);

        drawPolygon(intVerticesX, intVerticesY, fillPolygon);
    }
    @Override
    public void drawPolygon(double[] verticesX, double[] verticesY, boolean fillPolygon) {
        int[] intVerticesX = new int[verticesX.length];
        int[] intVerticesY = new int[verticesY.length];

        for (int i = 0; i < intVerticesX.length; i++)
            intVerticesX[i] = (int) verticesX[i];
        for (int i = 0; i < intVerticesY.length; i++)
            intVerticesY[i] = (int) verticesY[i];

        drawPolygon(intVerticesX, intVerticesY, fillPolygon);
    }
    @Override
    public void drawPolygon(int[] verticesX, int[] verticesY, boolean fillPolygon){
        this.addPolygonInput(verticesX, verticesY, fillPolygon);

        super.drawPolygon(verticesX, verticesY, fillPolygon);
    }

    public int[][] getCombinedMap(){
        int[][] intMap = super.getBaseIntMap();

        for(int i = 0; i < inputLineX.size(); i++) {
            intMap[inputLineY.get(i)[0]][inputLineX.get(i)[0]] += inputLineCount.get(i) * 2;
            intMap[inputLineY.get(i)[1]][inputLineX.get(i)[1]] += inputLineCount.get(i) * 2;
        }

        for(int i = 0; i < inputCircleX.size(); i++)
            intMap[inputCircleY.get(i)][inputCircleX.get(i)] += inputCircleCount.get(i)*20;

        return intMap;
    }
    public FieldMap getBaseMap(){
        return super.getCopy();
    }
    public FieldMap getLineInputMap(){
        FieldMap lineInputMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputLineX.size(); i++){
            lineInputMap.drawPixel(inputLineX.get(i)[0], inputLineY.get(i)[0]);
            lineInputMap.drawPixel(inputLineX.get(i)[1], inputLineY.get(i)[1]);
        }

        return lineInputMap;
    }
    public FieldMap getLineMap(){
        FieldMap lineMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputLineX.size(); i++){
            lineMap.drawLine(inputLineX.get(i)[0], inputLineY.get(i)[0], inputLineX.get(i)[1], inputLineY.get(i)[1]);
        }

        return lineMap;
    }
    public FieldMap getCircleInputMap(){
        FieldMap circleInputMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputCircleX.size(); i++){
            circleInputMap.drawPixel(inputCircleX.get(i), inputCircleY.get(i));
        }

        return circleInputMap;
    }
    public FieldMap getCircleMap(boolean fillFromInputs, boolean fillCircle){
        FieldMap circleMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputCircleX.size(); i++){
            if(fillFromInputs && fillCircle)
                circleMap.drawCircleQuadrant(inputCircleX.get(i), inputCircleY.get(i), inputCircleQuadrants.get(i)[0], inputCircleQuadrants.get(i)[1], inputCircleQuadrants.get(i)[2], inputCircleQuadrants.get(i)[3], inputCircleRadius.get(i), true);
            else if(fillFromInputs && !fillCircle)
                circleMap.drawCircleQuadrant(inputCircleX.get(i), inputCircleY.get(i), inputCircleQuadrants.get(i)[0], inputCircleQuadrants.get(i)[1], inputCircleQuadrants.get(i)[2], inputCircleQuadrants.get(i)[3],inputCircleRadius.get(i), false);
            else
                circleMap.drawCircleQuadrant(inputCircleX.get(i), inputCircleY.get(i), inputCircleQuadrants.get(i)[0], inputCircleQuadrants.get(i)[1], inputCircleQuadrants.get(i)[2], inputCircleQuadrants.get(i)[3],inputCircleRadius.get(i), inputCircleFilled.get(i));
        }

        return circleMap;
    }
    public FieldMap getPolygonInputMap(){
        FieldMap polygonInputMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputPolygonX.size(); i++)
            polygonInputMap.drawPolygon(inputPolygonX.get(i), inputPolygonY.get(i), false);

        return polygonInputMap;
    }
    public FieldMap getPolygonMap(){
        FieldMap polygonMap = new FieldMap(this.getMapX(), this.getMapY());

        for(int i = 0; i < inputPolygonX.size(); i++)
            polygonMap.drawPolygon(inputPolygonX.get(i), inputPolygonY.get(i), inputPolygonFilled.get(i));

        return polygonMap;
    }

    public ArrayList<int[]> getInputLineX(){
        return (new ArrayList<>(List.copyOf(inputLineX)));
    }
    public ArrayList<int[]> getInputLineY(){
        return (new ArrayList<>(List.copyOf(inputLineY)));
    }
    public ArrayList<Integer> getInputLineCount(){
        return (new ArrayList<>(List.copyOf(inputLineCount)));
    }
    public ArrayList<Integer> getInputCircleX(){
        return (new ArrayList<>(List.copyOf(inputCircleX)));
    }
    public ArrayList<Integer> getInputCircleY(){
        return (new ArrayList<>(List.copyOf(inputCircleY)));
    }
    public ArrayList<Double> getInputCircleRadius(){
        return (new ArrayList<>(List.copyOf(inputCircleRadius)));
    }
    public ArrayList<boolean[]> getInputCircleQuadrants(){
        return (new ArrayList<>(List.copyOf(inputCircleQuadrants)));
    }
    public ArrayList<Boolean> getInputCircleFilled(){
        return (new ArrayList<>(List.copyOf(inputCircleFilled)));
    }
    public ArrayList<Integer> getInputCircleCount(){
        return (new ArrayList<>(List.copyOf(inputCircleCount)));
    };
    public ArrayList<int[]> getInputPolygonX(){
        return (new ArrayList<>(List.copyOf(inputPolygonX)));
    }
    public ArrayList<int[]> getInputPolygonY(){
        return (new ArrayList<>(List.copyOf(inputPolygonY)));
    }
    public ArrayList<Boolean> getInputPolygonFilled(){
        return (new ArrayList<>(List.copyOf(inputPolygonFilled)));
    }
    public ArrayList<Integer> getInputPolygonCount(){
        return (new ArrayList<>(List.copyOf(inputPolygonCount)));
    }

    public String toStringSuper(){
        return super.toString();
    }

    @Override
    public String toString(){
        int[][] intMap = this.getCombinedMap();

        StringBuilder stringReturn = new StringBuilder();

        for(int j = intMap.length-1; j >= 0; j--) {
            stringReturn.append(j > 9 ? j+" " : j+"  ");
            for (int i = 0; i < intMap[0].length; i++)
                stringReturn.append(intMap[j][i] == 0 ? "   ": intMap[j][i]>9 ? intMap[j][i]+" " : intMap[j][i]+"  ");
            stringReturn.append("\n");
        }

        stringReturn.append("   ");
        for(int i = 0; i < intMap[0].length; i++)
            stringReturn.append(i > 9 ? i+" " : i+"  ");
        stringReturn.append("\n");

        return stringReturn.toString();
    }
}