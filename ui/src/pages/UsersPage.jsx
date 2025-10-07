import { useState, useEffect } from 'react';
import { userApi } from '../api/userApi';
import UsersTable from '../components/users/UsersTable';
import '../styles/UsersPage.css';

function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await userApi.getAll();
      setUsers(response || []);
    } catch (error) {
      console.error('Error fetching users:', error);
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading-container">Loading users...</div>;
  }

  return <UsersTable users={users} />;
}

export default UsersPage;