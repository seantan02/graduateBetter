using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("student")] // Ensure it matches PostgreSQL's lowercase convention
    public class Student
    {
        [Column("id")]
        public int Id { get; set; }

        [Column("email")]
        public required string Email { get; set; }

        [Column("first_name")]
        public required string FirstName { get; set; }

        [Column("last_name")]
        public required string LastName { get; set; }
        public ICollection<Authentication> Authentication { get; set; } = new List<Authentication>();
    }
}