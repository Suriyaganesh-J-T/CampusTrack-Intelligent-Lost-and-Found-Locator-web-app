import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

console.log("API Base URL:", API_BASE); // ✅ Works fine

const api = axios.create({
    baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;
