# First argument is the path to Maven
$maven = $args[0]

# Check if the Maven path is provided
if (-not $maven) {
	Write-Host "Please provide the path to Maven as an argument."
	exit 1
}

# Check if the provided Maven path exists
if (-not (Test-Path $maven)) {
	Write-Host "The specified path to Maven does not exist."
	exit 1
}

# Add Maven to the PATH environment variable
$env:PATH = "$maven;$env:PATH"
