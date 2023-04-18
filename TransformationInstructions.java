import java.util.ArrayList;

/**
 * TransformationInstructions is a mutable list of opperations. These opperations can include
 * swap (awap two rows in a matrix), add (add a row to anouther rwo in a matrix), or scale (scale a row in a matrix).
 * Swap is 0, add is 1, and scale is 2.
*/
public class TransformationInstructions {
    private ArrayList<Instruction> list;
    TransformationInstructions(){
        list = new ArrayList<>();
    }
    public Matrix applyToMatrix(Matrix matrix){
        for(int i = 0; i != list.size(); i++){
            matrix = list.get(i).preformOpperation(matrix);
        }
        return matrix;
    }
    public void appendSwapInstruction(int row_1, int row_2){
        Swap a = new Swap(row_1, row_2, 0.);
        list.add(a);
    }
    public void appendAddInstruction(int row_1, int row_2, double scale){
        Add a = new Add(row_1, row_2, scale);
        list.add(a);
    }
    public void appendScaleInstruction(int row_1, double scale){
        Scale a = new Scale(row_1, 0, scale);
        list.add(a);
    }
    private class Instruction{
        private final int placeOfRowBeingChanged;
        private final int other;
        private final double scale;
        Instruction(int place, int other, double scale){
            placeOfRowBeingChanged = place;
            this.other = other;
            this.scale = scale;
        }
        public Matrix preformOpperation(Matrix matrix){
            throw new RuntimeException("This method is to only be run on subclasses of Instruction.");
        }
        public static void main(String[] args){
            //
        }
    }
    private class Swap extends Instruction{
        Swap(int place, int other, double scale){
            super(place, other, scale);
        }
        @Override
        public Matrix preformOpperation(Matrix matrix){
            System.out.println("");
            return matrix;
        }
    }
    private class Add extends Instruction{
        Add(int place, int other, double scale){
            super(place, other, scale);
        }
        @Override
        public Matrix preformOpperation(Matrix matrix){
            System.out.println("");
            return matrix;
        }
    }
    private class Scale extends Instruction{
        Scale(int place, int other, double scale){
            super(place, other, scale);
        }
        @Override
        public Matrix preformOpperation(Matrix matrix){
            System.out.println("");
            return matrix;
        }
    }
}
