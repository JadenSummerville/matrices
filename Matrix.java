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
        double[][] A = new double[3][3];
        A[0][0] = 1;
        A[1][0] = 0;
        A[2][0] = 1;
        A[0][1] = 0;
        A[1][1] = 1;
        A[2][1] = -1;
        A[0][2] = 1;
        A[1][2] = -1;
        A[2][2] = 1;
        Matrix a = new Matrix(A);
        //a.display();
        double[][] B = new double[3][3];
        B[0][0] = 0;
        B[1][0] = 1;
        B[2][0] = 1;
        B[0][1] = 1;
        B[1][1] = 0;
        B[2][1] = -1;
        B[0][2] = 1;
        B[1][2] = -1;
        B[2][2] = -1;
        Matrix b = new Matrix(B);
        double[][] C = new double[2][2];
        C[0][0] = 1;
        C[1][0] = 0;
        C[0][1] = 0;
        C[1][1] = 0;
        Matrix c = new Matrix(C);
        double[][] D = new double[2][2];
        D[0][0] = 1;
        D[1][0] = 0;
        D[0][1] = 0;
        D[1][1] = 1;
        Matrix d = new Matrix(D);
        //a.display();
        //b.display();
        d.matrixMultiply(c).display();
        //b.matrixMultiply(a.transpose()).display();;
        //a.power(4).display();
        //b.transpose().display();
    }
    /**
     * Return the matrix to the 'power' power of this. Return null if this does not exist.
     * 
     * @param power the power the matrix is put to
     * @return the matrix to the 'power' power of this. Return null if this does not exist (getM() != getN() or 'power' < 0).
    */
    public Matrix power(int power){
        if(power == 1){
            return this;
        }if(power == 0){
            return identityMatrix();
        }if(power < 0 || getM() != getN()){
            return null;
        }
        return power(power, this);
    }
    /**
     * Returns 'matrix' to the power of 'power'
     * 
     * @param power the power we are finding for matrix
     * @param matrix the matrix we are taking the power 'power' of.
     * @requires 'power' > 0
     * @requires 'matrix.getM()' == 'matrix.getN()'
     * @return the matrix 'matrix' to the 'power' power.
    */
    private Matrix power(int power, Matrix matrix){
        if(power == 1){
            return matrix;
        }if(power%2 == 1){
            return matrix.matrixMultiply(power(power-1, matrix));
        }
        return power(power/2, matrix.matrixMultiply(matrix));
    }
    /**
     * Return the transpose of 'this'.
     * 
     * @return transpose of 'this'.
    */
    public Matrix transpose(){
        double[][] transpose = new double[getM()][getN()];
        for(int i = 0; i != getM(); i++){
            for(int j = 0; j != getN(); j++){
                transpose[i][j] = graph[j][i];
            }
        }
        return new Matrix(transpose);
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
     * Get the identity matrix of 'this'.
     * 
     * @return The identity matrix of 'this'.
     * 
     *O(getM()^2)
    */
    public Matrix identityMatrix(){
        return identityMatrix(getM());
    }
    /**
     * Make an Identity matrix that is 'sideLength'x'sideLength'.
     * 
     * @param sideLength sideLength of the identity matrix.
     * @throws IllegalArgumentException iff sideLength <= 0.
     * @requires sideLength != NaN
     * @return the identity matrix 'sideLength'
    */
    public static Matrix identityMatrix(int sideLength){
        if(sideLength <= 0){
            throw new IllegalArgumentException("Side length must be atleast one.");
        }
        double[][] goal = new double[sideLength][sideLength];
        for(int i = 0; i != sideLength; i++){
            for(int j = 0; j != sideLength; j++){
                if(i == j){
                    goal[i][j] = 1;
                }else{
                    goal[i][j] = 0;
                }
            }
        }
        return new Matrix(goal);
    }
    /**
     * Return the result of matrix multiplication between this vector and 'matrix'.
     * 
     * @param matrix value to multiply this by.
     * @spec.requires no NaN inputs.
     * @throws IllegalArgumentException If not AXB times BX1 matricies.
     * @throws IllegalArgumentException If 'matrix' != null.
     * @return matrix multiple of this and 'matrix'.
     * 
     * O(getN()*getM())
    */
    public Matrix matrixMultiply(double[] matrix){
        checkNull(matrix);
        if(getM() != matrix.length){
            throw new IllegalArgumentException("not AXB times BX1 matricies.");
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
     * Returns the matrix multiple of two matrices. Returns null is this is impossible.
     * 
     * @param matrix value to multiply this by.
     * @throws IllegalArgumentException Iff 'matrix' == null.
     * @return matrix multiple of this and 'matrix'.
     * @return null if not AXB times BXC matricies
     * 
     * O(getN()*getM()*matrix.getN())
    */
    public Matrix matrixMultiply(Matrix matrix){
        checkNull(matrix);
        if(getM() != matrix.getN()){
            return null;
        }
        double[][] newGraph = new double[getN()][matrix.getM()];
        for(int k = 0; k != matrix.getM(); k++){
            for(int i = 0; i != getN(); i++){
                for(int j = 0; j != getM(); j++){
                    newGraph[i][k] += graph[i][j]*matrix.get(k, j);
                }
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
     * Find and return the inverse of 'this'.
     * 
     * @return the inverse of this, return null if none exists.
    */
    public Matrix inverse(){
        //Varify square matrix
        if(getM() != getN()){
            return null;
        }
        //Quick formula
        if(getM() == 2){
            // a b
            // c d
            double a = get(0, 0);
            double b = get(1, 0);
            double c = get(0, 1);
            double d = get(1, 1);
            // 1/(ad-bc)
            double scale = (a*d-b*c);
            if(scale < 0.0001 && scale > -0.0001){
                return null;
            }
            double[][] matrix = new double[2][2];

            matrix[0][0] = d;
            matrix[1][0] = -c;
            matrix[0][1] = -b;
            matrix[1][1] = a;

            return (new Matrix(matrix)).scale(1/scale);
        }
        throw new RuntimeException("Method not Implemented yet!");
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
     * (Pivot in every row)
     * 
     * @return true iff this is onto.
    */
    public boolean isOnto(){
        // Test scaler
        throw new RuntimeException("Method not Implemented yet!");
    }
    /**
     * Return true if this is one-to-one.
     * 
     * (Pivot in every column)
     * 
     * @return true iff this is one-to-one.
    */
    public boolean isOneToOne(){
        // Test scaler
        throw new RuntimeException("Method not Implemented yet!");
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
    public Matrix swap(int row_1, int row_2){
        isARow(row_1);
        isARow(row_2);
        double[][] matrix = cloneArray(graph);
        for(int i = 0; i != getM(); i++){
            double o = matrix[row_1][i];
            matrix[row_1][i] = matrix[row_2][i];
            matrix[row_2][i] = o;
        }
        return new Matrix(matrix);
    }
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