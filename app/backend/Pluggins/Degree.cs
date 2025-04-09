using System.Text.Json;

using backend.Models;
using backend.Services;
using backend.Pluggins.Utilities;
using System.Text.RegularExpressions;

namespace backend.Pluggins
{
    public class ClassEntry
    {
        public string code { get; set; } = "";
    }

    public class ClassesData
    {
        public List<ClassEntry> classes { get; set; } = new List<ClassEntry>();
    }

    public class Move
    {
        public List<int> currState {get; set;}
        public List<int> move {get; set;}
        public List<int> parentState {get; set;}
        public List<int> moveCount {get; set;}
        public int g {get; set;}
        public int h {get; set;}

        public Move(List<int> cs, List<int> m, List<int> ps, List<int> mc, int g, int h){
            this.currState = cs;
            this.move = m;
            this.parentState = ps;
            this.moveCount = mc;
            this.g = g;
            this.h = h;
        }
    }
    
    public class Degree : IDegree
    {
        // helpers
        public Dictionary<string, List<Dictionary<string, string>>> ParseClassesJson(string jsonString){
            // Parse the JSON string
            ClassesData data = JsonSerializer.Deserialize<ClassesData>(jsonString) ?? new ClassesData { classes = new List<ClassEntry>() };
            
            // Create our result dictionary
            Dictionary<string, List<Dictionary<string, string>>> result = new Dictionary<string, List<Dictionary<string, string>>>();
            
            // Add the classes list as a list of dictionaries
            result["classes"] = new List<Dictionary<string, string>>();
            
            // Convert each class entry to a dictionary and add to our list
            foreach (var entry in data.classes)
            {
                Dictionary<string, string> classDict = new Dictionary<string, string>
                {
                    { "code", entry.code }
                };
                
                result["classes"].Add(classDict);
            }
            
            return result;
        }

        public string ParseMajorReqCourse(string majorReqJsonCourseStr)
        {
            // This function parse the JSON string of a course and produce a course string
            // for example COMP SCI/ MATH 240++COMP SCI/MATH 250
            // 1. We will split by either ++ or -- (we should only have one of them)
            // 2. Then for each splitted course, we will split by '/' and split the last piece again by ' ' to get the number out
            // 3. Sort them alphabetically ascending and take the first one
            // 4. Combine the string back together

            bool containsOr = majorReqJsonCourseStr.Contains("--");
            bool containsAnd = majorReqJsonCourseStr.Contains("++");
            List<string> splittedCourses = new List<string>();
            // if contains --
            if(containsOr){
                splittedCourses = majorReqJsonCourseStr.Split("--").ToList();
            }
            else if(containsAnd){
                splittedCourses = majorReqJsonCourseStr.Split("++").ToList();
            }else{  // there's no operation needed
                splittedCourses.Add(majorReqJsonCourseStr);
            }

            // for each courses we want to now go through and split by the '/' and save the course number
            // which is at the end
            List<string> cleanedCourses = new List<string>();
            foreach(string splittedCourse in splittedCourses){
                string[] prefixes = splittedCourse.Split("/");
                // if there's no '/' then we're good
                if(prefixes.Length == 1){
                    cleanedCourses.Add(splittedCourse);
                    continue;
                }
                string[] lastPrefixPieces = prefixes.Last().Split(' ');
                string courseNum = lastPrefixPieces.Last().Trim();
                // we want to exclude the numer for the last prefix
                prefixes[prefixes.Length - 1] = string.Join(" ", lastPrefixPieces.Take(lastPrefixPieces.Length - 1));
                Array.Sort(prefixes);
                string prefix = prefixes[0].Trim();
                cleanedCourses.Add($"{prefix} {courseNum}");
            }
            // we assemble them back
            if(containsAnd){
                return string.Join("++", cleanedCourses);
            }else if(containsOr){
                return string.Join("--", cleanedCourses);
            }else{
                return string.Join("", cleanedCourses);
            }
        }

        public string GetCoursePrefix(string courseCode){
            // 1. we replace ++ or -- with white space because we want to make sure when we split by
            // " " we will hit a number and whatever before that is prefix
            // E.g AFROAMER 222++AFROAMER 555 should still work
            string courseCodeWithoutSep = courseCode.Replace("++", " ");
            courseCodeWithoutSep = courseCodeWithoutSep.Replace("--", " ");
            List<string> prefix_pieces = new List<string>();
            foreach(string code in courseCodeWithoutSep.Split(" ")){
                if(code.All(char.IsNumber)){
                    break;
                }
                prefix_pieces.Add(code);
            }
            return string.Join(' ', prefix_pieces);
        }

        public void ProduceDictFromList(ref List<Course> courses,
                                        ref Dictionary<string, Course> codeToCourse){
            /**
            This function produces a dictionary given a list of courses

            - courses: The reference of the list of courses
            - codeToCourse: An empty dictionary to be populated
            */
            foreach(var course in courses){
                codeToCourse.Remove(course.Code);  // removes the value
                codeToCourse.Add(course.Code, course);
            }
        }

