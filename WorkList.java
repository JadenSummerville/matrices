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
        double[][] array = new double[3][3];
        array[0][0] = 2.0;
        array[0][1] = 8.0;
        array[0][2] = 2.0;
        array[1][0] = 10.0;
        array[1][1] = 4.0;
        array[1][2] = 10.0;
        array[2][0] = 10.0;
        array[2][1] = 9.0;
        array[2][2] = 2.0;
        Matrix m = new Matrix(array);
        //m.display();
        /*
    2.0 8.0 2.0 
    10.0 4.0 10.0
    10.0 9.0 2.0
        WorkList a = new WorkList(3);
        a.matrix.display();
        int[] b = a.optimizeBruteForce();
        System.out.println();
        System.out.println(a.valueOfPath(b));
        System.out.println();
        for(int i = 0; i != b.length; i++){
            System.out.println(b[i]);
        }
        */
        WorkList a = new WorkList(18);
        a.matrix.display();
        System.out.println();
        int[] b  = a.branchAndBound(true);
        //int[] c = a.optimizeBruteForce();
        for(int i = 0; i != a.matrix.getM(); i++){
            System.out.println(b[i]);
            //System.out.println(c[i]);
            System.out.println();
        }
        System.out.println(a.valueOfPath(b));
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
            return Integer.compare(getCost(), other.getCost());
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