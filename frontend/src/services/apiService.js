import axios from 'axios';

// Create a pre-configured instance of axios
const apiService = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // The base URL of our Spring Boot backend
});

// We will add more configuration here later, like attaching the JWT.

export default apiService;