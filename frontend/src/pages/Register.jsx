import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../services/api";

export default function Register() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ firstName: "", lastName: "", email: "", password: "", role: "STUDENT" });

    const submit = async (e) => {
        e.preventDefault();
        try {
            const res = await api.post("/auth/register", form);
            const { token, role, userId } = res.data || {};
            if (!token) throw new Error("Token missing");
            localStorage.setItem("jwt", token);
            if (role) localStorage.setItem("role", role);
            if (userId) localStorage.setItem("userId", userId);
            toast.success("Registered");
            navigate("/");
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data || "Registration failed");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-6">
            <form onSubmit={submit} className="w-full max-w-md bg-white p-6 rounded-xl shadow border space-y-3">
                <h2 className="text-xl font-bold">Create account</h2>
                <div className="grid grid-cols-2 gap-2">
                    <input value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} className="border rounded p-2" placeholder="First name" />
                    <input value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} className="border rounded p-2" placeholder="Last name" />
                </div>
                <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="w-full border rounded p-2" placeholder="Email" />
                <input value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} type="password" className="w-full border rounded p-2" placeholder="Password" />
                <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })} className="w-full border rounded p-2">
                    <option value="STUDENT">Student</option>
                    <option value="ADMIN">Admin</option>
                </select>
                <button type="submit" className="w-full bg-sky-600 text-white rounded py-2">Register</button>
                <p className="text-sm">Already have an account? <a href="/login" className="text-sky-600">Login</a></p>
            </form>
        </div>
    );
}
