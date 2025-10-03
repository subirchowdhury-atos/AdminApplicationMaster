// ========================================
// UsersTable.jsx
// ========================================
import { Link } from 'react-router-dom';

function UsersTable({ users }) {
  return (
    <div>
      <h3>Listing Users</h3>
      <Link to="/users/new" className="btn btn-info pull-right">
        New User
      </Link>
      <table className="table table-bordered table-hover" id="simple-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          {users.length === 0 ? (
            <tr>
              <td colSpan="6" className="text-center">No users found</td>
            </tr>
          ) : (
            users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.firstName}</td>
                <td>{user.lastName}</td>
                <td>{user.email}</td>
                <td>{user.contact}</td>
                <td>{user.role}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default UsersTable;