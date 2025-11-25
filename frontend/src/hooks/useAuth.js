// File: src/hooks/useAuth.js

import { useState, useEffect } from "react";

// Helper function to decode JWT
const parseJwt = (token) => {
    try {
        return JSON.parse(atob(token.split(".")[1]));
    } catch {
        return null;
    }
};

const getAuthData = () => {
    const token = localStorage.getItem("jwt");
    if (!token) {
        return { isLoggedIn: false, userId: null, userName: null, userRole: null };
    }
    const decoded = parseJwt(token);
    return {
        isLoggedIn: true,
        userId: decoded?.userId || null,
        userName: decoded?.sub || decoded?.email || null, // Assuming subject is email/name
        userRole: decoded?.role || null,
    };
};

export default function useAuth() {
    const [authData, setAuthData] = useState(getAuthData());

    useEffect(() => {
        const handler = () => setAuthData(getAuthData());

        // Listen for changes in localStorage (login/logout events)
        window.addEventListener("storage", handler);
        return () => window.removeEventListener("storage", handler);
    }, []);

    // Function to manually refresh data if needed (e.g., after login)
    const refreshAuthData = () => setAuthData(getAuthData());

    return { ...authData, refreshAuthData };
}