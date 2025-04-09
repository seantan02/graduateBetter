FROM mcr.microsoft.com/dotnet/sdk:9.0
WORKDIR /backend
COPY . .

# Install required packages first
# COPY nuget-packages.sh .
# RUN chmod +x nuget-packages.sh
# RUN ./nuget-packages.sh

# Then restore and build
RUN dotnet restore
RUN dotnet build --no-restore

# Install EF Core tools

CMD ["dotnet", "run"]