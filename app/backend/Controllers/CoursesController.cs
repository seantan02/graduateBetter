using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using backend.Data;
using backend.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CoursesController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CoursesController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/courses
        [HttpGet]
        [Authorize]
        public async Task<ActionResult<IEnumerable<Course>>> GetCourses(int lastId)
        {
            /* Bearer Token Validation for now */
            var user = HttpContext.User;
            var tokenType = user.FindFirst("token_type")?.Value;
            var email = user.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if(tokenType == null || email == null || tokenType.CompareTo("access_token") != 0){ return StatusCode(401, null); }
            Student ?student = await _context.Student
                .Where(s => s.Email.CompareTo(email) == 0)
                .FirstOrDefaultAsync();
            if(student == null){ return StatusCode(401, null); }
            /* Bearer Token Validation for now */

            return await _context.Courses
                                 .OrderBy(c => c.Id)
                                 .Where(c => c.Id > lastId)
                                 .Take(100)
                                 .ToListAsync();
        }

        // GET: api/courses/search
        [HttpGet("search")]
        public async Task<ActionResult<IEnumerable<Course>>> GetCoursesBySearchKey(string searchKey)
        {
            if (string.IsNullOrEmpty(searchKey))
            {
                return await _context.Courses.Take(100).ToListAsync();
            }
            
            return await _context.Courses
                .Where(c => c.Code.ToLower().Contains(searchKey.ToLower()))
                .OrderBy(c => c.Id)
                .Take(100)
                .ToListAsync();
        }

        // GET: api/courses/id
        [HttpGet("{id}")]
        [Authorize]
        public async Task<ActionResult<Course>> GetCourse(int id)
        {
            /* Bearer Token Validation for now */
            var user = HttpContext.User;
            var tokenType = user.FindFirst("token_type")?.Value;
            var email = user.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if(tokenType == null || email == null || tokenType.CompareTo("access_token") != 0){ return StatusCode(401, null); }
            Student ?student = await _context.Student
                .Where(s => s.Email.CompareTo(email) == 0)
                .FirstOrDefaultAsync();
            if(student == null){ return StatusCode(401, null); }
            /* Bearer Token Validation for now */

            var course = await _context.Courses.FindAsync(id);

            if (course == null)
            {
                return NotFound();
            }

            return course;
        }
    }
}