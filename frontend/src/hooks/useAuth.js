// src/hooks/useAuth.js
import { useState, useEffect } from "react";

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
        return {
            isLoggedIn: false,
            userId: null,
            email: null,
            role: null,
        };
    }

    const decoded = parseJwt(token);

    return {
        isLoggedIn: true,
        userId: decoded?.sub || decoded?.userId || null,
        email: decoded?.email || null,
        role: decoded?.role || null,
    };
};

export default function useAuth() {
    const [auth, setAuth] = useState(getAuthData());

    useEffect(() => {
        const update = () => setAuth(getAuthData());
        window.addEventListener("storage", update);
        return () => window.removeEventListener("storage", update);
    }, []);

    const refreshAuth = () => setAuth(getAuthData());

    return { ...auth, refreshAuth };
}
