using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("major")] // Ensure it matches PostgreSQL's lowercase convention
    public class Major
    {
        [Column("id")]
        public int Id { get; set; }
        
        [Column("code")]
        public string Code { get; set; } = string.Empty;

        [Column("title")]
        public string Title { get; set; } = string.Empty;

        public ICollection<MajorRequirement> MajorRequirements { get; set; } = new List<MajorRequirement>();
    }
}