import React, { useState } from 'react';
import { apiLogin, apiResetPassword, setToken, saveUser } from '../api';

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('B25IT2009');
  const [password, setPassword] = useState('password123');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Forgot password modal state
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [resetIdentifier, setResetIdentifier] = useState('B25IT2009');
  const [newPassword, setNewPassword] = useState('password123');
  const [resetMsg, setResetMsg] = useState('');
  const [resetLoading, setResetLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await apiLogin(username.trim(), password);
      const data = res.data;
      setToken(data.token);
      saveUser(data);
      onLoginSuccess(data);
    } catch (err) {
      setError(err.message || 'Invalid PRN or password. Please verify your credentials.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setResetLoading(true);
    setResetMsg('');
    try {
      await apiResetPassword(resetIdentifier.trim(), newPassword);
      setResetMsg('Password successfully reset! You can now log in with the new password.');
      setPassword(newPassword);
      setUsername(resetIdentifier);
    } catch (err) {
      setResetMsg('Error: ' + (err.message || 'Unable to reset password.'));
    } finally {
      setResetLoading(false);
    }
  };

  const fillQuickLogin = (u, p) => {
    setUsername(u);
    setPassword(p);
    setError('');
  };

  return (
    <div className="mmcoe-login-wrapper">
      <div className="mmcoe-login-card">
        {/* College Header Banner */}
        <div className="login-college-header">
          <div className="login-logo-circle">
            <img src="/mmcoe_logo.png" alt="Marathwada Mitra Mandal, Pune" className="login-logo-img" />
          </div>
          <h1 className="login-college-title">Marathwada Mitra Mandal's<br/>College of Engineering, Pune</h1>
          <div className="login-module-banner">Fee Payment Management Platform</div>
        </div>

        {error && (
          <div className="alert-box alert-danger" role="alert">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label className="form-label">Registration No. / PRN / Username</label>
            <div className="input-with-icon">
              <input
                type="text"
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="e.g. B25IT2009 or officer1"
                required
                autoComplete="username"
              />
            </div>
            <span className="input-hint">Students: Enter your college allotted PRN</span>
          </div>

          <div className="form-group">
            <div className="label-row">
              <label className="form-label">Password</label>
              <button
                type="button"
                className="btn-link-forgot"
                onClick={() => { setShowForgotModal(true); setResetMsg(''); }}
              >
                Forgot Password?
              </button>
            </div>
            <input
              type="password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Authenticating with JWT...' : 'Sign In to Portal'}
          </button>
        </form>

        {/* 1-Click Demo Accounts */}
        <div className="demo-accounts-panel">
          <div className="demo-header">Quick Demo Logins:</div>
          <div className="demo-btn-grid">
            <button
              type="button"
              className={`demo-btn ${username === 'B25IT2009' ? 'active' : ''}`}
              onClick={() => fillQuickLogin('B25IT2009', 'password123')}
            >
              <span className="demo-role">Student (Saburi)</span>
              <span className="demo-id font-mono">B25IT2009</span>
            </button>
            <button
              type="button"
              className={`demo-btn ${username === 'officer1' ? 'active' : ''}`}
              onClick={() => fillQuickLogin('officer1', 'password123')}
            >
              <span className="demo-role">Accounts Officer</span>
              <span className="demo-id font-mono">officer1</span>
            </button>
            <button
              type="button"
              className={`demo-btn ${username === 'admin1' ? 'active' : ''}`}
              onClick={() => fillQuickLogin('admin1', 'password123')}
            >
              <span className="demo-role">System Admin</span>
              <span className="demo-id font-mono">admin1</span>
            </button>
          </div>
        </div>
      </div>

      {/* Forgot / Reset Password Modal */}
      {showForgotModal && (
        <div className="modal-backdrop">
          <div className="modal-box" style={{ maxWidth: 450 }}>
            <div className="modal-header">
              <h3 className="modal-title">Reset Portal Password</h3>
              <button className="btn-close" onClick={() => setShowForgotModal(false)}>×</button>
            </div>
            <form onSubmit={handleResetPassword}>
              <div className="modal-body">
                <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '1rem' }}>
                  Academic Demonstration Workflow: Enter your Student PRN or Username along with your new desired password.
                </p>

                {resetMsg && (
                  <div className={`alert-box ${resetMsg.startsWith('Error') ? 'alert-danger' : 'alert-success'}`}>
                    {resetMsg}
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Student PRN / Username</label>
                  <input
                    type="text"
                    className="form-control"
                    value={resetIdentifier}
                    onChange={(e) => setResetIdentifier(e.target.value)}
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
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowForgotModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={resetLoading}>
                  {resetLoading ? 'Resetting...' : 'Save New Password'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
