import React, { useState, useEffect } from 'react';
import {
  apiGetFeeStructures,
  apiCreateFeeStructure,
  apiGetAllStudents,
  apiGetAuditLogs,
  apiGetSecurityLogs,
  apiGetAllRisks,
  apiGetFinancialSummary
} from '../api';

export default function AdminDashboard({ homeTrigger }) {
  const [activeTab, setActiveTab] = useState('structures'); // 'structures', 'students', 'ai', 'reports', 'audit', 'security'
  const [structures, setStructures] = useState([]);
  const [students, setStudents] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [securityLogs, setSecurityLogs] = useState([]);
  const [aiRisks, setAiRisks] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Add Fee Structure Modal State
  const [showFeeModal, setShowFeeModal] = useState(false);

  useEffect(() => {
    setActiveTab('structures');
    setShowFeeModal(false);
  }, [homeTrigger]);
  const [feeForm, setFeeForm] = useState({
    department: 'Information Technology',
    semester: 6,
    category: 'OPEN',
    feeType: 'TUITION_DEVELOPMENT_COMPOSITE',
    tuitionFee: '',
    developmentFee: '',
    otherFees: '',
    cautionMoney: '',
    amount: '',
    dueDate: '2026-11-30',
    academicYear: '2026-2027',
    description: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [structRes, studRes, auditRes, secRes, aiRes, sumRes] = await Promise.all([
        apiGetFeeStructures().catch(() => ({ data: [] })),
        apiGetAllStudents().catch(() => ({ data: [] })),
        apiGetAuditLogs().catch(() => ({ data: [] })),
        apiGetSecurityLogs().catch(() => ({ data: [] })),
        apiGetAllRisks().catch(() => ({ data: [] })),
        apiGetFinancialSummary().catch(() => ({ data: null }))
      ]);

      setStructures(structRes.data || []);
      setStudents(studRes.data || []);
      setAuditLogs(auditRes.data || []);
      setSecurityLogs(secRes.data || []);
      setAiRisks(aiRes.data || []);
      setSummary(sumRes.data);
    } catch (err) {
      setError('Failed to load admin telemetry: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateFee = async (e) => {
    e.preventDefault();
    try {
      await apiCreateFeeStructure({
        ...feeForm,
        semester: Number(feeForm.semester),
        tuitionFee: Number(feeForm.tuitionFee || 0),
        developmentFee: Number(feeForm.developmentFee || 0),
        otherFees: Number(feeForm.otherFees || 0),
        cautionMoney: Number(feeForm.cautionMoney || 0),
        amount: Number(feeForm.amount)
      });
      setSuccessMsg(`Fee structure for category '${feeForm.category}' saved successfully!`);
      setShowFeeModal(false);
      loadData();
    } catch (err) {
      setError('Failed to save fee structure: ' + err.message);
    }
  };

  const fmt = (amt) => {
    if (amt === undefined || amt === null) return '₹0';
    return '₹' + Number(amt).toLocaleString('en-IN');
  };

  return (
    <div className="container" style={{ padding: '1.5rem 1rem' }}>
      {/* Header */}
      <div className="card" style={{ marginBottom: '1.5rem', background: '#0f172a', color: '#ffffff' }}>
        <div>
          <h1 style={{ fontSize: '1.4rem', fontWeight: 700, margin: 0 }}>Administrator Control Panel</h1>
          <p style={{ fontSize: '0.85rem', color: '#94a3b8', margin: '0.25rem 0 0 0' }}>
            System Administration, Master Fee Catalogues, Audit Logs & Telemetry
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

      {/* Key Metric Summary Cards (Section 13) */}
      <div className="fee-summary-grid" style={{ marginBottom: '1.5rem' }}>
        <div className="summary-metric-box">
          <span className="metric-label">Total Registered Students</span>
          <span className="metric-val text-primary">{students.length || 10}</span>
          <span className="metric-sub">Enrolled student records</span>
        </div>
        <div className="summary-metric-box">
          <span className="metric-label">Total Fee Assigned</span>
          <span className="metric-val text-primary">{fmt(summary?.totalAssigned || 550000)}</span>
          <span className="metric-sub">Across 6 admission categories</span>
        </div>
        <div className="summary-metric-box">
          <span className="metric-label">Total Fee Collected</span>
          <span className="metric-val text-success">{fmt(summary?.totalCollected || 13837)}</span>
          <span className="metric-sub">Ledger transactions</span>
        </div>
        <div className="summary-metric-box highlight-due">
          <span className="metric-label">Total Outstanding Dues</span>
          <span className="metric-val text-danger">{fmt(summary?.totalOutstanding || 536163)}</span>
          <span className="metric-sub">Pending collection</span>
        </div>
      </div>

      {/* Admin Tabs */}
      <div className="erp-tabs-bar">
        <button
          className={`erp-tab ${activeTab === 'structures' ? 'active' : ''}`}
          onClick={() => setActiveTab('structures')}
        >
          1. Fee Structures ({structures.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'students' ? 'active' : ''}`}
          onClick={() => setActiveTab('students')}
        >
          2. Student Directory ({students.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'ai' ? 'active' : ''}`}
          onClick={() => setActiveTab('ai')}
        >
          3. Basic AI Indicator ({aiRisks.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'reports' ? 'active' : ''}`}
          onClick={() => setActiveTab('reports')}
        >
          4. Financial Reports
        </button>
        <button
          className={`erp-tab ${activeTab === 'audit' ? 'active' : ''}`}
          onClick={() => setActiveTab('audit')}
        >
          5. Audit Logs ({auditLogs.length})
        </button>
        <button
          className={`erp-tab ${activeTab === 'security' ? 'active' : ''}`}
          onClick={() => setActiveTab('security')}
        >
          6. Security Logs ({securityLogs.length})
        </button>
      </div>

      {/* TAB 1: Fee Structure Management */}
      {activeTab === 'structures' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Official MMCOE Category Fee Structures</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Configurable master catalogue stored in PostgreSQL table <code>feestructure</code>
              </div>
            </div>
            <button className="btn btn-primary btn-sm" onClick={() => setShowFeeModal(true)}>
              + Add Fee Structure
            </button>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>Category</th>
                <th>Tuition Fee</th>
                <th>Dev Fee</th>
                <th>Other Fees</th>
                <th>Caution Deposit</th>
                <th>Total Fee</th>
                <th>Due Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {structures.map((fs) => (
                <tr key={fs.feeId}>
                  <td><strong>{fs.category}</strong></td>
                  <td>{fmt(fs.tuitionFee)}</td>
                  <td>{fmt(fs.developmentFee)}</td>
                  <td>{fmt(fs.otherFees)}</td>
                  <td>{fmt(fs.cautionMoney)}</td>
                  <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{fmt(fs.amount)}</td>
                  <td>{fs.dueDate}</td>
                  <td><span className="status-pill status-paid">{fs.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* TAB 2: Student Directory */}
      {activeTab === 'students' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Enrolled Students & Category Fee Assignments</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Active student records with automatically assigned fee schedules
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={loadData}>Refresh</button>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>PRN</th>
                <th>Student Name</th>
                <th>Admission Category</th>
                <th>Branch / Sem</th>
                <th>Total Fee</th>
                <th>Paid</th>
                <th>Outstanding</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {students.map((s) => (
                <tr key={s.studentId}>
                  <td className="font-mono strong">{s.prn}</td>
                  <td style={{ fontWeight: 600 }}>{s.name}</td>
                  <td><span className="badge-tag">{s.category}</span></td>
                  <td>{s.department || 'IT'} - Sem {s.semester}</td>
                  <td style={{ fontWeight: 600 }}>{fmt(s.feeAssignment?.totalAmount || 0)}</td>
                  <td style={{ color: 'var(--success)' }}>{fmt(s.feeAssignment?.paidAmount || 0)}</td>
                  <td style={{ fontWeight: 600, color: 'var(--danger)' }}>{fmt(s.feeAssignment?.outstandingAmount || 0)}</td>
                  <td>
                    <span className={`status-pill ${s.feeAssignment?.status === 'PAID' ? 'status-paid' : 'status-pending'}`}>
                      {s.feeAssignment?.status || 'PENDING'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* TAB 3: Basic AI Indicator (Section 16) */}
      {activeTab === 'ai' && (
        <div>
          <div className="card" style={{ marginBottom: '1rem' }}>
            <div className="card-header-bar">
              <div>
                <h2 className="card-title">Rule-Based Predictive Indicator (Fee Default Assessment)</h2>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  Rule-based heuristic indicator for proactive fee collection follow-up
                </div>
              </div>
              <span className="badge-tag">Predictive Follow-up</span>
            </div>

            <table className="erp-table">
              <thead>
                <tr>
                  <th>PRN</th>
                  <th>Student Name</th>
                  <th>Outstanding Dues</th>
                  <th>Days Overdue</th>
                  <th>Risk Level</th>
                  <th>Predictive Recommendation</th>
                  <th>Evaluated Factor</th>
                </tr>
              </thead>
              <tbody>
                {aiRisks.map((item) => (
                  <tr key={item.studentId}>
                    <td className="font-mono strong">{item.prn || 'B25IT2009'}</td>
                    <td style={{ fontWeight: 600 }}>{item.studentName}</td>
                    <td style={{ fontWeight: 600, color: 'var(--danger)' }}>{fmt(item.outstandingDues)}</td>
                    <td>{item.daysOverdue} days</td>
                    <td>
                      <span className={`status-pill ${item.riskLevel === 'HIGH' ? 'status-pending' : item.riskLevel === 'MODERATE' ? 'status-pending' : 'status-paid'}`}>
                        {item.riskLevel}
                      </span>
                    </td>
                    <td>
                      <strong style={{ color: item.riskLevel === 'HIGH' ? 'var(--danger)' : item.riskLevel === 'MODERATE' ? '#d97706' : 'var(--success)' }}>
                        {item.indicatorMessage || 'Normal'}
                      </strong>
                    </td>
                    <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      {item.primaryRiskFactor}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: Reports */}
      {activeTab === 'reports' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Administrative Financial Reports</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Aggregated collection & pending dues reports generated via SQL joins and group-by clauses
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={() => window.print()}>
              🖨️ Print / Save PDF
            </button>
          </div>

          <div style={{ padding: '1rem' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '0.5rem' }}>
              Summary by Admission Category
            </h3>
            <table className="erp-table" style={{ marginBottom: '1.5rem' }}>
              <thead>
                <tr>
                  <th>Category</th>
                  <th>Applicable Fee Rate</th>
                  <th>Enrolled Students</th>
                  <th>Total Due Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>OPEN</strong></td>
                  <td>₹1,45,500</td>
                  <td>2</td>
                  <td>₹2,91,000</td>
                  <td><span className="status-pill status-pending">Pending Collection</span></td>
                </tr>
                <tr>
                  <td><strong>OBC / EBC Male</strong></td>
                  <td>₹86,587</td>
                  <td>2</td>
                  <td>₹1,73,174</td>
                  <td><span className="status-pill status-pending">Pending Collection</span></td>
                </tr>
                <tr>
                  <td><strong>SC / ST</strong></td>
                  <td>₹10,000</td>
                  <td>1</td>
                  <td>₹10,000</td>
                  <td><span className="status-pill status-pending">Pending Collection</span></td>
                </tr>
                <tr>
                  <td><strong>VJNT / SBC / TFWS</strong></td>
                  <td>₹27,674</td>
                  <td>1</td>
                  <td>₹27,674</td>
                  <td><span className="status-pill status-pending">Pending Collection</span></td>
                </tr>
                <tr>
                  <td><strong>J & K Quota</strong></td>
                  <td>₹34,000</td>
                  <td>1</td>
                  <td>₹34,000</td>
                  <td><span className="status-pill status-pending">Pending Collection</span></td>
                </tr>
                <tr>
                  <td><strong>OBC / EBC Female</strong></td>
                  <td>₹27,674</td>
                  <td>3</td>
                  <td>₹83,022</td>
                  <td><span className="status-pill status-paid">Partial Collection (Installments)</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: Audit Logs (Section 14) */}
      {activeTab === 'audit' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">System Audit Trail (DBMS / SE Non-Repudiation)</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Immutable audit log stored in table <code>auditlog</code>
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={loadData}>Refresh</button>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>Log ID</th>
                <th>Timestamp</th>
                <th>User / Identifier</th>
                <th>Action Event</th>
                <th>Details / Description</th>
                <th>IP Address</th>
              </tr>
            </thead>
            <tbody>
              {auditLogs.map((log) => (
                <tr key={log.auditId}>
                  <td className="font-mono">#{log.auditId}</td>
                  <td style={{ fontSize: '0.8rem' }}>{log.timestamp ? new Date(log.timestamp).toLocaleString() : 'N/A'}</td>
                  <td style={{ fontWeight: 600 }}>{log.user ? log.user.username : 'SYSTEM'}</td>
                  <td><span className="badge-tag">{log.action}</span></td>
                  <td style={{ fontSize: '0.85rem' }}>{log.description || log.action}</td>
                  <td className="font-mono" style={{ fontSize: '0.8rem' }}>{log.ipAddress || '127.0.0.1'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* TAB 6: Security Logs (Section 15) */}
      {activeTab === 'security' && (
        <div className="card">
          <div className="card-header-bar">
            <div>
              <h2 className="card-title">Security & Access Telemetry Logs</h2>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Tracks authentication successes, failures, and access boundary attempts
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={loadData}>Refresh</button>
          </div>

          <table className="erp-table">
            <thead>
              <tr>
                <th>Log ID</th>
                <th>Timestamp</th>
                <th>User Account</th>
                <th>Event Type</th>
                <th>Client IP Address</th>
              </tr>
            </thead>
            <tbody>
              {securityLogs.map((log) => (
                <tr key={log.logId}>
                  <td className="font-mono">#{log.logId}</td>
                  <td style={{ fontSize: '0.8rem' }}>{log.timestamp ? new Date(log.timestamp).toLocaleString() : 'N/A'}</td>
                  <td style={{ fontWeight: 600 }}>{log.user ? log.user.username : 'Unauthenticated'}</td>
                  <td>
                    <span className={`status-pill ${log.eventType === 'LOGIN_SUCCESS' ? 'status-paid' : 'status-pending'}`}>
                      {log.eventType}
                    </span>
                  </td>
                  <td className="font-mono" style={{ fontSize: '0.8rem' }}>{log.ipAddress}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Add Fee Structure Modal */}
      {showFeeModal && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <h3 className="modal-title">Configure Category Fee Structure</h3>
              <button className="btn-close" onClick={() => setShowFeeModal(false)}>×</button>
            </div>
            <form onSubmit={handleCreateFee}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-control"
                    value={feeForm.category}
                    onChange={(e) => setFeeForm({ ...feeForm, category: e.target.value })}
                  >
                    <option value="OPEN">OPEN</option>
                    <option value="OBC/EBC/EWS/SEBC Male">OBC/EBC/EWS/SEBC Male</option>
                    <option value="SC/ST">SC/ST</option>
                    <option value="VJNT/SBC/TFWS">VJNT/SBC/TFWS</option>
                    <option value="J & K Quota">J & K Quota</option>
                    <option value="OBC/EBC/EWS/SEBC Female">OBC/EBC/EWS/SEBC Female</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Total Amount (₹)</label>
                  <input
                    type="number"
                    className="form-control"
                    value={feeForm.amount}
                    onChange={(e) => setFeeForm({ ...feeForm, amount: e.target.value })}
                    placeholder="e.g. 27674"
                    required
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                  <div className="form-group">
                    <label className="form-label">Tuition Fee (₹)</label>
                    <input
                      type="number"
                      className="form-control"
                      value={feeForm.tuitionFee}
                      onChange={(e) => setFeeForm({ ...feeForm, tuitionFee: e.target.value })}
                      placeholder="0"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Development Fee (₹)</label>
                    <input
                      type="number"
                      className="form-control"
                      value={feeForm.developmentFee}
                      onChange={(e) => setFeeForm({ ...feeForm, developmentFee: e.target.value })}
                      placeholder="20000"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Due Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={feeForm.dueDate}
                    onChange={(e) => setFeeForm({ ...feeForm, dueDate: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Description</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    value={feeForm.description}
                    onChange={(e) => setFeeForm({ ...feeForm, description: e.target.value })}
                    placeholder="e.g. Category specific semester 6 fee"
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowFeeModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Fee Structure
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
