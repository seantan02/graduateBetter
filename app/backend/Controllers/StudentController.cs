using Microsoft.AspNetCore.Mvc;
using backend.Data;
using backend.Interface;
using Microsoft.EntityFrameworkCore;

using backend.Models;
using backend.Services;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;

namespace backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class StudentController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly IEmailService _emailService;
        private readonly JWTService _jwtService;

        public StudentController(AppDbContext context, IEmailService emailService, JWTService jwtService)
        {
            _context = context;
            _emailService = emailService;
            _jwtService = jwtService;
        }

        // GET: api/student/me
        [HttpGet("me")]
        [Authorize]
        public async Task<ActionResult<string>> Me()
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
            
            return StatusCode(200, "Successful");
        }
    }
}