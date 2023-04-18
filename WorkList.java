import java.util.Set;
import java.util.HashSet;

/**
 * Immutable. Jobs on top(wifth, M, [][X]), workers in the left(hieght, n, [X][]).
*/
public class WorkList {
    private static final int HashSet = 0;
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
        this.matrix = new Matrix(size, size, false);
    }
    public static void main(String[] args){
        WorkList a = new WorkList(3);
        a.matrix.display();
        int[] b = a.optimizeBruteForce();
        for(int i = 0; i != b.length; i++){
            System.out.println(b[i]);
        }
    }
    /**
     * Find most efficient solution oprimizing for larger size
    */
    public int[] optimizeBruteForce(){
        Set<Integer> jobs = new HashSet<>();
        for(int i = 0; i != matrix.getM(); i++){
            jobs.add(i);
        }
        int[] board = new int[matrix.getM()];
        return optimizeBruteForce(jobs, board, 0);
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
    public int[] optimizeBruteForce(Set<Integer> unassignedJobs, int[] jobBoard, int numOfAssigned){
        //Clone to avoid rep exposure
        jobBoard = cloneArray(jobBoard);
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
            int[] solution = optimizeBruteForce(restOfJobs, jobBoard, numOfAssigned+1);
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
            total += matrix.get(i,path[i]);
        }
        return total;
    }
}