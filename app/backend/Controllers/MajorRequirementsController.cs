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
    public class MajorRequirementsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public MajorRequirementsController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/major_requirements
        [HttpGet]
        [Authorize]
        public async Task<ActionResult<IEnumerable<MajorRequirement>>> GetMajorRequirements(int majorId)
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

            return await _context.MajorRequirements
                                 .OrderBy(m => m.Id)
                                 .Where(m => m.MajorId == majorId)
                                 .ToListAsync();
        }

        // GET: api/major_requirements/5
        [HttpGet("{id}")]
        [Authorize]
        public async Task<ActionResult<MajorRequirement>> GetMajorRequirement(int id)
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

            var majorRequirement = await _context.MajorRequirements.FindAsync(id);

            if (majorRequirement == null)
            {
                return NotFound();
            }

            return majorRequirement;
        }
    }
}