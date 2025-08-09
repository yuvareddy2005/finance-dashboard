import axios from 'axios';

// Create a pre-configured instance of axios
const apiService = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // The base URL of our Spring Boot backend
});

// Add a request interceptor
apiService.interceptors.request.use(
  (config) => {
    // Get the token from local storage
    const token = localStorage.getItem('token');
    
    // If the token exists, add it to the Authorization header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    // Do something with request error
    return Promise.reject(error);
  }
);

export default apiService;
