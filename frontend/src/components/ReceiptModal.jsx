import React from 'react';

export default function ReceiptModal({ receipt, onClose }) {
  if (!receipt) return null;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: 600 }}>
        <div className="modal-header">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Official Fee Payment Receipt</h3>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>✕</button>
        </div>

        <div className="modal-body">
          <div className="receipt-box" id="printable-receipt">
            <div style={{ textAlign: 'center', borderBottom: '1px solid #ccc', paddingBottom: '0.75rem', marginBottom: '1rem' }}>
              <div style={{ fontWeight: 700, fontSize: '1.1rem' }}>MARATHWADA MITRA MANDAL'S COLLEGE OF ENGINEERING</div>
              <div style={{ fontSize: '0.8rem', color: '#555' }}>Karvenagar, Pune - 411052 • Dept. of Information Technology</div>
              <div style={{ fontWeight: 600, marginTop: '0.5rem', textTransform: 'uppercase', letterSpacing: 1 }}>E-Fee Payment Receipt</div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '1rem' }}>
              <div>
                <span style={{ color: '#777' }}>Receipt Number:</span><br />
                <strong>{receipt.receiptNumber}</strong>
              </div>
              <div>
                <span style={{ color: '#777' }}>Date & Time:</span><br />
                <strong>{new Date(receipt.generatedDate).toLocaleString()}</strong>
              </div>
              <div>
                <span style={{ color: '#777' }}>Student Name:</span><br />
                <strong>{receipt.studentName || 'Student'}</strong>
              </div>
              <div>
                <span style={{ color: '#777' }}>Student ID:</span><br />
                <strong>#{receipt.studentId || receipt.paymentId}</strong>
              </div>
              <div>
                <span style={{ color: '#777' }}>Course / Program:</span><br />
                <strong>{receipt.course || 'B.E. Information Technology'}</strong>
              </div>
              <div>
                <span style={{ color: '#777' }}>Academic Year:</span><br />
                <strong>{receipt.academicYear || '2026-2027'}</strong>
              </div>
            </div>

            <table style={{ width: '100%', borderTop: '1px solid #ccc', borderBottom: '1px solid #ccc', marginBottom: '1rem' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: '0.5rem 0' }}>Description</th>
                  <th style={{ textAlign: 'right', padding: '0.5rem 0' }}>Amount (INR)</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td style={{ padding: '0.5rem 0' }}>{receipt.feeType || 'College Fee Settlement'}</td>
                  <td style={{ textAlign: 'right', padding: '0.5rem 0', fontWeight: 600 }}>
                    ₹ {Number(receipt.amountPaid).toLocaleString('en-IN')}
                  </td>
                </tr>
              </tbody>
            </table>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <div>
                <div style={{ fontSize: '0.75rem', color: '#777' }}>Payment Method: {receipt.paymentMethod}</div>
                <div style={{ fontSize: '0.75rem', color: '#777' }}>Gateway Ref: <code>{receipt.gatewayReference}</code></div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '0.8rem', color: '#777' }}>Status</div>
                <strong style={{ color: '#16a34a' }}>VERIFIED & PAID</strong>
              </div>
            </div>

            <div style={{ textAlign: 'center', fontSize: '0.75rem', color: '#888', borderTop: '1px solid #eee', paddingTop: '0.5rem' }}>
              This is a computer-generated receipt issued per SRS FR12. No physical signature required.
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Close</button>
          <button className="btn btn-primary" onClick={handlePrint}>Print / Save PDF</button>
        </div>
      </div>
    </div>
  );
}
