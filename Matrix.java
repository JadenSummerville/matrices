import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * Immutable
 * **/
public class Matrix{
    private final double[][] graph;
    private static Random random = new Random();
    Matrix(double[][] graph){
        this.graph = cloneArray(graph);
    }
    Matrix(int height, int width, boolean intMatrix){
        graph = new double[height][width];
        for(int i = 0; i != graph.length; i++){
            for(int j = 0; j != graph[0].length; j++){
                if(intMatrix){
                    graph[i][j] = random.nextInt(21)-10;
                }else{
                    graph[i][j] = random.nextDouble()*20-10;
                }
            }
        }
    }
    public static void main(String[] args){
        double[][] A = new double[2][3];
        A[0][0] = 2;
        A[1][0] = 0;
        A[0][1] = 3;
        A[1][1] = 1;
        A[0][2] = -1;
        A[1][2] = 4;
        Matrix a = new Matrix(A);
        double[][] B = new double[3][1];
        B[0][0] = 1;
        B[1][0] = 1;
        B[2][0] = 1;
        Matrix b = new Matrix(B);
        b.display();
        System.out.println();
        b.matrixMultiply(a).display();;

    }
    public Matrix matrixMultiply(double[] matrix){
        double[][] newMatrix = new double[matrix.length][1];
        for(int i = 0; i != matrix.length; i++){
            newMatrix[i][0] = matrix[i];
        }
        Matrix mat = new Matrix(newMatrix);
        return matrixMultiply(mat);
    }
    public Matrix matrixMultiply(Matrix matrix){
        double[][] newGraph = new double[graph.length][1];
        for(int i = 0; i != graph.length; i++){
            for(int j = 0; j != graph[0].length; j++){
                newGraph[i][0] += graph[i][j]*matrix.get(0, j);
            }
        }
        Matrix goal = new Matrix(newGraph);
        return goal;
    }
    private static double[][] cloneArray(double[][] original) {
        int rows = original.length;
        int columns = original[0].length;
    
        double[][] copy = new double[rows][columns];
    
        for (int i = 0; i != rows; i++) {
            for(int j = 0; j != columns; j++){
                copy[i][j] = original[i][j];
            }
        }
    
        return copy;
    }
    public Matrix scale(int y, double scale){
        double[][] goalArray = cloneArray(graph);
        for(int i = 0; i != graph[0].length; i++){
            goalArray[y][i] *= scale;
        }
        Matrix goal = new Matrix(goalArray);
        return goal;
    }
    public Matrix scale(double scale){
        double[][] goalArray = cloneArray(graph);
        for(int i = 0; i != graph[0].length; i++){
            for(int j = 0; j != graph.length; j++){
                goalArray[j][i] *= scale;
            }
        }
        Matrix goal = new Matrix(goalArray);
        return goal;
    }
    public Matrix add(Matrix mat){
        double[][] goal =  new double[graph.length][graph[0].length];
        for(int i = 0; i != graph.length; i++){
            for(int j = 0; j != graph[0].length; j++){
                goal[i][j] = graph[i][j] + mat.get(j, i);
            }
        }
        return new Matrix(goal);
    }
    public double get(int x, int y){
        return graph[y][x];
    }
    public void display(){
        for(int i = 0; i != graph.length; i++){
            for(int j = 0; j != graph[0].length; j++){
                System.out.print(graph[i][j]+" ");
            }
            System.out.println();
        }
    }

}