// Centralized API Client with JWT Bearer Token Injection
const API_BASE = '/api';

export function getToken() {
  return localStorage.getItem('feepay_jwt');
}

export function setToken(token) {
  localStorage.setItem('feepay_jwt', token);
}

export function removeToken() {
  localStorage.removeItem('feepay_jwt');
  localStorage.removeItem('feepay_user');
}

export function getSavedUser() {
  const user = localStorage.getItem('feepay_user');
  return user ? JSON.parse(user) : null;
}

export function saveUser(userData) {
  localStorage.setItem('feepay_user', JSON.stringify(userData));
}

export async function apiRequest(endpoint, method = 'GET', body = null) {
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };

  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const config = {
    method,
    headers
  };

  if (body) {
    config.body = JSON.stringify(body);
  }

  const response = await fetch(`${API_BASE}${endpoint}`, config);
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    const errorMsg = data.message || `Request failed with status ${response.status}`;
    const err = new Error(errorMsg);
    err.status = response.status;
    throw err;
  }

  return data;
}

// Authentication & Password (Team 1)
export const apiLogin = (username, password) => 
  apiRequest('/auth/login', 'POST', { username, password });

export const apiChangePassword = (oldPassword, newPassword) =>
  apiRequest('/auth/change-password', 'POST', { oldPassword, newPassword });

export const apiResetPassword = (identifier, newPassword) =>
  apiRequest('/auth/reset-password', 'POST', { identifier, newPassword });

// Student Module (Team 1)
export const apiGetMyProfile = () => apiRequest('/students/me');
export const apiUpdateMyProfile = (data) => apiRequest('/students/me', 'PUT', data);
export const apiGetMyFees = () => apiRequest('/students/me/fees');
export const apiGetAllStudents = () => apiRequest('/students');
export const apiGetStudentFees = (studentId) => apiRequest(`/students/${studentId}/fees`);
export const apiAssignFee = (studentId, feeStructureId) => 
  apiRequest(`/students/${studentId}/assign-fee/${feeStructureId}`, 'POST');

// Fee Structure (Team 1)
export const apiGetFeeStructures = (dept, sem) => {
  const query = dept && sem ? `?department=${encodeURIComponent(dept)}&semester=${sem}` : '';
  return apiRequest(`/feestructures${query}`);
};
export const apiCreateFeeStructure = (data) => apiRequest('/feestructures', 'POST', data);
export const apiUpdateFeeStructure = (id, data) => apiRequest(`/feestructures/${id}`, 'PUT', data);

// Installment Module (Team 2)
export const apiApplyInstallment = (assignmentId, numberOfInstallments, reason) => 
  apiRequest('/installments/apply', 'POST', { assignmentId, numberOfInstallments, reason });
export const apiGetMyInstallments = () => apiRequest('/installments/my-installments');
export const apiGetPendingInstallments = () => apiRequest('/installments/pending');
export const apiReviewInstallment = (assignmentId, action, numberOfInstallments) => 
  apiRequest('/installments/review', 'POST', { assignmentId, action, numberOfInstallments });

// Payment Module (Team 2)
export const apiInitiatePayment = (assignmentId, installmentId, amount, paymentMethod = 'ONLINE_NETBANKING') => 
  apiRequest('/payments/initiate', 'POST', { assignmentId, installmentId, amount, paymentMethod });
export const apiProcessPayment = (paymentId, gatewayReference, gatewaySignature = 'test_sig') => 
  apiRequest('/payments/process', 'POST', { paymentId, gatewayReference, gatewaySignature });
export const apiGetReceipt = (receiptNumber) => apiRequest(`/payments/receipt/${receiptNumber}`);
export const apiGetMyPayments = (sortByAmount = false) => 
  apiRequest(`/payments/my-history?sortByAmount=${sortByAmount}`);
export const apiGetAllPayments = () => apiRequest('/payments');
export const apiRollbackPayment = (transactionId, reason) => 
  apiRequest(`/payments/rollback/${transactionId}?reason=${encodeURIComponent(reason)}`, 'POST');

// Reports
export const apiGetFinancialSummary = () => apiRequest('/reports/summary');
export const apiGetReportHistory = () => apiRequest('/reports/history');
export const apiGenerateReport = (reportType, fromDate, toDate) => 
  apiRequest(`/reports/generate?reportType=${encodeURIComponent(reportType)}&fromDate=${fromDate}&toDate=${toDate}`, 'POST');

// Admin, Audit, Security & Backup
export const apiGetAuditLogs = () => apiRequest('/admin/audit-logs');
export const apiGetSecurityLogs = () => apiRequest('/admin/security-logs');
export const apiGetBackups = () => apiRequest('/admin/backups');
export const apiTriggerBackup = (remarks = 'Manual Backup') => 
  apiRequest(`/admin/backups/trigger?remarks=${encodeURIComponent(remarks)}`, 'POST');

// Predictive Fee Defaulter Risk Assessment
export const apiGetRiskAssessment = (studentId) => apiRequest(`/ai/risk/${studentId}`);
export const apiGetAllRisks = () => apiRequest('/ai/risk/all');

// RBAC Unauthorized Access Verification
export const apiTestUnauthorizedAdmin = () => apiRequest('/admin/audit-logs');
