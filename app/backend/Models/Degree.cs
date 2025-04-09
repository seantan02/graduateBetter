namespace backend.Models
{
    public class DegreeRouteRequest
    {
        public required List<int> MajorIds { get; set; }

        public required List<int> CourseTakenIds {get; set;}
    }
}