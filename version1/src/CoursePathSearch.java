import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

/**
 * This class will utilize the idea of A Search to construct a efficient algorithm to search for the shortest path
 */
public class CoursePathSearch {
    private HashSet<Course> courseList; //Assume every course in the list is unique
    private Set<Course> shortestCoursesPath;
    private HashMap<String, Course> codeToCourse;
    private HashMap<String, List<String>> courseToRequisite;
    private HashMap<String, Set<String>> requisiteToCourse;
    private List<List<Integer>> creditCombinationList;
    private HashMap<String, List<Integer>> creditCombination;
    private HashMap<String, Integer> creditCombinationCount;
    private HashMap<String, List<Course>> creditCombinationToCourses;
    private int[] startState; //we use an array to represent the target credit we need, for example: 5, 5, 5 which means 5 for cat 1, 5 for cat 2
    private int[] targetState; //we use an array to represent the target credit we want for each category, mostly all 0
    private HashMap<Integer, String> intToCategory;
    private HashMap<String, Integer> categoryToInt;
    private int categoryId = 0;
    private int numberOfCategories;

    /**
     * This is the subclass that will gives us the Real Overall Value of the course which is defined by
     * Credits spent - Credits gained (Including pre-requisites)
     * For example: If class A is 3 credits and has class B as requisite, both in total is 6 credits and satisfied 12 credits of our degree then it has 12-6 of ROV, +6.
     */
    class courseWithROV implements Comparable<courseWithROE>{
        private int[] state;
        private int realOverallValue;
        private List<courseWithROV> prerequisite;

        public courseWithROV(int[] _state){
            this.state = _state;
            this.realOverallValue = Integer.MIN_VALUE;
            this.prerequisite = new ArrayList<courseWithROV>();
        }
    }

    public CoursePathSearch(int _numberOfCategories){
        this.numberOfCategories = _numberOfCategories;
        this.courseList = new HashSet<Course>();
        this.shortestCoursesPath = new HashSet<Course>();
        this.codeToCourse = new HashMap<String, Course>();
        this.courseToRequisite = new HashMap<String, List<String>>();
        this.requisiteToCourse = new HashMap<String, Set<String>>();
        this.creditCombination = new HashMap<String, List<Integer>>();
        this.creditCombinationList = new ArrayList<List<Integer>>();
        this.creditCombinationCount = new HashMap<String, Integer>();
        this.creditCombinationToCourses = new HashMap<String, List<Course>>();
        this.intToCategory = new HashMap<Integer, String>();
        this.categoryToInt = new HashMap<String, Integer>();
    }
    public CoursePathSearch(){
        this(0);
    }

    //accessor
    public HashMap<String, Integer> getCategoryHashTable(){
        return this.categoryToInt;
    }
    public HashMap<Integer, String> getIntToCatHashTable(){
        return this.intToCategory;
    }
    public HashMap<String, List<Course>> getCreditCombinationPQ(){
        return this.creditCombinationToCourses;
    }
    public HashMap<String, Set<String>> getRequisiteToCourse(){
        return this.requisiteToCourse;
    }

    //mutator
    public void setTargetGoal(int[] _targetState){
        this.targetState = _targetState;
    }
    public void setStartGoal(int[] _startState){
        this.startState = _startState;
    }
    public void setNumberOfCategories(int _numberOfCategories){
        this.numberOfCategories = _numberOfCategories;
    }

    private List<Integer> computeMoveFromCourse(Course _course){
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0;i<this.categoryToInt.size();i++){ //fill the result up with zeros
            result.add(0);
        }

        if(_course.satisfiedCategories != null){
            for(String category : _course.satisfiedCategories){
                result.set(this.categoryToInt.get(category), _course.credits);
            }
        }
        
