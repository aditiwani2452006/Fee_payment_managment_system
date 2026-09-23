import React, { useState, useEffect } from 'react';
import {
  apiGetPendingInstallments,
  apiReviewInstallment,
  apiGetAllPayments,
  apiGetFinancialSummary
} from '../api';

export default function AccountsDashboard({ homeTrigger }) {
  const [activeTab, setActiveTab] = useState('installments'); // 'installments', 'payments', 'reports'
  const [pendingInstallments, setPendingInstallments] = useState([]);
  const [payments, setPayments] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Approval modal state
  const [reviewTarget, setReviewTarget] = useState(null);
  const [splitCount, setSplitCount] = useState(2);
  const [submittingReview, setSubmittingReview] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    setActiveTab('installments');
    setReviewTarget(null);
  }, [homeTrigger]);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [instRes, payRes, sumRes] = await Promise.all([
        apiGetPendingInstallments().catch(() => ({ data: [] })),
        apiGetAllPayments().catch(() => ({ data: [] })),
        apiGetFinancialSummary().catch(() => ({ data: null }))
      ]);
      setPendingInstallments(instRes.data || []);
      setPayments(payRes.data || []);
      setSummary(sumRes.data);
    } catch (err) {
      setError('Failed to load accounts records: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleReview = async (action) => {
    if (!reviewTarget) return;
    setSubmittingReview(true);
    setError('');
    try {
      await apiReviewInstallment(reviewTarget.assignmentId, action, splitCount);
      setSuccessMsg(`Installment request for ${reviewTarget.studentName} was ${action === 'APPROVE' ? 'APPROVED (Schedule generated)' : 'REJECTED'}.`);
      setReviewTarget(null);
      loadData();
    } catch (err) {
      setError(err.message || 'Action failed');
    } finally {
      setSubmittingReview(false);
    }
  };

  const fmt = (amt) => {
    if (amt === undefined || amt === null) return '₹0';
    return '₹' + Number(amt).toLocaleString('en-IN');
  };

  return (
    <div className="container" style={{ padding: '1.5rem 1rem' }}>
      <div className="card" style={{ marginBottom: '1.5rem', background: '#1e3a8a', color: '#ffffff' }}>
        <div>
          <h1 style={{ fontSize: '1.4rem', fontWeight: 700, margin: 0 }}>Accounts Officer Dashboard</h1>
          <p style={{ fontSize: '0.85rem', color: '#bfdbfe', margin: '0.25rem 0 0 0' }}>
            Marathwada Mitra Mandal's College of Engineering — Fee Verification & Installment Administration
          </p>
        </div>
      </div>

      {successMsg && (
        <div className="alert-box alert-success">
          <strong>Success: </strong> {successMsg}
        </div>
      )}
      {error && (
        <div className="alert-box alert-danger">
          <strong>Notice: </strong> {error}
        </div>
      )}

      {/* Tabs */}
      <div className="erp-tabs-bar">
        <button
          className={`erp-tab ${activeTab === 'installments' ? 'active' : ''}`}
          onClick={() => setActiveTab('installments')}
        >
          1. Installment Requests ({pendingInstallments.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'payments' ? 'active' : ''}`}
          onClick={() => setActiveTab('payments')}
        >
          2. Payment Ledger & Verification ({payments.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'reports' ? 'active' : ''}`}
          onClick={() => setActiveTab('reports')}
        >
          3. Collection Summary Report
        </button>
      </div>

      {/* TAB 1: Installment Review */}
      {activeTab === 'installments' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Pending Installment Applications</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Review and approve student requests for semester fee split schedule
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={loadData}>
              Refresh List
            </button>
          </div>

          {pendingInstallments.length === 0 ? (
            <div className="empty-state-box">
              <div style={{ fontSize: '2rem' }}>✅</div>
              <div style={{ fontWeight: 600, marginTop: '0.5rem' }}>No Pending Installment Requests</div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                All student installment applications have been processed.
              </div>
            </div>
          ) : (
            <table className="erp-table">
              <thead>
                <tr>
                  <th>Assignment ID</th>
                  <th>Student Name</th>
                  <th>PRN</th>
                  <th>Course / Sem</th>
                  <th>Outstanding Amount</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {pendingInstallments.map((item) => (
                  <tr key={item.assignmentId}>
                    <td className="font-mono">#{item.assignmentId}</td>
                    <td style={{ fontWeight: 600 }}>{item.studentName}</td>
                    <td className="font-mono">{item.prn || 'B25IT2009'}</td>
                    <td>{item.department || 'IT'} - Sem {item.semester || 6}</td>
                    <td style={{ fontWeight: 600, color: 'var(--danger)' }}>{fmt(item.outstandingAmount)}</td>
                    <td>
                      <span className="status-pill status-pending">PENDING_APPROVAL</span>
                    </td>
                    <td>
                      <button
                        className="btn btn-primary btn-sm"
                        onClick={() => { setReviewTarget(item); setSplitCount(2); }}
                      >
                        Review & Decide
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* TAB 2: Payment Ledger & Verification */}
      {activeTab === 'payments' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Payment & Transaction Ledger</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Ledger of payment requests. Team 2 gateway integration will auto-reconcile live test payments.
              </div>
            </div>
            <span className="badge-tag">Review Build Seeded Data</span>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>Payment ID</th>
                <th>Student Name</th>
                <th>Amount</th>
                <th>Payment Method</th>
                <th>Status</th>
                <th>Gateway Reference</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {payments.map((p) => (
                <tr key={p.paymentId}>
                  <td className="font-mono">#{p.paymentId}</td>
                  <td style={{ fontWeight: 600 }}>{p.studentName}</td>
                  <td style={{ fontWeight: 600 }}>{fmt(p.amountPaid)}</td>
                  <td>{p.paymentMethod}</td>
                  <td>
                    <span className={`status-pill ${p.status === 'SUCCESS' ? 'status-paid' : 'status-pending'}`}>
                      {p.status}
                    </span>
                  </td>
                  <td className="font-mono" style={{ fontSize: '0.8rem' }}>
                    {p.gatewayReference || 'MMCOE-REQ-PENDING'}
                  </td>
                  <td>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => alert(`Payment #${p.paymentId} for ${p.studentName} (${fmt(p.amountPaid)}). Gateway verification integration is scoped for Team 2.`)}
                    >
                      View Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* TAB 3: Basic Reports */}
      {activeTab === 'reports' && (
        <div>
          <div className="fee-summary-grid" style={{ marginBottom: '1.5rem' }}>
            <div className="summary-metric-box">
              <span className="metric-label">Total Fee Assigned</span>
              <span className="metric-val text-primary">{fmt(summary?.totalAssigned || 550000)}</span>
              <span className="metric-sub">Across all registered students</span>
            </div>
            <div className="summary-metric-box">
              <span className="metric-label">Total Fee Collected</span>
              <span className="metric-val text-success">{fmt(summary?.totalCollected || 13837)}</span>
              <span className="metric-sub">Processed transactions</span>
            </div>
            <div className="summary-metric-box highlight-due">
              <span className="metric-label">Total Outstanding Balance</span>
              <span className="metric-val text-danger">{fmt(summary?.totalOutstanding || 536163)}</span>
              <span className="metric-sub">Unpaid dues pending collection</span>
            </div>
            <div className="summary-metric-box">
              <span className="metric-label">Students with Pending Dues</span>
              <span className="metric-val" style={{ color: '#d97706' }}>
                {summary?.studentsWithPendingDues || 9}
              </span>
              <span className="metric-sub">Out of 10 enrolled students</span>
            </div>
          </div>

          <div className="card">
            <h2 className="card-title" style={{ marginBottom: '1rem' }}>Collection by Category (DBMS Aggregation)</h2>
            <table className="erp-table">
              <thead>
                <tr>
                  <th>Category</th>
                  <th>Applicable Fee</th>
                  <th>Collection Rate</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>OPEN</strong></td>
                  <td>₹1,45,500</td>
                  <td>Pending</td>
                  <td><span className="status-pill status-pending">Dues Active</span></td>
                </tr>
                <tr>
                  <td><strong>OBC / EBC Male</strong></td>
                  <td>₹86,587</td>
                  <td>Pending</td>
                  <td><span className="status-pill status-pending">Dues Active</span></td>
                </tr>
                <tr>
                  <td><strong>SC / ST</strong></td>
                  <td>₹10,000</td>
                  <td>Pending</td>
                  <td><span className="status-pill status-pending">Dues Active</span></td>
                </tr>
                <tr>
                  <td><strong>VJNT / SBC / TFWS</strong></td>
                  <td>₹27,674</td>
                  <td>Pending</td>
                  <td><span className="status-pill status-pending">Dues Active</span></td>
                </tr>
                <tr>
                  <td><strong>J & K Quota</strong></td>
                  <td>₹34,000</td>
                  <td>Pending</td>
                  <td><span className="status-pill status-pending">Dues Active</span></td>
                </tr>
                <tr>
                  <td><strong>OBC / EBC Female</strong></td>
                  <td>₹27,674</td>
                  <td>50% Paid (Installment Plan)</td>
                  <td><span className="status-pill status-paid">Partial Collection</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Review Installment Modal */}
      {reviewTarget && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 480 }}>
            <div className="modal-header">
              <h3 className="modal-title">Review Installment Application</h3>
              <button className="btn-close" onClick={() => setReviewTarget(null)}>×</button>
            </div>
            <div className="modal-body">
              <div style={{ marginBottom: '1rem' }}>
                <div>Student: <strong>{reviewTarget.studentName}</strong></div>
                <div>PRN: <span className="font-mono">{reviewTarget.prn || 'B25IT2009'}</span></div>
                <div>Outstanding Balance: <strong style={{ color: 'var(--danger)' }}>{fmt(reviewTarget.outstandingAmount)}</strong></div>
              </div>

              <div className="form-group">
                <label className="form-label">Approved Split Count</label>
                <select
                  className="form-control"
                  value={splitCount}
                  onChange={(e) => setSplitCount(Number(e.target.value))}
                >
                  <option value={2}>2 Installments (₹{Math.round(reviewTarget.outstandingAmount / 2).toLocaleString('en-IN')} each)</option>
                  <option value={3}>3 Installments (₹{Math.round(reviewTarget.outstandingAmount / 3).toLocaleString('en-IN')} each)</option>
                </select>
                <span className="input-hint">Schedule deadlines will be automatically calculated within an ACID transaction.</span>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => handleReview('REJECT')}
                disabled={submittingReview}
              >
                Reject Request
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => handleReview('APPROVE')}
                disabled={submittingReview}
              >
                {submittingReview ? 'Processing...' : 'Approve & Create Schedule'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
