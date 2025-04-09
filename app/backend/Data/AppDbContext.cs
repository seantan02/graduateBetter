using Microsoft.EntityFrameworkCore;
using backend.Models;

namespace backend.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<Major> Majors { get; set; } = null!;
        public DbSet<MajorRequirement> MajorRequirements { get; set; } = null!;

        public DbSet<CourseGroup> CourseGroups { get; set; } = null!;
        public DbSet<Course> Courses { get; set; } = null!;
        public DbSet<Authentication> Authentication { get; set; } = null!;
        public DbSet<Student> Student { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Configure entities if needed
            // Major
            modelBuilder.Entity<Major>()
                .HasKey(p => p.Id);

            modelBuilder.Entity<Major>()
                .Property(p => p.Code)
                .IsRequired();

            // Major Requirements
            modelBuilder.Entity<MajorRequirement>()
                .HasKey(p => p.Id);

            modelBuilder.Entity<MajorRequirement>()
                .Property(p => p.MajorId).IsRequired();

            modelBuilder.Entity<MajorRequirement>()
                .Property(p => p.Number).IsRequired();

            modelBuilder.Entity<MajorRequirement>()
                .Property(p => p.Credits).IsRequired();
            
            modelBuilder.Entity<MajorRequirement>()
                .Property(p => p.RequiredCourse)
                .HasColumnType("jsonb");

            modelBuilder.Entity<MajorRequirement>()
                .HasOne(p => p.MajorParent)
                .WithMany(p => p.MajorRequirements)
                .HasForeignKey(p => p.MajorId)
                .OnDelete(DeleteBehavior.Cascade);

            // Course Group
            modelBuilder.Entity<CourseGroup>()
                .HasKey(p => p.Id);

            modelBuilder.Entity<CourseGroup>()
                .Property(p => p.Code).IsRequired();

            modelBuilder.Entity<CourseGroup>()
                .Property(p => p.Title).IsRequired();

            // Course
            modelBuilder.Entity<Course>()
                .HasKey(p => p.Id);

            modelBuilder.Entity<Course>()
                .Property(p => p.CourseGroupId).IsRequired();

            modelBuilder.Entity<Course>()
                .Property(p => p.Code).IsRequired();

            modelBuilder.Entity<Course>()
                .Property(p => p.MinCredits).IsRequired();

            modelBuilder.Entity<Course>()
                .Property(p => p.MaxCredits).IsRequired();

            modelBuilder.Entity<Course>()
                .HasOne<CourseGroup>()
                .WithMany(p => p.Courses)
                .HasForeignKey(p => p.CourseGroupId)
                .OnDelete(DeleteBehavior.Cascade);

            // Authentication
            modelBuilder.Entity<Authentication>()
                .HasKey(a => a.Id);
            modelBuilder.Entity<Authentication>()
                .Property(a => a.Code).IsRequired();
            modelBuilder.Entity<Authentication>()
                .Property(a => a.CreatedAt).IsRequired();
             modelBuilder.Entity<Authentication>()
                .HasOne(a => a.Student)
                .WithMany(s=> s.Authentication)
                .HasForeignKey(a => a.StudentId)
                .OnDelete(DeleteBehavior.Cascade);

            // Student
            modelBuilder.Entity<Student>()
                .HasKey(s => s.Id);
            modelBuilder.Entity<Student>()
                .Property(s => s.Email).IsRequired();
            modelBuilder.Entity<Student>()
                .Property(s => s.FirstName).IsRequired();
            modelBuilder.Entity<Student>()
                .Property(s => s.LastName).IsRequired();
        }
    }
}