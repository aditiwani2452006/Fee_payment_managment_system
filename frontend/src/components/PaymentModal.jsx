import React, { useState } from 'react';
import { apiInitiatePayment, apiProcessPayment } from '../api';

export default function PaymentModal({ item, onClose, onSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [step, setStep] = useState('INITIATE'); // INITIATE -> SIMULATE_GATEWAY -> PROCESSING
  const [initiatedData, setInitiatedData] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('RAZORPAY_TEST');

  const amountToPay = item.installmentAmount || item.outstandingAmount;

  const handleInitiate = async () => {
    setLoading(true);
    setError('');
    try {
      const assignmentId = item.assignmentId;
      const installmentId = item.installmentId || null;

      const res = await apiInitiatePayment(assignmentId, installmentId, amountToPay, paymentMethod);
      setInitiatedData(res.data);
      setStep('SIMULATE_GATEWAY');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmSimulation = async () => {
    setLoading(true);
    setError('');
    try {
      // Simulates successful Razorpay callback with generated payment reference
      const paymentId = initiatedData.paymentId;
      const gatewayRef = initiatedData.gatewayReference;
      
      const res = await apiProcessPayment(paymentId, gatewayRef, 'simulated_rzp_sig_verified');
      onSuccess(res.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>
            {step === 'INITIATE' ? 'Initiate Fee Payment' : 'Razorpay Test Mode Checkout'}
          </h3>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>✕</button>
        </div>

        <div className="modal-body">
          {error && (
            <div style={{ padding: '0.75rem', background: '#fee2e2', color: '#b91c1c', borderRadius: 6, marginBottom: '1rem', fontSize: '0.85rem' }}>
              {error}
            </div>
          )}

          {step === 'INITIATE' ? (
            <div>
              <div style={{ marginBottom: '1rem', padding: '0.75rem', background: '#f8fafc', borderRadius: 6 }}>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Payment Item</div>
                <div style={{ fontWeight: 600 }}>{item.feeType || 'College Fee'}</div>
                {item.installmentNo && <div>Installment #{item.installmentNo}</div>}
                <div style={{ marginTop: '0.5rem', fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary)' }}>
                  ₹ {Number(amountToPay).toLocaleString('en-IN')}
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Payment Gateway Mode</label>
                <select className="form-control" value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
                  <option value="RAZORPAY_TEST">Razorpay Sandbox / Test Mode (Simulated)</option>
                  <option value="UPI">UPI Test Simulator</option>
                  <option value="NET_BANKING">Net Banking Test Simulator</option>
                </select>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                  Demonstrating CN & Security: Simulated transaction token, no real money transferred.
                </div>
              </div>

              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', background: '#f1f5f9', padding: '0.5rem', borderRadius: 4 }}>
                <span className="concept-tag">OS</span> Pessimistic lock (SELECT ... FOR UPDATE) will be acquired upon confirmation.
              </div>
            </div>
          ) : (
            <div>
              <div style={{ background: '#0c2340', color: '#fff', padding: '1rem', borderRadius: 6, marginBottom: '1rem' }}>
                <div style={{ fontSize: '0.75rem', opacity: 0.8, textTransform: 'uppercase' }}>Razorpay Test Checkout Simulator</div>
                <div style={{ fontSize: '1.3rem', fontWeight: 700, marginTop: '0.25rem' }}>
                  ₹ {Number(amountToPay).toLocaleString('en-IN')}
                </div>
                <div style={{ fontSize: '0.75rem', opacity: 0.9, marginTop: '0.5rem' }}>
                  Simulated Order ID: <code>{initiatedData?.gatewayReference}</code>
                </div>
              </div>

              <div style={{ fontSize: '0.85rem', marginBottom: '1rem', color: 'var(--text-muted)' }}>
                Click below to simulate a successful payment authorization and trigger backend transaction verification, balance deduction, and receipt generation.
              </div>
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose} disabled={loading}>
            Cancel
          </button>
          {step === 'INITIATE' ? (
            <button className="btn btn-primary" onClick={handleInitiate} disabled={loading}>
              {loading ? 'Initializing Order...' : 'Proceed to Checkout'}
            </button>
          ) : (
            <button className="btn btn-success" onClick={handleConfirmSimulation} disabled={loading}>
              {loading ? 'Processing Transaction...' : 'Simulate Successful Payment'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
