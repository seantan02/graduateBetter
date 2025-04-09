using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    [Table("authentication")] // Ensure it matches PostgreSQL's lowercase convention
    public class Authentication
    {
        [Column("id")]
        public int Id { get; set; }
        [Column("code")]
        public required string Code { get; set; }

        [Column("created_at")]
        public required DateTime CreatedAt { get; set; }
        [Column("student_id")]
        public int StudentId { get; set; }
        public Student ?Student { get; set; }
    }
    public class AuthenticationResponse
    {
        [Required]
        public string AccessToken { get; set; } = "";
        [Required]
        public string RefreshToken { get; set; } = "";
    }
    public class EmailVerificationRequest
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; } = "";
    }

    public class EmailVerification
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; } = "";

        [Required]
        public string Code { get; set; } = "";
    }
}