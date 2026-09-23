import React, { useState, useRef, useEffect } from 'react';

export default function Navbar({ user, onLogout, onHome, onOpenProfile, onOpenPasswordChange }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const formatRole = (role) => {
    if (!role) return '';
    if (role === 'ROLE_STUDENT') return 'Student';
    if (role === 'ROLE_ACCOUNTS_OFFICER') return 'Accounts Officer';
    if (role === 'ROLE_ADMIN') return 'System Administrator';
    return role.replace('ROLE_', '').replace('_', ' ');
  };

  const isStudent = user && (user.role === 'ROLE_STUDENT');

  return (
    <header className="mmcoe-header-container">
      {/* Primary College Branding Bar */}
      <div className="mmcoe-brand-strip">
        <div
          className="mmcoe-brand-left"
          onClick={() => { if (onHome) onHome(); else window.location.href = '/'; }}
          style={{ cursor: 'pointer' }}
          title="Return to Home Dashboard"
          role="button"
          tabIndex={0}
        >
          {/* MMCOE Official Logo */}
          <div className="mmcoe-logo-badge">
            <img src="/mmcoe_logo.png" alt="Marathwada Mitra Mandal, Pune" className="mmcoe-navbar-logo" />
          </div>
          <div>
            <div className="mmcoe-college-name">Marathwada Mitra Mandal's College of Engineering</div>
            <div className="mmcoe-college-sub">Karvenagar, Pune | Accredited with 'A' Grade by NAAC | Affiliated to SPPU</div>
          </div>
        </div>

        {/* Header Right Quick Links */}
        <div className="mmcoe-header-links">
          {user && (
            <div className="nav-actions">
              <button
                type="button"
                className="nav-link-btn"
                title="Home Dashboard"
                onClick={() => { if (onHome) onHome(); else window.location.href = '/'; }}
                style={{
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.35rem',
                  fontSize: '0.85rem',
                  fontWeight: 600,
                  color: '#1e3a8a'
                }}
              >
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                  <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                Home
              </button>
              <span className="nav-link-btn notification-badge" title="Notifications">
                Notifications <span className="badge-pill">2</span>
              </span>

              {/* Profile Dropdown Menu */}
              <div className="profile-dropdown-wrapper" ref={dropdownRef}>
                <button
                  type="button"
                  className="profile-menu-trigger"
                  onClick={() => setDropdownOpen(!dropdownOpen)}
                  aria-expanded={dropdownOpen}
                >
                  <span className="profile-avatar-circle">
                    {(user.studentName || user.username || 'U').charAt(0).toUpperCase()}
                  </span>
                  <span className="profile-name-label">
                    {user.studentName || user.username}
                  </span>
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <path d="M6 9l6 6 6-6"/>
                  </svg>
                </button>

                {dropdownOpen && (
                  <div className="profile-dropdown-menu">
                    <div className="dropdown-user-header">
                      <div className="user-title">{user.studentName || user.username}</div>
                      <div className="user-role-sub">{formatRole(user.role)}</div>
                    </div>
                    {isStudent && (
                      <button
                        type="button"
                        className="dropdown-item"
                        onClick={() => { setDropdownOpen(false); onOpenProfile && onOpenProfile(); }}
                      >
                        User Profile
                      </button>
                    )}
                    <button
                      type="button"
                      className="dropdown-item"
                      onClick={() => { setDropdownOpen(false); onOpenPasswordChange && onOpenPasswordChange(); }}
                    >
                      Change Password
                    </button>
                    <div className="dropdown-divider" />
                    <button
                      type="button"
                      className="dropdown-item logout-item"
                      onClick={() => { setDropdownOpen(false); onLogout(); }}
                    >
                      Log Out
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Student Banner Bar (Section 4: Exact Visual Reference) */}
      {isStudent && (
        <div className="mmcoe-student-ribbon">
          <div className="student-ribbon-item">
            <span className="ribbon-label">STUDENT:</span>
            <span className="ribbon-value strong">{user.studentName || 'SABURI YEOLA'}</span>
          </div>
          <div className="ribbon-sep">•</div>
          <div className="student-ribbon-item">
            <span className="ribbon-label">REGISTRATION NO:</span>
            <span className="ribbon-value font-mono">{user.prn || 'B25IT2009'}</span>
          </div>
          <div className="ribbon-sep">•</div>
          <div className="student-ribbon-item">
            <span className="ribbon-label">BRANCH:</span>
            <span className="ribbon-value">{user.branch || 'B.TECH. INFORMATION TECHNOLOGY'}</span>
          </div>
        </div>
      )}
    </header>
  );
}
