using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authorization;

using backend.Data;
using backend.Models;
using System.Security.Claims;

namespace backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class MajorsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public MajorsController(AppDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Returns a list of majors that are available LIMIT 50 at a time.
        /// </summary>
        /// <response code="200">Majors returned</response>
        /// <response code="400">Something is not right!</response>
        /// <response code="401">Please login before using the API</response>
        /// <response code="500">Oops! Can't find any majors for you right now!</response>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Major>>> GetMajors(int lastId)
        {
            List<Major> majors = await _context.Majors
                                               .OrderBy(m => m.Id)
                                               .Where(m => m.MajorRequirements.Any() && m.Id > lastId)
                                               .Take(50)
                                               .ToListAsync();
            return majors;
        }

        // GET: api/majors/5
        [HttpGet("{id}")]
        [Authorize]
        public async Task<ActionResult<Major>> GetMajor(int id)
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

            var major = await _context.Majors.FindAsync(id);

            if (major == null)
            {
                return NotFound();
            }

            return major;
        }
    }
}