using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("course_group")] // Ensure it matches PostgreSQL's lowercase convention
    public class CourseGroup
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("code")]
        public required string Code { get; set; }

        [Column("title")]
        public string? Title { get; set; }

        public ICollection<Course> Courses { get; set; } = new List<Course>();
    }
}