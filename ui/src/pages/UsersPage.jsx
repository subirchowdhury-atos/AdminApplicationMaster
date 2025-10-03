// ========================================
// UsersPage.jsx (index)
// ========================================
import { useState, useEffect } from 'react';
import { userApi } from '../api/userApi';
import UsersTable from '../components/users/UsersTable';

function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await userApi.getAll();
      // Handle both response.data and direct response
      setUsers(response.data || response || []);
    } catch (error) {
      console.error('Error fetching users:', error);
      setUsers([]); // Set empty array on error
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center p-5">Loading...</div>;
  }

  return (
    <div className="main-content-inner">
      <div className="page-content">
        <div className="row">
          <div className="col-xs-12">
            <UsersTable users={users} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default UsersPage;