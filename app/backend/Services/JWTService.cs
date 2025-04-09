using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace backend.Services;

public class JWTService
{
    private string secretKey;
    public JWTService(){
        string ?secretKey = Environment.GetEnvironmentVariable("ASPNETCORE_SECRET_KEY");
        if(secretKey == null){
            throw new Exception("SECRET KEY of the application is null! Aborting...");
        }
        this.secretKey = secretKey;
    }

    public string CreateToken(string userEmail, string tag, string tokenType, int hours)
    /*
    This function creates a signed JWT token.
    params:
    - tokenType: access_token, or refresh_token
    - hours: the number of hours to be valid for
    */
    {
        var handler = new JwtSecurityTokenHandler();
        var privateKey = Encoding.UTF8.GetBytes(this.secretKey);
        var credentials = new SigningCredentials(
            new SymmetricSecurityKey(privateKey),
            SecurityAlgorithms.HmacSha256);

        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, userEmail.ToString()),
            new Claim("token_type", tokenType),
            new Claim("tag", tag)
        };

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(claims),
            SigningCredentials = credentials,
            Expires = DateTime.UtcNow.AddHours(hours)
        };
        
        var token = handler.CreateToken(tokenDescriptor);
        return handler.WriteToken(token);
    }
    
    public ClaimsPrincipal ValidateToken(string token, bool isRefreshToken, bool ignoreExpiration)
    {
        var handler = new JwtSecurityTokenHandler();
        var privateKey = Encoding.UTF8.GetBytes(this.secretKey);
        
        var validationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(privateKey),
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = !ignoreExpiration,
            ClockSkew = TimeSpan.Zero
        };
        
        SecurityToken validatedToken;
        var principal = handler.ValidateToken(token, validationParameters, out validatedToken);
        
        // Verify token type
        var tokenTypeClaim = principal.FindFirst("token_type")?.Value;
        var expectedTokenType = isRefreshToken ? "refresh_token" : "access_token";
        
        if (tokenTypeClaim != expectedTokenType)
        {
            throw new SecurityTokenValidationException($"Invalid token type. Expected {expectedTokenType}.");
        }
        
        return principal;
    }
}