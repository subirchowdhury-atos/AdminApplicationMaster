import { useEffect, useState } from 'react';

export default function FlashMessage({ message, type = 'info', duration = 5000, onClose }) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        setIsVisible(false);
        if (onClose) onClose();
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [duration, onClose]);

  if (!isVisible || !message) return null;

  const typeStyles = {
    success: {
      backgroundColor: '#d4edda',
      borderColor: '#c3e6cb',
      color: '#155724',
    },
    error: {
      backgroundColor: '#f8d7da',
      borderColor: '#f5c6cb',
      color: '#721c24',
    },
    warning: {
      backgroundColor: '#fff3cd',
      borderColor: '#ffeaa7',
      color: '#856404',
    },
    info: {
      backgroundColor: '#d1ecf1',
      borderColor: '#bee5eb',
      color: '#0c5460',
    },
  };

  const style = typeStyles[type] || typeStyles.info;

  return (
    <div
      style={{
        padding: '12px 20px',
        marginBottom: '20px',
        border: `1px solid ${style.borderColor}`,
        borderRadius: '4px',
        backgroundColor: style.backgroundColor,
        color: style.color,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        fontSize: '14px',
      }}
    >
      <span>{message}</span>
      <button
        onClick={() => {
          setIsVisible(false);
          if (onClose) onClose();
        }}
        style={{
          background: 'none',
          border: 'none',
          fontSize: '20px',
          cursor: 'pointer',
          color: style.color,
          marginLeft: '10px',
          padding: '0 5px',
        }}
        aria-label="Close"
      >
        ×
      </button>
    </div>
  );
}