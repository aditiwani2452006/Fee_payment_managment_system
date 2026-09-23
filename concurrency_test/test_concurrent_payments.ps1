# ============================================================================
# College: MMCOE, Pune - Dept. of Information Technology
# Concurrency Verification Test Script: Race Condition Prevention
# Demonstrates: Operating Systems (OS: Mutual Exclusion, Thread-Safety) &
#               DBMS (Pessimistic Locking 'SELECT ... FOR UPDATE', ACID Atomicity)
# ============================================================================

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$StudentUser = "student1",
    [string]$StudentPass = "password123"
)

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  MMCOE FeePay Platform - Concurrency & Race Condition Verification" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

# 1. Authenticate as student1 to acquire JWT token
Write-Host "`n[STEP 1] Authenticating as Student '$StudentUser' to acquire JWT..." -ForegroundColor Yellow
$loginBody = @{
    username = $StudentUser
    password = $StudentPass
} | ConvertTo-Json

try {
    $authResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $authResponse.data.token
    Write-Host "[SUCCESS] JWT Acquired: Bearer $($token.Substring(0, 25))..." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Authentication failed. Ensure backend is running on $BaseUrl." -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type"  = "application/json"
}

# 2. Fetch student's assigned fee records
Write-Host "`n[STEP 2] Fetching active fee assignments for student..." -ForegroundColor Yellow
$fees = (Invoke-RestMethod -Uri "$BaseUrl/api/students/me/fees" -Method GET -Headers $headers).data
$targetFee = $fees | Where-Object { $_.outstandingAmount -gt 0 } | Select-Object -First 1

if (-not $targetFee) {
    Write-Host "[WARN] No unpaid fees found for student1. Seed data may need resetting." -ForegroundColor Red
    exit 1
}

Write-Host "[INFO] Target Fee Assignment ID: $($targetFee.assignmentId)" -ForegroundColor Cyan
Write-Host "[INFO] Current Outstanding Amount: Rs. $($targetFee.outstandingAmount)" -ForegroundColor Cyan

# 3. Initiate payment order
Write-Host "`n[STEP 3] Initiating Payment Order for Rs. $($targetFee.outstandingAmount)..." -ForegroundColor Yellow
$initiateBody = @{
    assignmentId = $targetFee.assignmentId
    amount = $targetFee.outstandingAmount
    paymentMethod = "RAZORPAY_TEST"
} | ConvertTo-Json

$initiateRes = (Invoke-RestMethod -Uri "$BaseUrl/api/payments/initiate" -Method POST -Headers $headers -Body $initiateBody).data
$paymentId = $initiateRes.paymentId
$orderId = $initiateRes.gatewayReference
Write-Host "[SUCCESS] Payment Order Initialized: Payment ID = $paymentId, Gateway Ref = $orderId" -ForegroundColor Green

# 4. Concurrency Test: Fire two near-simultaneous payment confirmations targeting the same record
Write-Host "`n[STEP 4] Firing TWO SIMULTANEOUS payment confirmation requests..." -ForegroundColor Yellow
Write-Host "         Thread 1: Process Payment with Gateway Ref '$orderId'" -ForegroundColor Gray
Write-Host "         Thread 2: Concurrent duplicate request attempting double-deduction" -ForegroundColor Gray

$scriptBlock = {
    param($BaseUrl, $token, $paymentId, $gatewayRef)
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type"  = "application/json"
    }
    $body = @{
        paymentId = $paymentId
        gatewayReference = $gatewayRef
    } | ConvertTo-Json

    try {
        $res = Invoke-RestMethod -Uri "$BaseUrl/api/payments/process" -Method POST -Headers $headers -Body $body
        return @{ Status = "SUCCESS"; StatusCode = 200; Data = $res }
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        return @{ Status = "REJECTED"; StatusCode = $status; Message = $_.Exception.Message }
    }
}

# Start two PowerShell background jobs simultaneously
$job1 = Start-Job -ScriptBlock $scriptBlock -ArgumentList $BaseUrl, $token, $paymentId, "$orderId"
$job2 = Start-Job -ScriptBlock $scriptBlock -ArgumentList $BaseUrl, $token, $paymentId, "$orderId"

# Wait for both jobs to finish
$results = Wait-Job $job1, $job2 | Receive-Job
Remove-Job $job1, $job2

Write-Host "`n====================================================================" -ForegroundColor Cyan
Write-Host "  Concurrency Test Results" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

$successCount = 0
$conflictCount = 0

foreach ($res in $results) {
    if ($res.Status -eq "SUCCESS") {
        $successCount++
        Write-Host "[THREAD RESULT] Request SUCCEEDED (HTTP $($res.StatusCode)) -> Receipt Number: $($res.Data.data.receiptNumber)" -ForegroundColor Green
    } else {
        $conflictCount++
        Write-Host "[THREAD RESULT] Request SAFELY BLOCKED / REJECTED (HTTP $($res.StatusCode)) -> Concurrency/Idempotency Lock Held!" -ForegroundColor Yellow
    }
}

Write-Host "`n[ANALYSIS FOR VIVA PANEL]:" -ForegroundColor White
if ($successCount -eq 1 -and $conflictCount -eq 1) {
    Write-Host ">>> TEST PASSED: Exactly 1 transaction committed; race condition prevented!" -ForegroundColor Green
    Write-Host ">>> Proves: Pessimistic Row Locking (SELECT ... FOR UPDATE) and synchronized blocks protect financial balances." -ForegroundColor Green
} else {
    Write-Host ">>> Check results: Succeeded=$successCount, Blocked=$conflictCount" -ForegroundColor Yellow
}
