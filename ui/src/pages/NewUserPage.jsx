import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { userApi } from '../api/userApi';
import UserForm from '../components/users/UserForm';

function NewUserPage() {
  const navigate = useNavigate();

  const handleSubmit = async (formData) => {
  console.log('Form data being submitted:', formData);
  
  try {
    const response = await userApi.create(formData);
    console.log('Success response:', response);
    navigate('/users');
  } catch (error) {
    console.error('Full error object:', error);
    console.error('Error response data:', error.response?.data);
    console.error('Error status:', error.response?.status);
    
    const errorMessage = error.response?.data?.errors || 
                        error.response?.data?.message || 
                        'Failed to create user. Please try again.';
    alert(errorMessage);
  }
};

  return <UserForm onSubmit={handleSubmit} />;
}

export default NewUserPage;