        return result;
    }

    //helper methods
    private int computeHCost(int[] _currentState, double scaler) throws IllegalStateException{
        if(this.targetState.length ==0 ) throw new IllegalStateException("Please first set target goal array before computing cost");
        if(_currentState.length ==0 ) throw new IllegalStateException("Please first set current goal array before computing cost");
        
        //compute by taking abs of end goal[i] - start goal[i]
        int cost = 0;
        for(int i=0;i<this.targetState.length;i++){
            int targetGoalValue = this.targetState[i];
            int currentStateValue = _currentState[i];
            cost += Math.abs(targetGoalValue-currentStateValue); 
        }
        return (int) ((double) cost * scaler);
    }

    /**
     * Because G Cost has to be a cost, we will make it the negative of our credits gain (ROV)
     * @param _currentState
     * @param _move
     * @return
     * @throws IllegalStateException
     */
    private int computeGCost(int[] _currentState, List<Integer> _move, double scaler) throws IllegalStateException{
        if(_move.size() ==0 ) throw new IllegalStateException("Next state cannot be length of 0");
        if(_currentState.length ==0 ) throw new IllegalStateException("Please first set current goal array before computing cost");
        //compute by taking the current state[i] - move[i]
        int gain = 0;
        int spend = -999;
        for(int i=0;i<_move.size();i++){
            int currentStateValue = _currentState[i];
            int moveValue = _move.get(i);
            if(spend==-999 && moveValue>0) spend = moveValue;
            gain += (currentStateValue-(currentStateValue-moveValue)); 
        }
        gain -= spend;
        return (int) -((double) gain * scaler);
    }

    /**
     * This method computes all possible successor with its G cost at last index
     * @param state
     * @return List of Integers representing the state and G cost at index length-1
     */
    private List<List<int[]>> getSuccessor(int[] state, HashMap<String, List<Integer>> moves){
        List<List<int[]>> successors = new ArrayList<List<int[]>>();
        //we want to use the Set setOfCourseCode to 
        for(HashMap.Entry<String,List<Integer>> move: moves.entrySet()){
            List<int[]> successor = new ArrayList<int[]>();
            int[] stateCloned = Arrays.copyOf(state, state.length);
            int[] moveTaken = Arrays.copyOf(state, state.length);
            List<Integer> moveValueList = move.getValue();
            int gCost = computeGCost(stateCloned, moveValueList, 1.0);
            for(int i=0;i<moveValueList.size();i++){
                int moveValue = 0;
                try{
                    moveValue =moveValueList.get(i);
                }catch(Exception e){
                    //pass
                }
                moveTaken[i] = moveValue;
                stateCloned[i] -= (moveValue <= stateCloned[i]) ? moveValue : stateCloned[i];
            }
            successor.add(stateCloned);
            int hCost = computeHCost(stateCloned, 0.5);
            int fCost = gCost+hCost;
            successor.add(new int[] {gCost, (int) hCost/2, fCost});
            successor.add(moveTaken);
            successors.add(successor);
        }
        Collections.sort(successors, Comparator.comparingInt(list -> list.get(1)[list.get(1).length - 1]));
        return successors;
    }

    public void addCourse(Course _course){
        this.courseList.add(_course);
        this.codeToCourse.put(_course.code, _course);
        for(List<String> courseRequisite: _course.preRequesites){
            this.courseToRequisite.put(_course.code, courseRequisite);
            for(String requisite:courseRequisite){
                Set<String> setCourse;
                if(this.requisiteToCourse.containsKey(requisite)){
                    setCourse = this.requisiteToCourse.get(requisite);
                    setCourse.add(_course.code);
                    this.requisiteToCourse.replace(requisite, setCourse);
                }else{
                    setCourse = new HashSet<String>();
                    setCourse.add(_course.code);
                    this.requisiteToCourse.put(requisite, setCourse);
                }
            }
        }

        //To make sure the arraylist contains enough length (0)
        int count=0;
        List<Integer> creditCombinationArray = new ArrayList<Integer>();
        while(count < this.numberOfCategories){
            creditCombinationArray.add(0);
            count++;
        }

        for(int i=0;i<_course.satisfiedCategories.size();i++){
            String satisfiedCategory = _course.satisfiedCategories.get(i);
            if(!this.categoryToInt.containsKey(satisfiedCategory)){
                this.categoryToInt.put(satisfiedCategory,this.categoryId);
                this.intToCategory.put(this.categoryId, satisfiedCategory);
                this.categoryId++; //make sure our category id is unique
            }
            //now for each satisfied category we will make it an array
            if(this.categoryToInt.get(satisfiedCategory)<creditCombinationArray.size()){
                creditCombinationArray.set(this.categoryToInt.get(satisfiedCategory), _course.credits);
            }else{
                creditCombinationArray.add(this.categoryToInt.get(satisfiedCategory), _course.credits);
            }
        }
        String creditCombinationString = creditCombinationArray.toString();
        if(this.creditCombination.containsKey(creditCombinationString)){
            int oldCount = this.creditCombinationCount.get(creditCombinationString);
            this.creditCombinationCount.replace(creditCombinationString, oldCount+1);
        }else{
            this.creditCombinationCount.put(creditCombinationString, 1);
        }

        this.creditCombination.put(creditCombinationString, creditCombinationArray);// have our combination set
        creditCombinationList.add(creditCombinationArray);
        
        if(this.creditCombinationToCourses.containsKey(creditCombinationString)){
            this.creditCombinationToCourses.get(creditCombinationString).add(_course);
        }else{
            List<Course> listOfCourse = new ArrayList<Course>();
            listOfCourse.add(_course);
            this.creditCombinationToCourses.put(creditCombinationString, listOfCourse);
        }
        
    }

    /**
     * This method description:
     * So instead of looking through all the courses, we create a satisfactory credit array that will tell us how many credits it satisfy in total.
     * For example, a 3 credits course that satisfy requirement A(index 0), B(index 1) out of A-F, then it will be [3, 3, 0, 0, 0, 0]
     * Then we have method that generate successors (Our next possible states sorted by total F cost)
     * F cost = G cost + H cost
     * G cost = ROV where we take current state and next state to check the overall value, e.g: [3,3,3,3,3,3] with [0,0,3,3,3,3] then we have 3 gains because we spend 3 credits and satisfy 6 credits, so in terms of cost, it will be -3 (flipped the gain)
     * H cost is our prediction to [0,0,0,0,0,0], which is just taking each absolute difference in our current state and target state ([0,0,0,0,0,0])
     * We then have a PQ that always give us the next best possible move in terms of F cost
     * We have visited set to keep track of what is visited too
     * Algo:
     * Pop from PQ and we check if it is visited, if not we add it to our visited set
     * Compute the sucessors
     * For each successors, we check if it is in visited, if yes, ignored.
     * If not in visited, we then check if it is in our PQ, if yes, we check if the cost is better, if not, ignored.
     * 
     * More implementation needed: 
     * We need to know have an efficient way of tracking down what credit combination is left to use because right now we assume each course or credit combination [x1, x2,....] is inifinite
     * We need to check the course that gives us the credit combination, and check if it has requisite, if it does, go to them and see if they will increase our ROV, if not we choose the one without requisite.
     * If the requisite maintain then we take it if it's the only choice, we will then take all requisite and this course together
     * 
     * @return
     */
    public List<List<int[]>> computeShortestPath(){
        List<List<int[]>> bestCreditComPath = new ArrayList<List<int[]>>();
        HashMap<String, List<Course>> possibleMoveToPossibleCourse = this.creditCombinationToCourses;
        List<HashMap<String, List<Integer>>> possibleMoves = new ArrayList<HashMap<String, List<Integer>>>();
        List<HashMap<String, Integer>> possibleCount = new ArrayList<HashMap<String, Integer>>();
        Set<String> visited = new HashSet<String>();
        PriorityQueue<List<int[]>> nextBestMoves = new PriorityQueue<>(Comparator.comparingInt(
                list -> list.get(1)[list.get(1).length - 1]
        ));
        HashMap<String, Integer> nextBestMovesCost = new HashMap<String, Integer>();

        int[] startState = this.startState;
        int g = 0;
        int h = computeHCost(startState, 1.0);
        int f = g+h;
        int parentIndex = -1;
        List<int[]> startMove = new ArrayList<int[]>();
        startMove.add(startState);
        startMove.add(new int[] {parentIndex, g, h, f});
        startMove.add(new int[] {0, 0, 0, 0, 0, 0});
        nextBestMoves.add(startMove);
        //initialize possible move
        possibleMoves.add(this.creditCombination);
        possibleCount.add(this.creditCombinationCount);

        int[] currentState = startState;
        //Algorithm search
        while(!Arrays.equals(currentState, this.targetState) && nextBestMoves.size() != 0){
            int bestTakeLimit = -1; //this tells the algorithm how many of the first best to add to queue

            List<int[]> nextBestMove = nextBestMoves.remove();
            int[] nextBestState = nextBestMove.get(0);
            int[] nextBestStateDetails = nextBestMove.get(1);
            int nextBestStateParentIndex = nextBestStateDetails[0];
            int[] moveTaken = nextBestMove.get(2);

            //add to visited
            if(!visited.contains(Arrays.toString(nextBestState))){
                //keep track of credit combo count
                if(!Arrays.equals(nextBestState, this.startState)){ //ignore if it is start
                    HashMap<String, List<Integer>> newPossibleMoves = deepCopyHashMapList(possibleMoves.get(nextBestStateParentIndex));
                    HashMap<String, Integer> newPossibleCount = deepCopyHashMap(possibleCount.get(nextBestStateParentIndex));
                    int oldCount = newPossibleCount.get(Arrays.toString(moveTaken));
                    if(oldCount == 1){
                        newPossibleMoves.remove(Arrays.toString(moveTaken));
                        newPossibleCount.remove(Arrays.toString(moveTaken));
                    }else{
                        newPossibleCount.replace(Arrays.toString(moveTaken), oldCount-1);
                    }

                    possibleMoves.add(newPossibleMoves);
                    possibleCount.add(newPossibleCount);
                }

                visited.add(Arrays.toString(nextBestState)); //add the next state after current operation
                bestCreditComPath.add(nextBestMove); //add the state to our best credit path as a record OPTIONAL
                parentIndex = bestCreditComPath.size()-1; //calculate parentIndex
            }

            HashMap<String, List<Integer>> possibleMove = possibleMoves.get(parentIndex);
            // System.out.println("Possible moves: "+possibleMove.size());
            List<List<int[]>> successors = getSuccessor(nextBestState, possibleMove);
            for(List<int[]> successor:successors){
                int[] successorDetail = new int[4];
                successorDetail[0] = parentIndex;
                System.arraycopy(successor.get(1), 0, successorDetail, 1, successor.get(1).length);
                successor.set(1, successorDetail);
                
                if(visited.contains(Arrays.toString(successor.get(0)))){ //Skip if this successor is already visited
                    continue; //go to next loop without adding to our PQ
                }

                if(!nextBestMoves.contains(successor)){
                    nextBestMoves.add(successor);
                    nextBestMovesCost.put(Arrays.toString(successor.get(0)), successor.get(1)[3]);
                }else{
                    if(nextBestMovesCost.get(Arrays.toString(successor.get(0))) > successor.get(1)[3]){
                        nextBestMoves.add(successor);
                        nextBestMovesCost.replace(Arrays.toString(successor.get(0)), successor.get(1)[3]);
                    }
                }
            }
            currentState = nextBestState;
        }
        return bestCreditComPath;
    }

    public List<Course> computeBestCourseFromPath(List<int[]> credComMove){
        HashMap<String, List<Course>> creditCombinationToCourses = this.creditCombinationToCourses;
        List<Course> result = new ArrayList<Course>();
        HashSet<String> visited = new HashSet<String>();
        HashMap<String, Integer> moveLeft = new HashMap<String, Integer>();

        for(int[] move: credComMove){ //compute the Hash Table that maps move to the remaining needed number
            String moveString = Arrays.toString(move);
            if(moveLeft.containsKey(moveString)){
                int oldCount = moveLeft.get(moveString);
                int newCount = oldCount+1;
                moveLeft.replace(moveString, newCount);
            }else{
                moveLeft.put(moveString, 1);
            }
        }

        for(int[] move: credComMove){
            String moveString = Arrays.toString(move);
            if(moveLeft.get(moveString) == 0) continue; //if this move is no longer needed
            if(creditCombinationToCourses.get(moveString) == null) continue;
            List<Course> bestCourseToTake = new ArrayList<Course>();
            int bestCourseCost = Integer.MAX_VALUE;
            for(Course course: creditCombinationToCourses.get(moveString)){
                List<Course> bestPreReqCombo = new ArrayList<Course>();
                int preReqComboCost = Integer.MAX_VALUE;
                if(visited.contains(course.code)) continue;
                if(course.preRequesites.get(0).size() == 0){ //no requisite
                    preReqComboCost = 0;
                }else{
                    List<List<String>> coursePreReqs = course.preRequesites;
                    for(List<String> coursePreReq : coursePreReqs){
                        Course preReqCourseToTake = null;
                        int currSmallestCost = Integer.MAX_VALUE;
                        for(String coursePreReqCode : coursePreReq){ 
                            //1. check if we have satisfied this requisite
                            //2. If yes, make the course to take null and move on
                            //3. If no, check if it is in our next move
                            //4. If yes, make it the course to add
                            if(visited.contains(coursePreReqCode)){
                                preReqCourseToTake = null;
                                break;
                            }
                            Course preReqCourse = this.codeToCourse.get(coursePreReqCode);
                            if(preReqCourse == null) throw new IllegalStateException("Course: "+coursePreReqCode+" doesn't seems to exist.");
                            List<Integer> preReqCourseMove = computeMoveFromCourse(preReqCourse);
                            if(moveLeft.containsKey(preReqCourseMove.toString())){
                                preReqCourseToTake = preReqCourse;
                                currSmallestCost = 0;
                            }else{
                                if(currSmallestCost > preReqCourse.credits){
                                    currSmallestCost = preReqCourse.credits;
                                    preReqCourseToTake = preReqCourse;
                                }
                            }
                        }
                        if(preReqCourseToTake != null && preReqComboCost > currSmallestCost){ //means we have found a req course to take
                            preReqComboCost = currSmallestCost;
                            bestPreReqCombo.add(preReqCourseToTake);
                        }
                    }
                }

                if(bestCourseCost > preReqComboCost){
                    bestCourseCost = preReqComboCost;
                    bestCourseToTake = bestPreReqCombo;
                    bestCourseToTake.add(course);
                }
            }
            //Now we add all of them
            for(Course bestCourse : bestCourseToTake){
                List<Integer> preReqCourseMove = computeMoveFromCourse(bestCourse);
                String preReqCourseMoveString = preReqCourseMove.toString();
                System.out.print("For move "+preReqCourseMoveString+", we take: "+bestCourse.code+"\n");
                if(moveLeft.get(preReqCourseMoveString)!=null){
                    int oldCount =  moveLeft.get(preReqCourseMoveString);
                    moveLeft.replace(preReqCourseMove.toString(), oldCount-1);
                }
                result.add(bestCourse);
                visited.add(bestCourse.code);
            }
            System.out.println();
        }
        return result;
    }

    private static HashMap<String, Integer> deepCopyHashMap(HashMap<String, Integer> original) {
        HashMap<String, Integer> copy = new HashMap<String, Integer>();
        for (HashMap.Entry<String, Integer> entry : original.entrySet()) {
            // Perform a deep copy of keys and values if they are complex objects
            String key = new String(entry.getKey());  // Copy the key
            Integer value = entry.getValue();  // Copy the value

            // Add the copied key-value pair to the new map
            copy.put(key, value);
        }
        return copy;
    }

    private static HashMap<String, List<Integer>> deepCopyHashMapList(HashMap<String, List<Integer>> original) {
        HashMap<String, List<Integer>> copy = new HashMap<>();

        for (HashMap.Entry<String, List<Integer>> entry : original.entrySet()) {
            // Perform a deep copy of the key
            String key = new String(entry.getKey());  // Copy the key

            // Perform a deep copy of the list
            List<Integer> originalList = entry.getValue();
            List<Integer> copyList = new ArrayList<>(originalList);

            // Add the copied key and list to the new map
            copy.put(key, copyList);
        }

        return copy;
    }

    public static void main(String[] args) {
        Dataset dataset = new Dataset();
        dataset.readFile("version1/prerequisites.csv");
        HashSet<Course> csCourses = dataset.filterCourseByPrefix("CS", 100);
        HashSet<Course> dsCourses = dataset.filterCourseByPrefix("STAT", 100);

        int numberOfDegreeRequirements = dataset.numberOfDegreeRequirement("CS");
        numberOfDegreeRequirements += dataset.numberOfDegreeRequirement("DS");

        CoursePathSearch courseSearch = new CoursePathSearch(numberOfDegreeRequirements);
        
        for(Course csCourse : csCourses){
            courseSearch.addCourse(csCourse);
            System.out.println(csCourse);
        } 
        for(Course dsCourse : dsCourses){
            courseSearch.addCourse(dsCourse);
            System.out.println(dsCourse);
        } 

        HashMap<String, Integer> categoryToIndex = courseSearch.getCategoryHashTable();
        int[] categoriesValues = new int[categoryToIndex.size()];
        for(String degree:dataset.getDegree()){
            for(String degreeRequirement: dataset.getDegreeRequirements().get(degree)){
                Random random = new Random();
                int randomNumber = random.nextInt(20, 25);
                int index = -1;
                try{
                    index = categoryToIndex.get(degreeRequirement);
                }catch(Exception e){
                }
                //If no category found
                if(index == -1){
                    System.out.println("No classes have satisfy "+degreeRequirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = randomNumber;
            }
        }

        System.out.println("Start state:");
        for(int categoriesValue : categoriesValues){
            System.out.print(categoriesValue+" ");
        }
        System.out.println();
        
        HashMap<Integer, String> intToCatHashMap = courseSearch.getIntToCatHashTable();
        System.out.print("Category ordered in index: ");
        for(Map.Entry<Integer, String> entry: intToCatHashMap.entrySet()){
            String value = entry.getValue();
            System.out.print(value+", ");
        }

        courseSearch.setStartGoal(categoriesValues);
        int[] targetState = new int[categoriesValues.length];
        courseSearch.setTargetGoal(targetState);
        System.out.println("\n");

        List<List<int[]>> allCreComPath = courseSearch.computeShortestPath(); //This computes the big paths
        List<List<int[]>> bestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
        List<int[]> bestCreComList = new ArrayList<int[]>(); //The best moves to take in String
        int i = allCreComPath.size()-1; //Set i as the final (When we reach end goal)
        while(i>=0){// when i less than 0 means we get to the start state so stop
            List<int[]> creComPath = allCreComPath.get(i); 
            int parentIndex = creComPath.get(1)[0]; //Get the parent index and trace it back
            bestCreComPath.add(creComPath);
            bestCreComList.add(creComPath.get(2));
            i=parentIndex;
        }

        List<Course> bestCourseToTake = courseSearch.computeBestCourseFromPath(bestCreComList);
        for(Course bestCourse: bestCourseToTake){
            System.out.println(bestCourse.code);
        }
    }
}
