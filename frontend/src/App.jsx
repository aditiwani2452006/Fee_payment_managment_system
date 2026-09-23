import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import StudentDashboard from './pages/StudentDashboard';
import AccountsDashboard from './pages/AccountsDashboard';
import AdminDashboard from './pages/AdminDashboard';
import { getSavedUser, removeToken } from './api';

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Cross-component modal and tab triggers
  const [studentTab, setStudentTab] = useState('fees');
  const [showPwdModal, setShowPwdModal] = useState(false);

  useEffect(() => {
    const user = getSavedUser();
    if (user) {
      setCurrentUser(user);
    }
    setLoading(false);
  }, []);

  const handleLoginSuccess = (userData) => {
    setCurrentUser(userData);
    setStudentTab('fees');
  };

  const handleLogout = () => {
    removeToken();
    setCurrentUser(null);
  };

  const [homeTrigger, setHomeTrigger] = useState(0);

  const handleHome = () => {
    setStudentTab('fees');
    setShowPwdModal(false);
    setHomeTrigger((prev) => prev + 1);
  };

  if (loading) {
    return null;
  }

  if (!currentUser) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="app-root">
      <Navbar
        user={currentUser}
        onLogout={handleLogout}
        onHome={handleHome}
        onOpenProfile={() => setStudentTab('profile')}
        onOpenPasswordChange={() => setShowPwdModal(true)}
      />
      <main>
        {currentUser.role === 'ROLE_STUDENT' && (
          <StudentDashboard
            user={currentUser}
            openTab={studentTab}
            setOpenTab={setStudentTab}
            homeTrigger={homeTrigger}
            showPwdModal={showPwdModal}
            setShowPwdModal={setShowPwdModal}
          />
        )}
        {currentUser.role === 'ROLE_ACCOUNTS_OFFICER' && <AccountsDashboard homeTrigger={homeTrigger} />}
        {currentUser.role === 'ROLE_ADMIN' && <AdminDashboard homeTrigger={homeTrigger} />}
      </main>
    </div>
  );
}
