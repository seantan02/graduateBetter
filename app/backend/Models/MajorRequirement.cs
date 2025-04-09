using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("major_requirement")] // Ensure it matches PostgreSQL's lowercase convention
    public class MajorRequirement
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("number")]
        public int Number { get; set; }

        [Column("credits")]
        public int Credits { get; set; }

        [Column("required_course", TypeName = "jsonb")]
        public string RequiredCourse { get; set; } = "{}";

        [Column("major_id")]
        public int MajorId { get; set; }

        public required Major MajorParent { get; set; }
    }
}