        public HashSet<string> RetrieveAllMajorReqCourseGroups(ref List<MajorRequirement> majorRequirements){
            /**
            This function will produce a list of all course group codes given a list of major requirements

            Parameters:
            - majorRequirements: The reference to the list of major requirements to parse
            */
            HashSet<string> result = new HashSet<string>();

            int lenMajorReq = majorRequirements.Count;
            for(int i = 0; i < lenMajorReq; i++){
                MajorRequirement majorReq = majorRequirements[i];
                Dictionary<string, List<Dictionary<string, string>>> requiredClasses = ParseClassesJson(majorReq.RequiredCourse);
                foreach (var classes in requiredClasses){
                    foreach(var course in classes.Value){
                        string cleanCourseCode = ParseMajorReqCourse(course["code"]);
                        string courseGroup = GetCoursePrefix(cleanCourseCode);
                        result.Add(courseGroup);
                    }
                }
            }

            return result;
        }

        public void ProcessMajorsRequirements(ref List<MajorRequirement> majorRequirements,
                                              ref Dictionary<string, Course> courseCodeToCourse,
                                              ref List<int> majorReqCredVector,
                                              ref Dictionary<string, List<int>> courseToCredVector){

            /**
            This function will go through the given major requirement list and compute the major requirement credit vectors
            and also construct a dictionary (hashmap) of key being a course's code and its value being its credit vector.

            - To know what is Credit Vector, please read README.md

            Parameters:
            - majorRequirements: A list containing object MajorRequirement (Read from db) and pass in by reference
            - majorReqCredVector: An empty list reference
            - courseToCredVector: An empty Hashmap of string to List<int>
            */
            int lenMajorReq = majorRequirements.Count;
            for(int i = 0; i < lenMajorReq; i++){
                MajorRequirement majorReq = majorRequirements[i];
                majorReqCredVector.Add(majorReq.Credits);  // push an int of the major req credits
                Dictionary<string, List<Dictionary<string, string>>> requiredClasses = ParseClassesJson(majorReq.RequiredCourse);
                // go through the requiredClasses and for each 
                // then for each element within each requiredClasses[key] we will ParseCourse on it
                // and use it put it into the courseToCredVector with a value pair that
                // should be an array of int, then set the i_th int to the requirement credits
                foreach (var classes in requiredClasses){
                    foreach(var course in classes.Value){
                        string cleanCourseCode = ParseMajorReqCourse(course["code"]);
                        if(!courseToCredVector.TryGetValue(cleanCourseCode, out var _curr_vec)){
                            List<int> credVector = Enumerable.Repeat(0, lenMajorReq).ToList();
                            courseToCredVector.Add(cleanCourseCode, credVector);
                        }
                        // not exist
                        courseToCredVector[cleanCourseCode][i] = courseCodeToCourse[cleanCourseCode].MaxCredits;
                    }
                }
            }
        }

        public void ProduceCredVectorToCourses(ref Dictionary<string, List<int>> courseToCredVector,
                                               ref Dictionary<string, Course> courseCodeToCourse,
                                               ref Dictionary<string, PriorityQueue<Course, int>> credVectorToCourses,
                                               ref List<List<int>> allCredVectors)
        {
            /**
            This function will compute a dictionary that maps a credit vector to a priority queue of courses comparing the number
            of requisites one course has (in another word, the number '()' for the requiste).

            Parameters:
            - courseToCredVector: Reference to a dictionary of course code to its credit vector (should obtain from calling ProcessMajorsRequirements)
            - courseCodeToCourse: Reference to a dictionary that maps course code to course object
            - credVectorToCourses: Reference to an empty dictionary of string to priority queue of Course comparing int
            - allCredVectors: Reference to an empty hashset which will be populated with all seen credit vectors

            Returns:
            - None if no exception occurs in the process of updating credVectorToCourses
            */
            //  Go through courseToCredVector and for each:
            //  a. we will convert its credit vector into a string by using ToPythonString method
            //  b. then we will check if credVectorToCourses contains that key already:
            //      i. if no, we will create an empty queue and add it
            //  c. add the course to the queue 

            foreach(var credicVectorKeyValuePair in courseToCredVector){
                string courseCode = credicVectorKeyValuePair.Key;
                List<int> creditVector = credicVectorKeyValuePair.Value;

                string creditVectorStringRep = creditVector.ToPythonString();

                Course course = courseCodeToCourse[courseCode];
                // so since we have a requisite string and that we know whenever '&&' occurs
                // it means that both left and right side course has to be satisfied
                // therefore there could be many or on either side but we know
                // that base on the logic, we will have at least number of '&&' + 1 
                // courses to satify before this course is satisfied
                int numReqCourse;
                if(course.RequisiteString.Trim().CompareTo("") == 0){
                    numReqCourse = 0;
                }else{
                    numReqCourse = Regex.Matches(course.RequisiteString, "&&").Count + 1;
                }

                if(!credVectorToCourses.TryGetValue(creditVectorStringRep, out var _curr_queue)){
                    credVectorToCourses.Add(creditVectorStringRep, new PriorityQueue<Course, int>());
                    allCredVectors.Add(creditVector);
                }
                credVectorToCourses[creditVectorStringRep].Enqueue(course, numReqCourse);
            }
            return;
        }
    }
}