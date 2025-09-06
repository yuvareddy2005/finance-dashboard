import axios from 'axios';

// Create an Axios instance with a base URL for our backend
const apiService = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Make sure this matches your backend port
});

// Use an interceptor to automatically attach the JWT to every request
// This is a professional practice that keeps our component logic clean
apiService.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiService;