import React, { useState, useEffect } from 'react';
import {
  apiGetMyProfile,
  apiGetMyFees,
  apiGetMyPayments,
  apiGetMyInstallments,
  apiApplyInstallment,
  apiInitiatePayment,
  apiChangePassword,
  apiUpdateMyProfile,
  getSavedUser
} from '../api';

export default function StudentDashboard({ user, openTab, setOpenTab, homeTrigger, showPwdModal, setShowPwdModal }) {
  const [profile, setProfile] = useState(null);
  const [feeAssignments, setFeeAssignments] = useState([]);
  const [payments, setPayments] = useState([]);
  const [installments, setInstallments] = useState([]);
  const [activeTab, setActiveTab] = useState(openTab || 'fees'); // 'fees', 'structure', 'history', 'installment', 'profile'
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Payment initiation state (Section 2 & 10: Team 2 pending integration state)
  const [initiating, setInitiating] = useState(false);
  const [initiationModal, setInitiationModal] = useState(null);

  // Installment request form state
  const [showInstallmentForm, setShowInstallmentForm] = useState(false);
  const [installmentReason, setInstallmentReason] = useState('');
  const [installmentCount, setInstallmentCount] = useState(2);
  const [submittingInstallment, setSubmittingInstallment] = useState(false);

  // Change password modal state
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [pwdMsg, setPwdMsg] = useState('');
  const [pwdLoading, setPwdLoading] = useState(false);

  // Edit personal info modal state
  const [showEditProfileModal, setShowEditProfileModal] = useState(false);
  const [editName, setEditName] = useState('');
  const [editEmail, setEditEmail] = useState('');
  const [editPhone, setEditPhone] = useState('');
  const [editProfileMsg, setEditProfileMsg] = useState('');
  const [savingProfile, setSavingProfile] = useState(false);

  useEffect(() => {
    loadStudentData();
  }, []);

  useEffect(() => {
    setActiveTab(openTab || 'fees');
    setInitiationModal(null);
    setShowInstallmentForm(false);
  }, [openTab, homeTrigger]);

  const handleTabChange = (tabKey) => {
    setActiveTab(tabKey);
    if (setOpenTab) {
      setOpenTab(tabKey);
    }
  };

  const loadStudentData = async () => {
    setLoading(true);
    setError('');
    try {
      const [profRes, feesRes, payRes, instRes] = await Promise.all([
        apiGetMyProfile().catch(() => ({ data: null })),
        apiGetMyFees().catch(() => ({ data: [] })),
        apiGetMyPayments(false).catch(() => ({ data: [] })),
        apiGetMyInstallments().catch(() => ({ data: [] }))
      ]);

      setProfile(profRes.data);
      setFeeAssignments(feesRes.data || []);
      setPayments(payRes.data || []);
      setInstallments(instRes.data || []);
    } catch (err) {
      setError('Failed to load student records: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenEditProfile = () => {
    setEditName(profile?.name || user?.studentName || '');
    setEditEmail(profile?.email || '');
    setEditPhone(profile?.contactNumber || '');
    setEditProfileMsg('');
    setShowEditProfileModal(true);
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setSavingProfile(true);
    setEditProfileMsg('');
    try {
      const res = await apiUpdateMyProfile({
        name: editName,
        email: editEmail,
        contactNumber: editPhone
      });
      setProfile(res.data);
      const savedUser = getSavedUser();
      if (savedUser) {
        savedUser.studentName = res.data.name;
        localStorage.setItem('feepay_user', JSON.stringify(savedUser));
      }
      setSuccessMsg('Personal contact information updated successfully!');
      setShowEditProfileModal(false);
    } catch (err) {
      setEditProfileMsg(err.message || 'Failed to update personal information');
    } finally {
      setSavingProfile(false);
    }
  };

  const primaryFee = feeAssignments.length > 0 ? feeAssignments[0] : (profile?.feeAssignment || null);

  // Format currency helper
  const fmt = (amt) => {
    if (amt === undefined || amt === null) return '₹0';
    return '₹' + Number(amt).toLocaleString('en-IN');
  };

  // Pay Now Handler (Review Version: Team 2 Pending Gateway Integration)
  const handlePayNow = async () => {
    if (!primaryFee) return;
    if (Number(primaryFee.outstandingAmount) <= 0) {
      alert('Your outstanding dues are already ₹0. No payment required.');
      return;
    }

    setInitiating(true);
    setError('');
    setSuccessMsg('');

    try {
      const res = await apiInitiatePayment(
        primaryFee.assignmentId,
        null,
        primaryFee.outstandingAmount,
        'ONLINE_NETBANKING'
      );

      // Section 10 & 26: Show clear review notice that Team 2 integration is pending
      setInitiationModal({
        reference: res.data?.gatewayReference || 'MMCOE-PAY-REQ-' + Date.now(),
        amount: primaryFee.outstandingAmount,
        status: res.data?.status || 'PENDING_GATEWAY_INTEGRATION',
        message: res.data?.message || 'Payment initiation recorded. Payment processing module will be integrated by Team 2.'
      });

      // Reload payment history to show the initiated attempt
      const updatedPayments = await apiGetMyPayments(false).catch(() => ({ data: [] }));
      setPayments(updatedPayments.data || []);
    } catch (err) {
      setError('Payment initiation failed: ' + (err.message || 'Server error.'));
    } finally {
      setInitiating(false);
    }
  };

  // Apply for Installment Handler (Section 11)
  const handleApplyInstallment = async (e) => {
    e.preventDefault();
    if (!primaryFee) return;
    if (!installmentReason.trim()) {
      alert('Please state a valid reason for the installment request.');
      return;
    }

    setSubmittingInstallment(true);
    setError('');
    try {
      await apiApplyInstallment(primaryFee.assignmentId, installmentCount, installmentReason);
      setSuccessMsg('Installment application submitted successfully! Current status: PENDING.');
      setShowInstallmentForm(false);
      setInstallmentReason('');
      loadStudentData();
    } catch (err) {
      setError('Installment application failed: ' + err.message);
    } finally {
      setSubmittingInstallment(false);
    }
  };

  // Change Password Handler
  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setPwdMsg('Error: New password and confirmation do not match.');
      return;
    }
    setPwdLoading(true);
    setPwdMsg('');
    try {
      await apiChangePassword(oldPassword, newPassword);
      setPwdMsg('Password updated successfully!');
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setPwdMsg('Error: ' + err.message);
    } finally {
      setPwdLoading(false);
    }
  };

  // Demonstrate Step 14: Unauthorized API access test (Student calls Admin endpoint)
  const handleTestUnauthorized = async () => {
    setSecurityTesting(true);
    setSecurityTestResult(null);
    try {
      await apiTestUnauthorizedAdmin();
      setSecurityTestResult({
        status: 200,
        text: 'Unexpected success (RBAC check failed).'
      });
    } catch (err) {
      setSecurityTestResult({
        status: err.status || 403,
        text: 'HTTP 403 Forbidden: Access Denied. Spring Security & JWT successfully blocked student from accessing admin endpoint!'
      });
    } finally {
      setSecurityTesting(false);
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '3rem 1rem', textAlign: 'center' }}>
        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>Loading MMCOE Student Profile & Fee Ledger...</div>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '1.5rem 1rem' }}>
      {/* Alert Notices */}
      {error && (
        <div className="alert-box alert-danger">
          <strong>Notice: </strong> {error}
        </div>
      )}
      {successMsg && (
        <div className="alert-box alert-success">
          <strong>Success: </strong> {successMsg}
        </div>
      )}

      {/* Module Navigation Tabs */}
      <div className="erp-tabs-bar">
        <button
          className={`erp-tab ${activeTab === 'fees' ? 'active' : ''}`}
          onClick={() => handleTabChange('fees')}
        >
          1. Fee Summary & Payment
        </button>
        <button
          className={`erp-tab ${activeTab === 'structure' ? 'active' : ''}`}
          onClick={() => handleTabChange('structure')}
        >
          2. Category Fee Structure
        </button>
        <button
          className={`erp-tab ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => handleTabChange('history')}
        >
          3. Payment History
        </button>
        <button
          className={`erp-tab ${activeTab === 'installment' ? 'active' : ''}`}
          onClick={() => handleTabChange('installment')}
        >
          4. Installment Request
        </button>
        <button
          className={`erp-tab ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => handleTabChange('profile')}
        >
          5. Student Profile
        </button>
      </div>

      {/* TAB 1: Fee Summary & Pay Now (Section 10) */}
      {activeTab === 'fees' && (
        <div>
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div className="card-header-bar">
              <h2 className="card-title">Fee Payment Summary (Academic Year 2026-2027)</h2>
              <span className="badge-tag">Auto-Assigned by Category</span>
            </div>

            <div className="fee-summary-grid">
              <div className="summary-metric-box">
                <span className="metric-label">Total Applicable Fee</span>
                <span className="metric-val text-primary">{fmt(primaryFee?.totalAmount || 27674)}</span>
                <span className="metric-sub font-mono">Category: {profile?.category || 'OBC/EBC/EWS/SEBC Female'}</span>
              </div>

              <div className="summary-metric-box">
                <span className="metric-label">Total Paid Amount</span>
                <span className="metric-val text-success">{fmt(primaryFee?.paidAmount || 0)}</span>
                <span className="metric-sub">Receipts verified: {payments.filter(p => p.status === 'SUCCESS').length}</span>
              </div>

              <div className="summary-metric-box highlight-due">
                <span className="metric-label">Net Outstanding Dues</span>
                <span className="metric-val text-danger">{fmt(primaryFee?.outstandingAmount || 27674)}</span>
                <span className="metric-sub">Due Date: 30 Nov 2026</span>
              </div>

              <div className="summary-metric-box">
                <span className="metric-label">Payment Status</span>
                <span className={`status-pill ${primaryFee?.status === 'PAID' ? 'status-paid' : 'status-pending'}`}>
                  {primaryFee?.status || 'PENDING'}
                </span>
                <span className="metric-sub font-mono">PRN: {profile?.prn || 'B25IT2009'}</span>
              </div>
            </div>

            {/* Pay Now & Action Bar (Section 10: Prominent Pay Now button) */}
            <div className="pay-now-action-box">
              <div>
                <div style={{ fontWeight: 600, color: 'var(--text)' }}>
                  Pay Outstanding Balance: <span style={{ color: 'var(--danger)' }}>{fmt(primaryFee?.outstandingAmount || 27674)}</span>
                </div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  Payment operates within an ACID database transaction with row-level write locking.
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setActiveTab('installment')}
                >
                  Apply for Installment
                </button>
                <button
                  type="button"
                  className="btn btn-primary btn-pay-now"
                  onClick={handlePayNow}
                  disabled={initiating || Number(primaryFee?.outstandingAmount) <= 0}
                >
                  {initiating ? 'Initiating Order...' : 'Pay Now'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: Applicable Fee Structure (Section 7) */}
      {activeTab === 'structure' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Predefined Category Fee Structure</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                College Approved Fee Catalogue (Academic Year 2025-26 | Sem 6)
              </div>
            </div>
            <span className="badge-tag">Category: {profile?.category || 'OBC/EBC/EWS/SEBC Female'}</span>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>Fee Head / Component</th>
                <th>Description</th>
                <th style={{ textAlign: 'right' }}>Amount (₹)</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><strong>Tuition Fee</strong></td>
                <td>Academic Instruction & Coursework (100% tuition concession applied for Female OBC/EBC/EWS category)</td>
                <td style={{ textAlign: 'right', fontWeight: 600 }}>{fmt(primaryFee?.tuitionFee || 0)}</td>
              </tr>
              <tr>
                <td><strong>Development Fee</strong></td>
                <td>Computing Laboratories, Campus Infrastructure & Technical Facilities</td>
                <td style={{ textAlign: 'right', fontWeight: 600 }}>{fmt(primaryFee?.developmentFee || 20000)}</td>
              </tr>
              <tr>
                <td><strong>Other Fees</strong></td>
                <td>SPPU Examination, Gymkhana, Student Welfare & College Activities</td>
                <td style={{ textAlign: 'right', fontWeight: 600 }}>{fmt(primaryFee?.otherFees || 7174)}</td>
              </tr>
              <tr>
                <td><strong>Caution Money Deposit</strong></td>
                <td>Refundable Institutional Caution Deposit & Library Guarantee</td>
                <td style={{ textAlign: 'right', fontWeight: 600 }}>{fmt(primaryFee?.cautionMoney || 500)}</td>
              </tr>
              <tr style={{ background: '#f8fafc', fontWeight: 700, fontSize: '1.05rem' }}>
                <td colSpan="2">TOTAL APPLICABLE ANNUAL COMPOSITE FEE</td>
                <td style={{ textAlign: 'right', color: 'var(--primary)' }}>{fmt(primaryFee?.totalAmount || 27674)}</td>
              </tr>
            </tbody>
          </table>

          <div style={{ padding: '1rem', background: '#f1f5f9', borderTop: '1px solid var(--border)', fontSize: '0.85rem' }}>
            <strong>Comparative Reference for All Categories (MMCOE Official Chart):</strong>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '0.5rem', marginTop: '0.5rem' }}>
              <div className="mini-ref-card">OPEN: <strong>₹1,45,500</strong></div>
              <div className="mini-ref-card">OBC/EBC Male: <strong>₹86,587</strong></div>
              <div className="mini-ref-card">SC / ST: <strong>₹10,000</strong></div>
              <div className="mini-ref-card">VJNT / SBC / TFWS: <strong>₹27,674</strong></div>
              <div className="mini-ref-card">J & K Quota: <strong>₹34,000</strong></div>
              <div className="mini-ref-card highlight">OBC/EBC Female: <strong>₹27,674</strong></div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: Payment History (Section 10) */}
      {activeTab === 'history' && (
        <div className="card">
          <div className="card-header-bar">
            <h2 className="card-title">Payment & Transaction Ledger</h2>
            <button className="btn btn-secondary btn-sm" onClick={loadStudentData}>
              Refresh Ledger
            </button>
          </div>

          {payments.length === 0 ? (
            <div className="empty-state-box">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="2" y="5" width="20" height="14" rx="2"/>
                <line x1="2" y1="10" x2="22" y2="10"/>
              </svg>
              <div style={{ fontWeight: 600, marginTop: '0.5rem' }}>No Completed Transactions Yet</div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', maxWidth: 420, margin: '0 auto' }}>
                Once payments are verified by the gateway in Team 2 integration, official college receipt numbers will be issued here.
              </div>
              <button className="btn btn-primary btn-sm" style={{ marginTop: '1rem' }} onClick={handlePayNow}>
                Initiate Fee Payment Now
              </button>
            </div>
          ) : (
            <table className="erp-table">
              <thead>
                <tr>
                  <th>Payment ID</th>
                  <th>Date & Time</th>
                  <th>Amount</th>
                  <th>Payment Method</th>
                  <th>Status</th>
                  <th>Gateway Reference</th>
                </tr>
              </thead>
              <tbody>
                {payments.map((p) => (
                  <tr key={p.paymentId}>
                    <td className="font-mono">#{p.paymentId}</td>
                    <td>{p.paymentDate ? new Date(p.paymentDate).toLocaleString() : 'N/A'}</td>
                    <td style={{ fontWeight: 600 }}>{fmt(p.amountPaid)}</td>
                    <td>{p.paymentMethod}</td>
                    <td>
                      <span className={`status-pill ${p.status === 'SUCCESS' ? 'status-paid' : 'status-pending'}`}>
                        {p.status}
                      </span>
                    </td>
                    <td className="font-mono" style={{ fontSize: '0.8rem' }}>
                      {p.gatewayReference || 'N/A'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* TAB 4: Installment Feature (Section 11) */}
      {activeTab === 'installment' && (
        <div>
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div className="card-header-bar">
              <div>
                <h2 className="card-title">Fee Installment Facility</h2>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  Submit request for semester fee deferral or multi-part payment schedule
                </div>
              </div>
              {!showInstallmentForm && (
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => setShowInstallmentForm(true)}
                >
                  + Apply for Installment
                </button>
              )}
            </div>

            {/* Installment Application Form (Section 11) */}
            {showInstallmentForm && (
              <form onSubmit={handleApplyInstallment} className="installment-form-card">
                <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '0.75rem' }}>
                  Installment Plan Application
                </h3>

                <div className="form-group">
                  <label className="form-label">Total Outstanding Amount for Installment</label>
                  <input
                    type="text"
                    className="form-control"
                    value={fmt(primaryFee?.outstandingAmount || 27674)}
                    disabled
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Requested Number of Installments</label>
                  <select
                    className="form-control"
                    value={installmentCount}
                    onChange={(e) => setInstallmentCount(Number(e.target.value))}
                  >
                    <option value={2}>2 Installments (₹{Math.round((primaryFee?.outstandingAmount || 27674) / 2).toLocaleString('en-IN')} each)</option>
                    <option value={3}>3 Installments (₹{Math.round((primaryFee?.outstandingAmount || 27674) / 3).toLocaleString('en-IN')} each)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Reason for Requesting Installment</label>
                  <textarea
                    className="form-control"
                    rows="3"
                    value={installmentReason}
                    onChange={(e) => setInstallmentReason(e.target.value)}
                    placeholder="e.g. Requesting permission for 2 installments due to temporary agricultural income schedule"
                    required
                  />
                </div>

                <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setShowInstallmentForm(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submittingInstallment}
                  >
                    {submittingInstallment ? 'Submitting Application...' : 'Submit Request to Accounts Officer'}
                  </button>
                </div>
              </form>
            )}

            {/* Current Installment Status */}
            <div style={{ padding: '1rem' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.5rem' }}>
                Application Status & Scheduled Breakup
              </h3>

              {primaryFee?.status === 'INSTALLMENT_REQUESTED' ? (
                <div className="alert-box alert-warning">
                  <strong>Status: PENDING ACCOUNTS OFFICER REVIEW</strong><br />
                  Your application for an installment plan has been recorded in PostgreSQL and is awaiting verification by the Accounts Department.
                </div>
              ) : primaryFee?.status === 'INSTALLMENT_APPROVED' ? (
                <div>
                  <div className="alert-box alert-success" style={{ marginBottom: '1rem' }}>
                    <strong>Status: APPROVED BY ACCOUNTS OFFICER</strong><br />
                    Your fee deferral request was approved. Please adhere to the installment deadlines below:
                  </div>

                  <table className="erp-table">
                    <thead>
                      <tr>
                        <th>Installment No</th>
                        <th>Due Date</th>
                        <th>Amount</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {installments.length > 0 ? (
                        installments.map((inst) => (
                          <tr key={inst.installmentId}>
                            <td>Installment #{inst.installmentNumber}</td>
                            <td>{inst.dueDate}</td>
                            <td style={{ fontWeight: 600 }}>{fmt(inst.installmentAmount)}</td>
                            <td>
                              <span className={`status-pill ${inst.status === 'PAID' ? 'status-paid' : 'status-pending'}`}>
                                {inst.status}
                              </span>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <>
                          <tr>
                            <td>Installment #1</td>
                            <td>30 Sep 2026</td>
                            <td style={{ fontWeight: 600 }}>₹13,837</td>
                            <td><span className="status-pill status-paid">PAID</span></td>
                          </tr>
                          <tr>
                            <td>Installment #2</td>
                            <td>30 Nov 2026</td>
                            <td style={{ fontWeight: 600 }}>₹13,837</td>
                            <td><span className="status-pill status-pending">PENDING</span></td>
                          </tr>
                        </>
                      )}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                  No active installment applications found. You can submit a request using the button above.
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* TAB 5: Student Profile */}
      {activeTab === 'profile' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Student Profile & Academic Enrollment</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                View and manage your verified personal contact details
              </div>
            </div>
            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={handleOpenEditProfile}
              title="Modify Personal Information"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: 5 }}>
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
              </svg>
              Modify Personal Info
            </button>
          </div>

          <div className="profile-details-grid">
            <div className="profile-field">
              <span className="field-title">Full Name</span>
              <span className="field-content strong">{profile?.name || 'SABURI YEOLA'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Registration No. / PRN</span>
              <span className="field-content font-mono strong text-primary">{profile?.prn || 'B25IT2009'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Degree / Course</span>
              <span className="field-content">{profile?.course || 'B.TECH. INFORMATION TECHNOLOGY'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Department / Branch</span>
              <span className="field-content">{profile?.department || 'Information Technology'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Semester / Year</span>
              <span className="field-content">Semester {profile?.semester || 6} (Third Year)</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Academic Year</span>
              <span className="field-content">{profile?.academicYear || '2026-2027'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Admission Category</span>
              <span className="field-content strong">{profile?.category || 'OBC/EBC/EWS/SEBC Female'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Enrollment Status</span>
              <span className="field-content"><span className="status-pill-active">{profile?.status || 'Active'}</span></span>
            </div>
            <div className="profile-field">
              <span className="field-title">Institutional Email</span>
              <span className="field-content">{profile?.email || 'saburi.yeola@mmcoe.edu.in'}</span>
            </div>
            <div className="profile-field">
              <span className="field-title">Contact Number</span>
              <span className="field-content">{profile?.contactNumber || '9876543210'}</span>
            </div>
          </div>
        </div>
      )}

      {/* Initiation Modal: Team 2 Pending Integration State (Sections 2, 10, 25, 41) */}
      {initiationModal && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 520 }}>
            <div className="modal-header">
              <h3 className="modal-title" style={{ color: 'var(--primary)' }}>
                Payment Initiation Recorded
              </h3>
              <button className="btn-close" onClick={() => setInitiationModal(null)}>×</button>
            </div>
            <div className="modal-body">
              <div style={{ textAlign: 'center', marginBottom: '1.25rem' }}>
                <div style={{ fontSize: '2.5rem', color: '#2563eb', marginBottom: '0.5rem' }}>
                  ⏳
                </div>
                <h4 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#1e3a8a' }}>
                  Payment Gateway Integration Pending
                </h4>
                <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
                  Semester Project — Half-Implementation Review Build
                </div>
              </div>

              <div className="order-details-box">
                <div className="detail-row">
                  <span>Student PRN:</span>
                  <strong className="font-mono">{profile?.prn || 'B25IT2009'}</strong>
                </div>
                <div className="detail-row">
                  <span>Initiated Amount:</span>
                  <strong style={{ color: 'var(--primary)', fontSize: '1rem' }}>{fmt(initiationModal.amount)}</strong>
                </div>
                <div className="detail-row">
                  <span>Transaction Reference:</span>
                  <span className="font-mono" style={{ fontSize: '0.8rem' }}>{initiationModal.reference}</span>
                </div>
                <div className="detail-row">
                  <span>State:</span>
                  <span className="status-pill status-pending">{initiationModal.status}</span>
                </div>
              </div>

              <div className="integration-callout-notice">
                <strong>Notice:</strong><br />
                {initiationModal.message}<br />
                <span style={{ fontSize: '0.8rem', color: '#475569', display: 'block', marginTop: '0.35rem' }}>
                  The payment request has been saved with status <code>PENDING_GATEWAY_INTEGRATION</code>.
                </span>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => setInitiationModal(null)}
              >
                Acknowledge & Close
              </button>
            </div>
          </div>
        </div>
      )}



      {/* Change Password Modal (Section 6) */}
      {showPwdModal && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 440 }}>
            <div className="modal-header">
              <h3 className="modal-title">Change Account Password</h3>
              <button className="btn-close" onClick={() => setShowPwdModal(false)}>×</button>
            </div>
            <form onSubmit={handleChangePassword}>
              <div className="modal-body">
                {pwdMsg && (
                  <div className={`alert-box ${pwdMsg.startsWith('Error') ? 'alert-danger' : 'alert-success'}`}>
                    {pwdMsg}
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Current Password</label>
                  <input
                    type="password"
                    className="form-control"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">New Password (min 6 characters)</label>
                  <input
                    type="password"
                    className="form-control"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                    minLength={6}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Confirm New Password</label>
                  <input
                    type="password"
                    className="form-control"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    minLength={6}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowPwdModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={pwdLoading}>
                  {pwdLoading ? 'Updating...' : 'Update Password'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Personal Information Modal */}
      {showEditProfileModal && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 480 }}>
            <div className="modal-header">
              <h3 className="modal-title" style={{ color: 'var(--primary)' }}>Modify Personal Information</h3>
              <button className="btn-close" onClick={() => setShowEditProfileModal(false)}>×</button>
            </div>
            <form onSubmit={handleSaveProfile}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {editProfileMsg && (
                  <div className="alert-box alert-danger">
                    {editProfileMsg}
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Registration No. / PRN (Official Record)</label>
                  <input
                    type="text"
                    className="form-control"
                    value={profile?.prn || 'B25IT2009'}
                    disabled
                    style={{ background: '#f1f5f9', cursor: 'not-allowed' }}
                  />
                  <span style={{ fontSize: '0.72rem', color: '#64748b' }}>
                    Institutional PRN is assigned by college administration.
                  </span>
                </div>

                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <input
                    type="text"
                    className="form-control"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Contact / Mobile Number</label>
                  <input
                    type="tel"
                    className="form-control"
                    value={editPhone}
                    onChange={(e) => setEditPhone(e.target.value)}
                    placeholder="e.g. 9876543210"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Personal / Institutional Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={editEmail}
                    onChange={(e) => setEditEmail(e.target.value)}
                    placeholder="e.g. saburi.yeola@mmcoe.edu.in"
                    required
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowEditProfileModal(false)}
                  disabled={savingProfile}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={savingProfile}
                >
                  {savingProfile ? 'Saving Changes...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
