import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../services/api";

export default function Login() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const submit = async (e) => {
        e.preventDefault();
        try {
            const res = await api.post("/auth/login", { email, password });
            const { token, role, userId } = res.data || {};
            if (!token) throw new Error("Token missing");
            localStorage.setItem("jwt", token);
            if (role) localStorage.setItem("role", role);
            if (userId) localStorage.setItem("userId", userId);
            toast.success("Logged in");
            navigate("/");
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data || "Login failed");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-6">
            <form onSubmit={submit} className="w-full max-w-sm bg-white p-6 rounded-xl shadow border space-y-3">
                <h2 className="text-xl font-bold">Login</h2>
                <input value={email} onChange={(e) => setEmail(e.target.value)} className="w-full border rounded p-2" placeholder="Email" />
                <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" className="w-full border rounded p-2" placeholder="Password" />
                <button type="submit" className="w-full bg-slate-800 text-white rounded py-2">Login</button>
                <p className="text-sm">No account? <a href="/register" className="text-sky-600">Register</a></p>
            </form>
        </div>
    );
}
