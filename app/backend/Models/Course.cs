using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("course")] // Ensure it matches PostgreSQL's lowercase convention
    public class Course
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("title")]
        public string? Title { get; set; }

        [Column("code")]
        public required string Code { get; set; }

        [Column("min_credits")]
        public int MinCredits { get; set; }

        [Column("max_credits")]
        public int MaxCredits { get; set; }

        [Column("requisite_string")]
        public string RequisiteString { get; set; } = "";

        [Column("course_group_id")]
        public int CourseGroupId { get; set; }
    }
}