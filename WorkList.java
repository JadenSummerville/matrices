import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Immutable. Jobs on top(width, M, [][X]), workers in the left(height, n, [X][]).
*/
public class WorkList {
    public final Matrix matrix;
    public WorkList(Matrix matrix){
        if(matrix.getN() != matrix.getM()){
            throw new IllegalArgumentException("Must be a A by A matrix, is "+matrix.getN()+" by "+matrix.getM()+" matrix.");
        }
        this.matrix = matrix;
    }
    public WorkList(double[][] matrix){
        Matrix m = new Matrix(matrix);
        if(matrix.length != matrix[0].length){
            throw new IllegalArgumentException("Must be a A by A matrix, is "+matrix.length+" by "+matrix[0].length+" matrix.");
        }
        this.matrix = m;
    }
    public WorkList(int size){
        this.matrix = new Matrix(size, size, true).abs();
    }
    public static void main(String[] args){
        double[][] array = new double[4][4];
        array[0][0] = 45.;
        array[0][1] = 38.0;
        array[0][2] = 30.0;
        array[0][3] = 22.0;

        array[1][0] = 35.;
        array[1][1] = 29.0;
        array[1][2] = 20.0;
        array[1][3] = 14.0;

        array[2][0] = 35.;
        array[2][1] = 29.0;
        array[2][2] = 20.0;
        array[2][3] = 14.0;

        array[3][0] = 27.;
        array[3][1] = 20.0;
        array[3][2] = 15.0;
        array[3][3] = 10.0;
        WorkList a = new WorkList(130);
        a.matrix.display();
        int[] arr = a.munkres();
        //int[] arr2 = a.optimizeBruteForce();

        for(int i = 0; i != a.matrix.getM(); i++){
            System.out.println(arr[i]);
        }
        //WorkList a = new WorkList(18);
        //a.matrix.display();
        //System.out.println();
        //int[] b  = a.branchAndBound(true);
        //int[] c = a.optimizeBruteForce();
        //for(int i = 0; i != a.matrix.getM(); i++){
        //    System.out.println(b[i]);
            //System.out.println(c[i]);
        //    System.out.println();
        //}
        //System.out.println(a.valueOfPath(b));
    }
    /**
     * Find most efficient solution optimizing for larger size
     * 
     * @return 
    */
    public int[] optimizeBruteForce(){
        Set<Integer> jobs = new HashSet<>();
        for(int i = 0; i != matrix.getM(); i++){
            jobs.add(i);
        }
        int[] board = new int[matrix.getM()];
        return optimizeBruteForce(jobs, board);
    }
    public static int[] cloneArray(int[] array){
        int[] goal = new int[array.length];
        for(int i = 0; i != array.length; i++){
            goal[i] = array[i];
        }
        return goal;
    }
    /**
     * Find what combination of unassigned jobs appended to the end of the assigned jobs in 'jobBoard' will give
     * the most efficient solution. Optimize for size.
     * 
     * @param unassignedJobs jobs to be added to the remainder of 'jobBoad'
     * @param jobBoard jobs that have been assigned up to 'numOfAssigned'. After 'numOfAssigned jobs' there are no promisses.
     * @param numOfJobs number of pre-assigned jobs from index-0
    */
    public int[] optimizeBruteForce(Set<Integer> unassignedJobs, int[] jobBoard){
        //Clone to avoid rep exposure
        jobBoard = cloneArray(jobBoard);
        int numOfAssigned = jobBoard.length - unassignedJobs.size();
        //If numOfJobs has assigned all jobs return
        if(numOfAssigned == jobBoard.length){
            return jobBoard;
        }
        //Set up maxValue and maxJobs
        Double maxValue = null;
        int[] maxJobs = null;
        for(Integer job: unassignedJobs){
            Set<Integer> restOfJobs = new HashSet<>(unassignedJobs);
            restOfJobs.remove(job);
            jobBoard[numOfAssigned] = job;
            int[] solution = optimizeBruteForce(restOfJobs, jobBoard);
            if(maxValue == null){
                maxValue = valueOfPath(solution);
                maxJobs = solution;
            }
            if(valueOfPath(solution) > maxValue){
                maxValue = valueOfPath(solution);
                maxJobs = solution;
            }
        }
        return maxJobs;
    }
    /***/
    public int[] branchAndBound(boolean max){
        if(max){
            WorkList work = new WorkList(matrix.add(-matrix.getMax()).scale(-1));
            return work.branchAndBound(false);
        }
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new NodeComparator());
        for(int i = 0; i != matrix.getN(); i++){
            priorityQueue.add(new Node(i));
        }
        while(!priorityQueue.isEmpty()){
            Node node = priorityQueue.remove();
            if(node.worker == matrix.getM()-1){
                return node.listPath();
            }
            Set<Node> nodes = node.getChildren();
            for(Node n: nodes){
                priorityQueue.add(n);
            }
        }
        throw new IllegalAccessError();
    }
    /**
     * Assign jobs and workers to minimize the values.
     * 
     * @return int array that represents the smallest selection of jobs and workers
    */
    public int[] munkres(){
        int[] goal = new int[matrix.getM()];
        double[][] matrix = this.matrix.getDoubleArray();
        boolean[][] marked = null;
        // for each entry matrix[i][j]-=max
        double max = this.matrix.getMax();
        for(int i = 0; i != matrix.length; i++){
            for(int j = 0; j != matrix.length; j++){
                matrix[i][j] *= -1;
                matrix[i][j] += max;
            }
        }
        // Row reduce
        for(int i = 0; i != matrix.length; i++){
            rowReduce(matrix, i);
        }
        // Column reduce
        for(int i = 0; i != matrix.length; i++){
            colunmReduce(matrix, i);
        }
        //
        Set<Integer> columns = null;
        Set<Integer> rows = null;
        int lines = 0;
        while(lines != goal.length){
            columns = new HashSet<>();
            rows = new HashSet<>();
            marked = new boolean[matrix.length][matrix.length];
            lines = 0;
            //
            for(int i = 0; i != matrix.length; i++){
                int zeroLocation = findZeroInRow(matrix, i, rows, columns);
                if(zeroLocation != -1){
                    marked[i][zeroLocation] = true;
                    columns.add(zeroLocation);
                    lines++;
                }
            }
            //
            for(int i = 0; i != matrix.length; i++){
                int zeroLocation = findZeroInColumn(matrix, i, rows, columns);
                if(zeroLocation != -1){
                    marked[zeroLocation][i] = true;
                    rows.add(zeroLocation);
                    lines++;
                }
            }
            while(!allZerosCoverted(matrix, rows, columns)){
                for(int i = 0; i < matrix.length; i++){
                    for(int j = 0; j != matrix.length && i < matrix.length; j++){
                        if(matrix[i][j] == 0 && !rows.contains(i) && !columns.contains(j)){
                            marked[i][j] = true;
                            columns.add(j);
                            lines++;

                            i++;
                            j = -1;
                        }
                    }
                }
            }
            if(lines != goal.length){
                double min = findMinOf(matrix, rows, columns);
                for(int i = 0; i != matrix.length; i++){
                    for(int j = 0; j != matrix.length; j++){
                        if(rows.contains(i) && columns.contains(j)){
                            matrix[i][j] += min;
                        }
                        else if(!rows.contains(i) && !columns.contains(j)){
                            matrix[i][j] -= min;
                        }
                    }
                }
            }
        }
        // Copy matrix into goal
        for(int i = 0; i != marked.length; i++){
            for(int j = 0; j != marked.length; j++){
                if(marked[i][j]){
                    goal[i] = j;
                }
            }
        }
        return goal;
    }
    private double findMinOf(double[][] matrix, Set<Integer> rows, Set<Integer> columns){
        double min = Double.MAX_VALUE;
        for(int i = 0; i != matrix.length; i++){
            for(int j = 0; j != matrix.length; j++){
                if(min > matrix[i][j] && !rows.contains(i) && !columns.contains(j)){
                    min = matrix[i][j];
                }
            }
        }
        return min;
    }
    private boolean allZerosCoverted(double[][] matrix, Set<Integer> rows, Set<Integer> columns){
        for(int i = 0; i != matrix.length; i++){
            for(int j = 0; j != matrix.length; j++){
                if(!rows.contains(i) && !columns.contains(j) && matrix[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Find a place to put a verticle line to cover the only 0 in row 'row'
     * and return -1 if there is more or less than 1 availible zero in that row.
     * 
     * @param matrix matrix to search for zero
     * @param row in 'matrix' to search for zero in
     * @param rows locations of all already existing rows
     * @param columns locations of all already existing columns
     * 
     * @requires no special intups (NaN, null)
     * @requires matrix.length > row >= 0
     * 
     * @return column of availible 0 or -1 if non exists
    */
    private static int findZeroInRow(double[][] matrix, int row, Set<Integer> rows, Set<Integer> columns){
        int zeroLocation = -1;
        if(rows.contains(row)){
            return zeroLocation;
        }
        for(int i = 0; i != matrix.length; i++){
            if(!columns.contains(i) && matrix[row][i] == 0){
                if(zeroLocation == -1){
                    zeroLocation = i;
                }else{
                    return -1;
                }
            }
        }
        return zeroLocation;
    }
    /**
     * Find a place to put a horizontile line to cover the only 0 in column 'column'
     * and return -1 if there is more or less than 1 availible zero in that column.
     * 
     * @param matrix matrix to search for zero
     * @param column in 'matrix' to search for zero in
     * @param rows locations of all already existing rows
     * @param columns locations of all already existing columns
     * 
     * @requires no special intups (NaN, null)
     * @requires matrix.length > column >= 0
     * 
     * @return row of availible 0 or -1 if non exists
    */
    private static int findZeroInColumn(double[][] matrix, int column, Set<Integer> rows, Set<Integer> columns){
        int zeroLocation = -1;
        if(columns.contains(column)){
            return zeroLocation;
        }
        for(int i = 0; i != matrix.length; i++){
            if(!rows.contains(i) && matrix[i][column] == 0){
                if(zeroLocation == -1){
                    zeroLocation = i;
                }else{
                    return -1;
                }
            }
        }
        return zeroLocation;
    }
    /**
     * Row reduce all items in row 'row'.
     * 
     * @param matrix matrix to row reduce
     * @param row row to row reduce
     * @requires matrix != null, row != NaN, matrix.length > row > 0, matrix[row][0]...matrix[row][matrix[row].length-1] >= 0, matrix.length == matrix[].length
     * @return matrix where we subtract the minimum value in row 'row' from all other
     * entries in row 'row'
    */
    private static void rowReduce(double[][] matrix, int row){
        double min = matrix[row][0];
        for(int i = 1; i != matrix.length; i++){
            if(min > matrix[row][i]){
                min = matrix[row][i];
            }
        }
        for(int i = 0; i != matrix.length; i++){
            matrix[row][i] -= min;
        }
    }
        /**
     * Row reduce all items in colunm 'colunm'.
     * 
     * @param matrix matrix to colunm reduce
     * @param colunm colunm to colunm reduce
     * @requires matrix != null, colunm != NaN, matrix.length > colunm > 0, matrix[0][colunm]...matrix[matrix.length-1][colunm] >= 0, matrix.length == matrix[].length
     * @return matrix where we subtract the minimum value in colunm 'colunm' from all other
     * entries in colunm 'colunm'
    */
    private static void colunmReduce(double[][] matrix, int colunm){
        double min = matrix[0][colunm];
        for(int i = 1; i != matrix.length; i++){
            if(min > matrix[i][colunm]){
                min = matrix[i][colunm];
            }
        }
        for(int i = 0; i != matrix.length; i++){
            matrix[i][colunm] -= min;
        }
    }
    /**
     * Calculate the productivity/cost of varius people working varius jobs. [person]->job
     * 
     * @param path the jobs each worker works.
     * @throws IllegalArgumentException iff path.length!=matrix.getM() || path == null
     * @spec.requires Each element of path is no more than matrix.getM()
     * @return productivity/cost of specified work assignments
    */
    public double valueOfPath(int[] path){
        if(path.length!=matrix.getM() || path == null){
            throw new IllegalArgumentException();
        }
        double total = 0;
        for(int i = 0; i != path.length; i++){
            total += matrix.get(path[i], i);
        }
        return total;
    }
    private static class NodeComparator implements Comparator<Node>{
        @Override
        public int compare(Node node1, Node node2){
            return (int)Double.compare(node1.getCost(), node2.getCost());
        }
    }
    /**
     * Immutable Node that keeps track of the worker assigned to a job and the previus node.
     * Nodes form lines that represent people working varius jobs.
    */
    private class Node implements Comparable<Node>{
        private final Node prev;
        private final int worker;
        private final double cost;
        private final int job;
        private final Set<Integer> possibleJobs;
        /**
         * Initiate a Node to represent the first worker working at job 'job'.
         * 
         * @param job the job that will be worked by this worker
        */
        public Node(int job){
            this.job = job;
            this.prev = null;
            worker = 0;
            cost = matrix.get(job, worker);
            possibleJobs = new HashSet<>();
            for(int i = 0; i != matrix.getM(); i++){
                possibleJobs.add(i);
            }
            possibleJobs.remove(job);
        }
        /**
         * Initiate a Node for the next worker after 'prev' with job 'job'.
         * 
         * @param job the job that is being worked
         * @param prev previus node for this path.
        */
        private Node(int job, Node prev){
            this.job = job;
            this.prev = prev;
            worker = prev.worker+1;
            cost = prev.getCost()+matrix.get(job, worker);
            possibleJobs = new HashSet<>(prev.possibleJobs);
            possibleJobs.remove(job);
        }
        /**
         * Create a new Node that forms the previus path plus the next job,
         * job by job 'job'.
        */
        public Node assignJob(int job){
            return new Node(job, this);
        }
        public int[] listPath(){
            int[] goal = new int[matrix.getM()];
            return listPath(goal, matrix.getM()-1);
        }
        public int[] listPath(int[] path, int i){
            path[i] = job;
            if(prev == null){
                return path;
            }
            return prev.listPath(path, i-1);
        }
        public double getCost(){
            return cost;
        }
        public int compareTo(Node other) {
            return Double.compare(getCost(), other.getCost());
        }
        public Set<Node> getChildren(){
            Set<Node> goal = new HashSet<>();
            for(Integer job: possibleJobs){
                goal.add(assignJob(job));
            }
            return goal;
        }
    }
}