using backend.Models;

namespace backend.Pluggins
{
    public interface IDegree
    {
        Dictionary<string, List<Dictionary<string, string>>> ParseClassesJson(string jsonString);
        string ParseMajorReqCourse(string majorReqJsonStr);

        void ProduceDictFromList(ref List<Course> courses,
                                 ref Dictionary<string, Course> codeToCourse);

        HashSet<string> RetrieveAllMajorReqCourseGroups(ref List<MajorRequirement> majorRequirements);

        void ProcessMajorsRequirements(ref List<MajorRequirement> majorRequirements,
                                       ref Dictionary<string, Course> courseCodeToCourse,
                                       ref List<int> majorReqCredVector,
                                       ref Dictionary<string, List<int>> courseToCredVector);
        void ProduceCredVectorToCourses(ref Dictionary<string, List<int>> courseToCredVector,
                                        ref Dictionary<string, Course> courseCodeToCourse,
                                        ref Dictionary<string, PriorityQueue<Course, int>> credVectorToCourses,
                                        ref List<List<int>> allCredVectors);
        // Other methods your plugin will implement
    }
}