import axios from 'axios';

const API_URL = process.env.REACT_APP_BACK_URL;

const api = axios.create({
    baseURL: API_URL,
})

api.interceptors.request.use((config) => {
    config.headers.Authorization = `Bearer ${localStorage.getItem('token')}`;
    return config;
})

export default api;