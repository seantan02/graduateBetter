using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

using backend.Data;
using backend.Models;
using backend.Pluggins;
using backend.Pluggins.Utilities;
using System.Text.RegularExpressions;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DegreeController : ControllerBase
    {
        private readonly AppDbContext _context;

        public DegreeController(AppDbContext context)
        {
            _context = context;
        }

        /* POST: api/degree/shortest-route
        <summary>
        Computes the shortest route to fulfill degree requirements based on the user's selected majors and courses already taken.
        </summary>

        <param name="requestBody">
        A <see cref="DegreeRouteRequest"/> object containing the following:
        <list type="bullet">
        <item><description><c>MajorIds</c>: A list of major IDs selected by the user.</description></item>
        <item><description><c>CourseTakenIds</c>: A list of course IDs that the user has already completed.</description></item>
        </list>
        </param>

        <returns>
        An <see cref="ActionResult"/> containing an <see cref="IEnumerable{Course}"/> representing the list of courses 
        required to fulfill the degree requirements in the shortest path.
        </returns>

        <remarks>
        This method uses the A* search algorithm to compute the optimal path to satisfy the degree requirements. 
        It considers the following:
        <list type="bullet">
        <item><description>Major requirements and their associated course groups.</description></item>
        <item><description>Courses already taken by the user.</description></item>
        <item><description>Credit vectors representing the requirements for each major.</description></item>
        </list>
        The algorithm calculates the shortest path by minimizing the G cost (credit move cost) and H cost 
        (remaining requirement major requirement credit vector sum). Credit vector is an array representing
        the number of credits in each requirement category. Major requirement credit vector is the initial
        number of credits we need for each category given all majors' requirement category. Then for each course
        there's an associated credit vector which is the number of credits it satisfy for each category.
        E.g: Major requirement vector [1,2,3,4] means 1,2,3,4 credits required in category 1,2,3,4 respectively.
        E.g: Course credit vector [0,0,1,1] means this course satisfy category 3, 4 each by 1 credits.
        </remarks>

        <exception cref="Exception">
        Thrown if the computation exceeds the 150,000 search in open list of our A* algo or if an 
        invalid state is encountered during processing.
        </exception> */
        [HttpPost("shortest-route")]
        [Authorize]
        public async Task<ActionResult<IEnumerable<Course>>> ComputeShortestRoute([FromBody] DegreeRouteRequest requestBody)
        {
            /* Bearer Token Validation for now */
            var user = HttpContext.User;
            var tokenType = user.FindFirst("token_type")?.Value;
            var email = user.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if(tokenType == null || email == null || tokenType.CompareTo("access_token") != 0){ return StatusCode(401, null); }
            Student ?student = await _context.Student
                .Where(s => s.Email.CompareTo(email) == 0)
                .FirstOrDefaultAsync();
            if(student == null){ return StatusCode(401, null); }
            /* Bearer Token Validation for now */

            int DEBUG = 0;
            HashSet<int> majorIdsHashSet = requestBody.MajorIds.ToHashSet();
            HashSet<int> coursesIdUserHasTaken = requestBody.CourseTakenIds.ToHashSet();

            List<MajorRequirement> requirements = await _context.MajorRequirements
                                                                    .Where(m => majorIdsHashSet.Contains(m.MajorId))
                                                                    .ToListAsync();
            Degree degree = new Degree();
            // retrieve all course group to select
            HashSet<string> courseGroupNeeded = degree.RetrieveAllMajorReqCourseGroups(ref requirements);
            // select all courses
            // First, get the course groups based on the codes
            List<int> courseGroupIds = await _context.CourseGroups
                                                           .Where(cg => courseGroupNeeded.Contains(cg.Code))
                                                           .Select(cg => cg.Id)
                                                           .ToListAsync();
            // Then, get the courses for those course groups
            List<Course> courses = await _context.Courses
                                                 .Where(c => courseGroupIds.Contains(c.CourseGroupId))
                                                 .ToListAsync();
            // Retrive the classes user have taken
            List<Course> coursesTaken = await _context.Courses
                                                 .Where(c => coursesIdUserHasTaken.Contains(c.Id))
                                                 .ToListAsync();
            // a hashmap that maps course code -> course
            Dictionary<string, Course> courseCodeToCourse = new Dictionary<string, Course>();
            degree.ProduceDictFromList(ref courses, ref courseCodeToCourse);
            // produce major requirement credit vector, courseToCredVector and credVectorToCourses
            List<int> majorReqCredVector = new List<int>();
            Dictionary<string, List<int>> courseToCredVector = new Dictionary<string, List<int>>();
            degree.ProcessMajorsRequirements(ref requirements, ref courseCodeToCourse, ref majorReqCredVector, ref courseToCredVector);
            Dictionary<string, PriorityQueue<Course, int>> credVectorToCourse = new Dictionary<string, PriorityQueue<Course, int>>();
            List<List<int>> allCredVectors = new List<List<int>>();
            degree.ProduceCredVectorToCourses(ref courseToCredVector, ref courseCodeToCourse, ref credVectorToCourse, ref allCredVectors);
            // now we have all the pieces we need, we can go ahead and compute our 'shortest path'
            /**
            A* Search Algorithm
            - target is all [0,0,0,0....] for our major requirement credit vector
            - G cost is the move credit (sum all number in list / # non zeros)
            - H cost is the new requirement credit vector sum
            */
            // we need to compute a double linked list of moves with a count so that we can 
            // keep track of which move is still available
            List<int> credVectorCount = new List<int>();  // last element of the int is the count
            Dictionary<string, int> credVectorToIndex = new Dictionary<string, int>();
            for(int index = 0; index < allCredVectors.Count; index++){  // populate available moves
                List<int> credVector = allCredVectors[index];
                string credVectorString = credVector.ToPythonString();
                credVectorCount.Add(credVectorToCourse[credVectorString].Count);
                credVectorToIndex.Add(credVectorString, index);
                if(DEBUG == 1) { Console.WriteLine($"For {credVectorString} we have {credVectorToCourse[credVectorString].Count}"); }
            }
            PriorityQueue<Move, int> openList = new PriorityQueue<Move, int>();
            Dictionary<string, Move> closedList = new Dictionary<string, Move>();
            Move? lastMove = null;
            // starting state should be original state - all states of whatever courses user have taken if it exists
            // otherwise ignore
            List<int> startingState = majorReqCredVector.ToList();
            foreach(Course courseHasTaken in coursesTaken){
                // course code of what user has taken doens't apply
                if(!courseCodeToCourse.ContainsKey(courseHasTaken.Code)){
                    continue;
                }
                // if this class has no cred vector then skip
                if(!courseToCredVector.ContainsKey(courseHasTaken.Code)){
                    continue;
                }
                List<int> credVector = courseToCredVector[courseHasTaken.Code];
                startingState = startingState.Zip(credVector, (int a, int b) => {
                    if(a > b) return a - b;
                    return 0;
                }).ToList();
                // we want to now then decrement the available move of that from the credVectorCount
                credVectorCount[credVectorToIndex[credVector.ToPythonString()]]--;
            }
            // construct initial move
            Move startingMove = new Move(startingState,
                                         Enumerable.Repeat(0, majorReqCredVector.Count).ToList(),
                                         new List<int>(),
                                         credVectorCount.ToList(),
                                         0,
                                         majorReqCredVector.Sum());
            openList.Enqueue(startingMove, startingMove.g + startingMove.h);
            if(DEBUG == 1){ Console.WriteLine($"Starting State: {startingState.ToPythonString()}"); }
            int i = 0;
            int pathDiscoveryCount = -1;
            List<int> closestGoalIfFailed = new List<int>();
            while(openList.TryDequeue(out var nextMove, out int nextCost)){
                // Console.WriteLine($"Selected next move: {nextMove.currState.ToPythonString()}");
                if(closestGoalIfFailed.Count == 0 || nextMove.currState.Sum() < closestGoalIfFailed.Sum()){
                    closestGoalIfFailed = nextMove.currState;
                }
                // to prevent forever computation, we want to break it after 10000 computation
                if(pathDiscoveryCount++ > 250000){
                    if(DEBUG == 1){  Console.WriteLine($"The closest state was {closestGoalIfFailed.ToPythonString()}"); }
                    throw new Exception("Degree Controller: Due to limitation of our computing resources, this path is too expensive to compute, " +
                                        "please select less majors/minors and resubmit the request! Thanks!");
                }

                lastMove = nextMove;
                if(nextMove.currState.Sum() == 0) break;  // at the goal so we're done
                // skip if we already processed this state
                if(closedList.TryGetValue(nextMove.currState.ToPythonString(), out var seenMove)){
                    if(seenMove.g <= nextMove.g){
                        continue;
                    }
                }
                // generate successors by going through each possible credit vector (move)
                i = 0;
                for(; i < allCredVectors.Count; i++){
                    // if the move count of successor exceeds available then we skip
                    if(nextMove.moveCount[i] <= 0){ continue; }
                    // we are good to use this move
                    List<int> potentialMove = allCredVectors[i];
                    List<int> successorState = nextMove.currState.Zip(potentialMove, (a, b) => {
                        if(a > b) return a-b;
                        return 0;
                    }).ToList();  // successor state can not be below 0
                    int g = nextMove.g + potentialMove.FirstOrDefault(n => n > 0);
                    int h = successorState.Sum() * 115 / 100;  // to make it converge quicker
                    // update move count
                    List<int> newMoveCount = [.. nextMove.moveCount];
                    if(--newMoveCount[i] < 0) {
                        if(DEBUG == 1){ Console.WriteLine("ERROR: New move count below zero!");}
                        throw new Exception("ERROR: New move count below zero");
                    }
                    openList.Enqueue(new Move(successorState, potentialMove, nextMove.currState, newMoveCount, g, h), g+h);
                }
                // add nextMove to closed list as we are done
                if(!closedList.TryAdd(nextMove.currState.ToPythonString(), nextMove)){
                    closedList[nextMove.currState.ToPythonString()] = nextMove;
                }
            }
            if(DEBUG == 1){ Console.WriteLine($"Iteration number: {pathDiscoveryCount}");}
            // now we will retrieve the optimal moves
            List<List<int>> finalizedMoves = new List<List<int>>();
            //go from the last one by tracking its parent
            if(lastMove != null){
                Move selectedMove = lastMove;
                while(true){
                    if(selectedMove.move.Sum() == 0) { break; }  // if this is the starting move
                    finalizedMoves.Add(selectedMove.move);
                    selectedMove = closedList[selectedMove.parentState.ToPythonString()];
                }
            }
            HashSet<string> finalizedCoursesCode = new HashSet<string>();
            List<Course> finalizedCourses = new List<Course>();
            // now using the computed finalized moved we will go in and select courses out using priority queue
            // at the same time we will create a hashset of selected courses to make it easier later for checking
            // if a course's requisite course is already satisfied
            foreach(List<int> finalizedMove in finalizedMoves){
                if(DEBUG == 1){ Console.WriteLine($"Selected moves: {finalizedMove.ToPythonString()}");}
                // NOTE: We could randomize the selection of course if there's requisite too 
                // for now we will just assume pq gives the optimal
                Course selectedCourse = credVectorToCourse[finalizedMove.ToPythonString()].Dequeue();
                finalizedCourses.Add(selectedCourse);
                finalizedCoursesCode.Add(selectedCourse.Code);
            }
            // Note that we do above loop first because we want the complete list of courses before we 
            // check if a course is already taken given a requisite
            // We will repeat this process for X amount of times because due to randomness, more repeat => better outcome
            Random rnd = new Random();
            int lowestCredits = 9999;
            HashSet<string> bestFinalizedCoursesCode = new HashSet<string>();
            List<Course> bestFinalizedCourses = new List<Course>();
            for(int r = 0; r < 100; r++){
                int totalReqCredits = 0;
                HashSet<string> tempFinalizedCoursesCode = finalizedCoursesCode.ToHashSet();
                List<Course> tempFinalizedCourses = finalizedCourses.ToList();
                i = -1;
                try{
                    while(i < tempFinalizedCourses.Count - 1){  // we want to make sure i increments everytime
                        i++;
                        Course finalizedCourse = tempFinalizedCourses[i];
                        // if course has requisite to satisfy:
                        // randomly pick the requisite course over 1000 times
                        // and then pick the ones with lowest cost
                        if(finalizedCourse.RequisiteString.Trim().CompareTo("") == 0){ continue; }  // no requisite
                        // our requisite string is in a CNF format so we just split by &&
                        // then for each clause, create a hashset of course code surrounded by ''
                        List<HashSet<string>> requisites = new List<HashSet<string>>();
                        List<string> requisiteAndCourses = finalizedCourse.RequisiteString.Split("&&").ToList();
                        foreach(string requisiteAndCourse in requisiteAndCourses){
                            // we want to create a hashset of everything enclosed by ''
                            requisites.Add(Regex.Matches(requisiteAndCourse, @"'([^']*)'")
                                                .Select(match => match.Groups[1].Value)
                                                .ToHashSet());
                        }
                        // now we check if we have satisfied the requisite
                        // since we are adding the randomly selected course to the finalizedCourses
                        // the Count of the finalizedCourses should increment and allow us to go through
                        // the newly added courses too
                        foreach(HashSet<string> requisiteList in requisites){
                            bool requisiteListSatisfied = false;
                            foreach(string potentialRequisiteCourse in requisiteList){
                                // we skip this list if user has completed it
                                if(courseCodeToCourse.ContainsKey(potentialRequisiteCourse)){
                                    if(coursesIdUserHasTaken.Contains(courseCodeToCourse[potentialRequisiteCourse].Id)){
                                        requisiteListSatisfied = true;
                                        break;
                                    }
                                }
                                // skip it if we are already taking it
                                if(tempFinalizedCoursesCode.Contains(potentialRequisiteCourse)){
                                    requisiteListSatisfied = true;
                                    break;
                                }
                            }
                            // randomly pick one course and add it, we should be going through the selected course as it will be the last one now
                            if(!requisiteListSatisfied){
                                int whileLoopCount = 0;
                                string randomSelectedCourseCode = requisiteList.ElementAt(rnd.Next(0, requisiteList.Count));
                                while(whileLoopCount++ < 10 && !courseCodeToCourse.TryGetValue(randomSelectedCourseCode, out var _)){
                                    Console.WriteLine($"requisite for course : {finalizedCourse.Code} and requisite selected: {randomSelectedCourseCode}");
                                    randomSelectedCourseCode = requisiteList.ElementAt(rnd.Next(0, requisiteList.Count));
                                }
                                if(courseCodeToCourse.TryGetValue(randomSelectedCourseCode, out var reqCourseToTake)){
                                    // if we get here that means we have a valid requisite course to take
                                    tempFinalizedCourses.Add(reqCourseToTake);
                                    tempFinalizedCoursesCode.Add(randomSelectedCourseCode);
                                    // add the credits
                                    totalReqCredits += reqCourseToTake.MaxCredits;
                                }else{
                                    throw new Exception("Not requisite can be found and used!"); 
                                }
                            }
                        }
                    }
                    // take it if it's the lowest credits we have seen so far
                    if(totalReqCredits < lowestCredits){
                        Console.WriteLine($"Found a lower credits at : {totalReqCredits}");
                        lowestCredits = totalReqCredits;
                        bestFinalizedCoursesCode = tempFinalizedCoursesCode;
                        bestFinalizedCourses = tempFinalizedCourses;
                    }
                }catch (Exception e){  // something is wrong so we just skip
                    if(DEBUG == 1) Console.WriteLine($"Error found while picking requisite courses: {e}");
                    continue;
                }
            }
            // now we add the taken courses to the list
            foreach(Course courseTaken in coursesTaken){
                bestFinalizedCourses.Add(courseTaken);
            }

            return bestFinalizedCourses;
        }
    }
}