using Microsoft.AspNetCore.Mvc;

using backend.Data;
using backend.Interface;
using Microsoft.EntityFrameworkCore;

using backend.Models;
using backend.Services;
using System.Security.Claims;

namespace backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthenticationController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly IEmailService _emailService;
        private readonly JWTService _jwtService;

        public AuthenticationController(AppDbContext context, IEmailService emailService, JWTService jwtService)
        {
            _context = context;
            _emailService = emailService;
            _jwtService = jwtService;
        }

        // POST: api/authenticatioin/send-email
        [HttpPost("send-email")]
        public async Task<IActionResult> SendEmail([FromBody] EmailVerificationRequest request)
        {
            // failed if email is empty
            if(request.Email.CompareTo("") == 0)
            {
                return StatusCode(400, "Email or Code is invalid!");
            }
            // failed if not allowed email ending
            if(request.Email.Split("@")[^1].CompareTo("wisc.edu") != 0){
                return StatusCode(400, "Sorry. This application is only opened to students at UWMadison with wisc.edu email. :()");
            }
            // we want to generate a random 6 digits number and then send that over
            Random rnd = new Random();
            string code = "";
            for(int i = 0; i < 6; i++){
                code += rnd.Next(0, 10).ToString();
            }
            DateTime dateNow = DateTime.Now;

            using (var transaction = await _context.Database.BeginTransactionAsync())
            {
                try
                {
                    // Check if there's an existing record for this email
                    var student = await _context.Student
                        .Where(s => s.Email.CompareTo(request.Email) == 0)
                        .FirstOrDefaultAsync();

                    if (student != null)
                    {
                        var existingRecord = await _context.Authentication
                            .Where(a => a.StudentId == student.Id)
                            .FirstOrDefaultAsync();
                        if (existingRecord != null)
                        {
                            // No update if code is just sent less than 5 minutes ago
                            if ((DateTime.UtcNow - existingRecord.CreatedAt).TotalMinutes <= 5)
                            {
                                await transaction.CommitAsync();
                                return StatusCode(201, "Verification code already sent. Please check your email or wait 5 minutes before requesting a new code.");
                            }
                            // Update the existing record with new code and timestamp
                            existingRecord.Code = code;
                            existingRecord.CreatedAt = DateTime.UtcNow;
                            _context.Authentication.Update(existingRecord);
                            await _emailService.SendEmailAsync(
                                request.Email, 
                                "Graduate Better - Verification", 
                                $"Your verification code is {code}",
                                false
                            );
                            // Save changes and commit transaction
                            await _context.SaveChangesAsync();
                            await transaction.CommitAsync();
                            // success message
                            return StatusCode(201, "Verification code sent successfully.");
                        }
                    }
                    // we have a student record here but no verification code or we have no student record here
                    // so we check if student is null or not
                    Student studentToSave;
                    if(student == null){
                        studentToSave = new Student
                        {
                            Email = request.Email,
                            FirstName = new string("abcdefghijklmnopqrstuvwxyz".OrderBy(c => rnd.Next()).ToArray()),
                            LastName = new string("abcdefghijklmnopqrstuvwxyz".OrderBy(c => rnd.Next()).ToArray())
                        };
                    }else{
                        studentToSave = student;
                    }

                    var newAuthentication = new Authentication
                    {
                        Code = code,
                        CreatedAt = DateTime.UtcNow,
                        Student = studentToSave
                    };

                    studentToSave.Authentication.Add(newAuthentication);

                    if(student == null){
                        _context.Student.Add(studentToSave);
                    }else{
                        _context.Student.Update(studentToSave);
                    }

                    await _emailService.SendEmailAsync(
                        request.Email, 
                        "Graduate Better - Verification", 
                        $"Your verification code is {code}",
                        false
                    );
                    // Save changes and commit transaction
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                    // success message
                    return StatusCode(201, "Verification code sent successfully.");
                }
                catch (Exception ex)
                {
                    // Rollback the transaction if any error occurs
                    await transaction.RollbackAsync();
                    return StatusCode(400, $"Failed to process verification code: {ex.Message}");
                }
            }
        }

        // POST: api/authentication/verify-email
        [HttpPost("verify-email")]
        public async Task<ActionResult<AuthenticationResponse>> VerifyEmail([FromBody] EmailVerification verification)
        {
            // we want to generate a random 6 digits number and then send that over
            DateTime dateNow = DateTime.Now;

            if(verification.Email.CompareTo("") == 0 || verification.Code.CompareTo("") == 0 || verification.Code.Length != 6)
            {
                return StatusCode(400, "Email or Code is invalid!");
            }

            using (var transaction = await _context.Database.BeginTransactionAsync())
            {
                try
                {
                    // Check if there's an existing student for this email
                    var student = await _context.Student
                        .Where(s => s.Email.CompareTo(verification.Email) == 0)
                        .FirstOrDefaultAsync();
                    
                    if(student == null){
                        return StatusCode(400, "Please request a code before verification.");
                    }

                    var existingRecord = await _context.Authentication
                        .Where(a => a.StudentId == student.Id && a.Code.CompareTo(verification.Code) == 0)
                        .FirstOrDefaultAsync();
                    
                    if (existingRecord != null)
                    {
                        // Verification successful if the verification is made within 5 minutes
                        if ((DateTime.UtcNow - existingRecord.CreatedAt).TotalMinutes <= 5)
                        {
                            _context.Authentication.Remove(existingRecord);
                            Random rnd = new Random();
                            string randomTag = new string("abcdefghijklmnopqrstuvwxyz".OrderBy(c => rnd.Next()).ToArray());
                            string accessToken = _jwtService.CreateToken(student.Email, randomTag, "access_token", 1);
                            string refreshToken = _jwtService.CreateToken(student.Email, randomTag, "refresh_token", 72);
                            AuthenticationResponse result = new AuthenticationResponse
                            {
                                AccessToken = accessToken,
                                RefreshToken = refreshToken
                            };
                            // Save changes and commit transaction
                            await _context.SaveChangesAsync();
                            await transaction.CommitAsync();
                            return StatusCode(201, result);
                        }
                    }
                    // failed message
                    return StatusCode(401, "Verification failed!");
                }
                catch (Exception ex)
                {
                    // Rollback the transaction if any error occurs
                    await transaction.RollbackAsync();
                    return StatusCode(400, $"Failed to process verification code: {ex.Message}");
                }
            }
        }

        // POST: api/authentication/verify-email
        [HttpPost("refresh-token")]
        public Task<ActionResult<AuthenticationResponse>> RefreshToken([FromBody] AuthenticationResponse request)
        {
            ClaimsPrincipal accessTokenClaims =  _jwtService.ValidateToken(request.AccessToken, false, true);
            ClaimsPrincipal refreshTokenClaims =  _jwtService.ValidateToken(request.RefreshToken, true, false);
            // we check if they both matches
            string ?accessTokenEmail = accessTokenClaims.FindFirstValue(ClaimTypes.NameIdentifier);
            string ?refreshTokenEmail = refreshTokenClaims.FindFirstValue(ClaimTypes.NameIdentifier);
            string ?accessTokenTag = accessTokenClaims.FindFirstValue("tag");
            string ?refreshTokenTag = refreshTokenClaims.FindFirstValue("tag");
            // if both tokens have different tag then it fails
            if(accessTokenTag == null || refreshTokenTag == null) { return Task.FromResult<ActionResult<AuthenticationResponse>>(StatusCode(401, null)); }
            if(accessTokenTag.CompareTo(refreshTokenTag) != 0) { return Task.FromResult<ActionResult<AuthenticationResponse>>(StatusCode(401, null)); }
            // if both tokens have email mismatching then it fails
            if(accessTokenEmail == null || refreshTokenEmail == null){ return Task.FromResult<ActionResult<AuthenticationResponse>>(StatusCode(401, null)); }
            if(accessTokenEmail.CompareTo(refreshTokenEmail) != 0){ return Task.FromResult<ActionResult<AuthenticationResponse>>(StatusCode(401, null)); }
            // Both tokens are VALID! We generate a new one for them
            Random rnd = new Random();
            string randomTag = new string("abcdefghijklmnopqrstuvwxyz".OrderBy(c => rnd.Next()).ToArray());
            string accessToken = _jwtService.CreateToken(accessTokenEmail, randomTag, "access_token", 1);
            string refreshToken = _jwtService.CreateToken(accessTokenEmail, randomTag, "refresh_token", 72);
            return Task.FromResult<ActionResult<AuthenticationResponse>>(new AuthenticationResponse{
                AccessToken = accessToken, 
                RefreshToken = refreshToken 
                });
        }
    }
}