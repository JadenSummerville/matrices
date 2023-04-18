import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.management.RuntimeErrorException;

import java.lang.IllegalArgumentException;
/**
 * A matrix is an immutable 2d array of numbers where each row and column is of length 1 or greater.
 * Visually the matrix forms a square
 */
// graph[y][x], graph[height][width], graph[N][M]
public class Matrix{
    private final double[][] graph;
    private static Random random = new Random();
    /**
     * Create a matrix with the given spefifications.
     * 
     * @param graph matrix values.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException iff 'graph is null'
     * 
     * O(original.length*original[0].length)
    */
    Matrix(double[][] graph){
        checkNull(graph);
        this.graph = cloneArray(graph);
    }
    /**
     * Generate a randomized matrix that is 'height'X'width'. Values are between -10 and 10.
     * If 'intMatrix' then integers are generated otherwise doubles are generated.
     * 
     * @param height n of matrix
     * @param width m of matrix
     * @param if true generate integers, else doubles.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException iff 'height' or 'width' > 1.
     * 
     * O('height'*'width')
    */
    Matrix(int height, int width, boolean intMatrix){
        if(height < 1 || width < 1){
            throw new IllegalArgumentException("Dimensions must be atleast 1X1.");
        }
        graph = new double[height][width];
        for(int i = 0; i != getN(); i++){
            for(int j = 0; j != getM(); j++){
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
        A[0][0] = 1;
        A[1][0] = 0;
        A[0][1] = 2;
        A[1][1] = -3;
        A[0][2] = 0;
        A[1][2] = 1;
        Matrix a = new Matrix(A);
        a.display();
        System.out.println();
        System.out.println();
        //double[][] B = new double[1][3];
        //B[0][0] = 1;
        //B[0][1] = 1;
        //B[0][2] = 1;
        //Matrix b = new Matrix(B);
        //b.display();
        
        //a.display();
        //System.out.println();
        //System.out.println(a.findPlaceOfLeadingVariable(1));
        //System.out.println(a.getN()+" "+a.getM());
        //b.display();
        //System.out.println();
        //System.out.println(b.getN()+" "+b.getM());
        //a.matrixMultiply(b).display();
    }
    /**
     * Throw IllegalArgumentException if input is null
     * @param ob object to check if null
     * @throws IllegalArgumentException iff input is null
     * 
     * O(1)
    */
    private static void checkNull(Object ob){
        if(ob == null){
            throw new IllegalArgumentException("Null input.");
        }
    }
    /**
     * Return the result of matrix multiplication between this vector and 'matrix'.
     * 
     * @param matrix value to multiply this by.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException If not AXB times BXC matricies.
     * @throws IllegalArgumentException If 'matrix' != null.
     * @return matrix multiple of this and 'matrix'.
     * 
     * O(getN()*getM())
    */
    public Matrix matrixMultiply(double[] matrix){
        checkNull(matrix);
        if(getM() != matrix.length){
            throw new IllegalArgumentException("not AXB times BXC matricies.");
        }
        double[][] newMatrix = new double[matrix.length][1];
        for(int i = 0; i != matrix.length; i++){
            newMatrix[i][0] = matrix[i];
        }
        Matrix mat = new Matrix(newMatrix);
        return matrixMultiply(mat);
    }
    /**
     * Get graph height.
     * @return graph height.
     * 
     * O(1)
    */
    public int getN(){
        return graph.length;
    }
        /**
     * Get graph width.
     * @return graph width
     * 
     * O(1)
    */
    public int getM(){
        return graph[0].length;
    }
    /**
     * @param matrix value to multiply this by.
     * @throws IllegalArgumentException If not AXB times BX1 matricies.
     * @throws IllegalArgumentException If 'matrix' != null.
     * @return matrix multiple of this and 'matrix'.
     * 
     * O(getN()*getM())
    */
    public Matrix matrixMultiply(Matrix matrix){
        checkNull(matrix);
        if(getM() != matrix.getN()){
            throw new IllegalArgumentException("not AXB times BX1 matricies.");
        }
        double[][] newGraph = new double[getN()][1];
        for(int i = 0; i != getN(); i++){
            for(int j = 0; j != getM(); j++){
                newGraph[i][0] += graph[i][j]*matrix.get(0, j);
            }
        }
        Matrix goal = new Matrix(newGraph);
        return goal;
    }
    /**
     * Clone doule[][] 'original'
     * 
     * @param original double[][] to be clones.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException iff 'original' == null
     * @return cloned double[][]
     * 
     * O(original.length*original[0].length)
    */
    private static double[][] cloneArray(double[][] original) {
        checkNull(original);
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
    /**
     * Return Matrix wih scaled row. Row 'y' is scaled by 'scale'.
     * @param y row to scale.
     * @param scale value to scale row by.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException iff y < 0 || y >= getN().
     * @return Matrix with scaled 'y' row.
     * 
     * O(getN()*getM())
    */
    public Matrix scale(int y, double scale){
        isARow(y);
        double[][] goalArray = cloneArray(graph);
        for(int i = 0; i != getM(); i++){
            goalArray[y][i] *= scale;
        }
        Matrix goal = new Matrix(goalArray);
        return goal;
    }
    /**
     * Return scaled Matrix. Scaled by 'scale'.
     * 
     * @param scale value to scale row by.
     * @spec.requires no NaN inputs.
     * @return scaled matrix.
     * 
     * 
     * O(getN()*getM())
    */
    public Matrix scale(double scale){
        double[][] goalArray = cloneArray(graph);
        for(int i = 0; i != getM(); i++){
            for(int j = 0; j != getN(); j++){
                goalArray[j][i] *= scale;
            }
        }
        Matrix goal = new Matrix(goalArray);
        return goal;
    }
    /**
     * Add matrix 'mat' to current matrix and return new matrix without
     * changing the original.
     * 
     * @param mat Matrix to be added.
     * @throws IllegalArgumentException if mat == null
     * @throws IllegalArgumentException if mat.getN() != this.getN() || mat.getM() != this.getM();
     * 
     * O(getN()*getM())
    */
    public Matrix add(Matrix mat){
        checkInRange(mat);
        double[][] goal =  new double[getN()][getM()];
        for(int i = 0; i != getN(); i++){
            for(int j = 0; j != getM(); j++){
                goal[i][j] = graph[i][j] + mat.get(j, i);
            }
        }
        return new Matrix(goal);
    }
    /**
     * Return Matrix with row in place 'place' replaced with 'row'.
     * 
     * @param row new row in place 'place'.
     * @param place where new row is to be placed.
     * @requires no null or NaN inputs.
     * @throws IllegalArgumentException Iff 'row' < 0 || 'row' >= this.getN().
     * @return matrix with row replace.
     * 
     * O(getN()*getM())
    */
    public Matrix replaceRow(double[] row, int place){
        isARow(place);
        double[][] array = cloneArray(graph);
        for(int i = 0; i != row.length; i++){
            array[place][i] = row[i];
        }
        return new Matrix(array);
    }
    /**
     * Get number in place 'x' 'y'.
     * @spec.requires no NaN inputs.
     * 
     * O(1)
    */
    public double get(int x, int y){
        return graph[y][x];
    }
    /**
     * Throws exception iff dimensions of Matrix 'matrix' are not identical to the dimensions of this.
     * 
     * @throws IllegalArgumentException if matrix == null.
     * @throws IllegalArgumentException if matrix.getN() != getN() || matrix.getM != getM().
     * 
     * O(1)
    */
    private void checkInRange(Matrix matrix){
        checkNull(matrix);
        if(matrix.getN() != getN() || matrix.getM() != getM()){
            throw new IllegalArgumentException("Cannot preform opperation on matricies of differnt dimensions.\n mat.getN() = " + matrix.getN() + 
            " while this.getN() = "+getN()+" mat.getM() = "+ matrix.getM() + " while this.getM() = "+getM()+".");
        }
    }
    /**
     * Finds the dot product of this and 'matrix'.
     * 
     * @param matrix to find dot product with this.
     * @throws IllegalArgumentException if mat == null
     * @throws IllegalArgumentException if mat.getN() != this.getN() || mat.getM() != this.getM();
     * 
     * O(getN()*getM())
    */
    public double dotProduct(Matrix matrix){
        checkInRange(matrix);
        double total = 0;
        for(int i = 0; i != getN(); i++){
            for(int j = 0; j != getM(); j++){
                total += graph[i][j] * matrix.get(j, i);
            }
        }
        return total;
    }
    /**
     * Display the matrix in the terminal.
     * 
     * O(getN()*getM())
    */
    public void display(){
        for(int i = 0; i != getN(); i++){
            for(int j = 0; j != getM(); j++){
                System.out.print(graph[i][j]+" ");
            }
            System.out.println();
        }
    }
    /**
     * Return place of leading variable. If non exists, return -1.
     * 
     * @param row to find leadind variable
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException Iff 'row' < 0 || 'row' >= this.getN().
     * @return place of leading variable. If non exists, return -1.
     * 
     * O(getM())
    */
    public int findPlaceOfLeadingVariable(int row){
        double[] equation = getRow(row);
        for(int i = 0; i != getM(); i++){
            if(equation[i] != 0){
                return i;
            }
        }
        return -1;
    }
    /**
     * Check if input is a valid row.
     * 
     * @param row place of potential row.
     * @spec.requires no NaN inputs
     * @throws IllegalArgumentException Iff 'row' < 0 || 'row' >= this.getN().
     * 
     * O(1)
    */
    public void isARow(int row){
        if(row < 0 || row >= getN()){
            throw new IllegalArgumentException("Input row is "+row+" which is out of range. Should be between 0 and "+(getN()-1));
        }
    }
    /**
     * Return array in row 'row'.
     * 
     * @param row to find leadind variable
     * @spec.requires no NaN inputs
     * @throws IllegalArgumentException Iff 'row' < 0 || 'row' >= this.getN().
     * @return array in row 'row'.
     * 
     * O(getM())
    */
    public double[] getRow(int row){
        isARow(row);
        double[] goal = new double[getM()];
        for(int i = 0; i != getM(); i++){
            goal[i] = graph[row][i];
        }
        return goal;
    }
    /**
     * Return true if this is onto.
     * 
     * @return true iff this is onto.
    */
    public boolean isOnto(){
        // Test scaler
        return true;
    }
    /**
     * Return true if this is one-to-onr.
     * 
     * @return true iff this is one-to-one.
    */
    public boolean isOneToOne(){
        // Test scaler
        return true;
    }
    /**
     * Return a Matrix identical to this matrix, but in reduced echelon form.
     * //
    */
    public Matrix putInEchelonForm(){
        throw new RuntimeException("Method not Implemented yet!");
    }
    /**
     * Return a matrix with two equations swapped. The equation in place 'row_1' and and 'row_2'.
     * 
     * @param row_1 first row to to swap
     * @param row_2 second row to swap
     * @spec.requires no NaN inputs
     * @throws IllegalArgumentException Iff 'row' < 0 || 'row' >= this.getN().
     * @return matrix with swapped equations.
     * 
     * 
    */
    /*
    public Matrix swap(int row_1, int row_2){
        isARow(row_1);
        isARow(row_2);
        //Matrix goal = add();
        //double[] o = getRow(row_1);
        
        
    }
    */
    /**
     * Return number of elements in this matrix
     * 
     * @return number of elements in this matrix
     * 
     * O(1)
    */
    public int getSize(){
        return getN() * getM();
    }
    /**
     * Standard equality opperator.
    */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Matrix)) {
            return false;
        }
        Matrix matrix = (Matrix)obj;
        if(matrix.getN() != getN()){
            return false;
        }
        if(matrix.getM() != getM()){
            return false;
        }
        for(int i = 0; i != getM(); i++){
            for(int j = 0; j != getN(); j++){
                if(get(i, j) - matrix.get(i, j) > 0.001 || get(i, j) - matrix.get(i, j) < -0.001){
                    return false;
                }
            }
        }
        return true;
    }
    /***/
    @Override
    public int hashCode(){
        int total = 0;
        for(int i = 0; i != getM(); i++){
            for(int j = 0; j != getN(); j++){
                total += Math.pow(2,get(i, j))*Math.pow(3, i)*Math.pow(5, j);
            }
        }
        return total;
    }
}