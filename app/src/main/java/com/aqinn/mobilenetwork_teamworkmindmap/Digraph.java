package com.aqinn.mobilenetwork_teamworkmindmap;

/**
 * @author TopoGraph 有向图
 * 由于后续优化JSON转TreeModel
 * @date 2020/6/26 2:14 PM
 */
import java.util.ArrayList;
import java.util.List;

public class Digraph {

    private List<Point> points;

    public Digraph() {
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point){
        if (!points.contains(point))
            this.points.add(point);
    }

    public void removePoint(Point point){
        if (points.contains(point))
            this.points.remove(point);
    }

    public Point getPointByName(String name){
        for(Point p:points){
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }

    public boolean containPointByName(String name){
        for(Point p:points){
            if (p.getName().equals(name))
                return true;
        }
        return false;
    }

    public boolean isTopoGraph(){
        for (Point p:points){
            if (!isCircle(p,p))
                return false;
        }
        return true;
    }

    private boolean isCircle(Point root, Point start){
        for (Point p:start.getOutPoints()){
            if (p.equals(root))
                return false;
            else
                return isCircle(root,p);
        }
        return true;
    }

    /**
     * 对有向图进行拓扑排序
     * @return
     */
    public List<Point> topologicalSort(){
        if (!isTopoGraph())
            return null;
        List<Point> result = new ArrayList<>();
        while (!points.isEmpty()){
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).getIn() == 0){
                    result.add(points.get(i));
                    for (int j = 0; j < points.size(); j++) {
                        points.get(j).removeIn(points.get(i));
                    }
                    removePoint(points.get(i));
                }
            }
        }
        return result;
    }

}

class Point{

    private String name;

    private List<Point> inPoints;

    private List<Point> outPoints;

    public Point(String name){
        this.name = name;
        inPoints = new ArrayList<>();
        outPoints = new ArrayList<>();
    }

    public int getIn(){
        return this.inPoints.size();
    }

    public int getOut(){
        return this.outPoints.size();
    }

    public void removeIn(Point point){
        if (this.inPoints.contains(point))
            this.inPoints.remove(point);
    }

    public void removeOut(Point point){
        if (this.outPoints.contains(point))
            this.outPoints.remove(point);
    }

    public void addIn(Point point){
        if (!inPoints.contains(point))
            inPoints.add(point);
    }

    public void addOut(Point point){
        if (!outPoints.contains(point))
            outPoints.add(point);
    }

    public List<Point> getInPoints() {
        return inPoints;
    }

    public List<Point> getOutPoints() {
        return outPoints;
    }

    public String getName() {
        return name;
    }

}
