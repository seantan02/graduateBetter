using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using System.Reflection;
using System.Text;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens; // Replace with your actual namespace for AppDbContext

using backend.Data;
using backend.Services;
using backend.Interface;

var builder = WebApplication.CreateBuilder(args);
// CORS origin
var allowedSpecificOrigins = "_allowedSpecificOrigins";
// development
if(builder.Environment.EnvironmentName.CompareTo("Production") != 0){
    builder.Services.AddCors(options =>
    {
        options.AddPolicy(name: allowedSpecificOrigins,
                        policy  =>
                        {
                            policy.WithOrigins("http://localhost:5173")
                            .WithMethods("GET", "POST", "PUT", "OPTIONS")
                            .AllowAnyHeader();
                        });
    });
}else{
    builder.Services.AddCors(options =>
    {
        options.AddPolicy(name: allowedSpecificOrigins,
                        policy  =>
                        {
                            policy.WithOrigins("http://graduatebetter.com")
                            .WithMethods("GET", "POST", "PUT", "OPTIONS")
                            .AllowAnyHeader();
                        });
    });
}

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
// Configure Swagger with XML comments
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Your API Name",
        Version = "v1",
        Description = "Description of your API"
    });
    
    // Set up XML comments
    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath);
    // Add JWT Authentication
    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT"
    });
    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            new string[] {}
        }
    });
});

// Connect to PostgreSQL database
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

// Authorization and Authentication
builder.Services.AddTransient<JWTService>();
string ?secretKey = Environment.GetEnvironmentVariable("ASPNETCORE_SECRET_KEY");
if(secretKey == null){ throw new Exception("SECRET KEY not defined!"); }
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey)),
        ValidateIssuer = false,
        ValidateAudience = false,
        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero,
        NameClaimType = "nameid",  // Add this line to map the name claim correctly
        ValidTypes = new[] { "JWT" }  // Remove specific token_type validation for now
    };
});

builder.Services.AddAuthorization();
// Email
builder.Services.Configure<EmailSettings>(
    builder.Configuration.GetSection("EmailSettings")
);
builder.Services.AddTransient<IEmailService, EmailService>();

// App
var app = builder.Build();
// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
else
{
    app.UseHttpsRedirection(); // Only use HTTPS redirection in non-development environments
}
app.UseCors(allowedSpecificOrigins);
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();