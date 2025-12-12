import React, { useEffect, useState } from "react";
import api from "../services/api";
import toast from "react-hot-toast";
import {
    PieChart,
    Pie,
    Cell,
    Tooltip as ReTooltip,
    Legend as ReLegend,
    ResponsiveContainer,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
} from "recharts";

const LOST_COLOR = "#ef4444";     // red
const FOUND_COLOR = "#22c55e";    // green
const RECOVERED_COLOR = "#3b82f6"; // blue

export default function AdminDashboard() {
    const [users, setUsers] = useState([]);
    const [reports, setReports] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            setLoading(true);
            try {
                const [usersRes, summaryRes] = await Promise.all([
                    api.get("/admin/users"),
                    api.get("/admin/reports/summary"),
                ]);
                setUsers(Array.isArray(usersRes.data) ? usersRes.data : []);
                setReports(summaryRes.data || {});
            } catch (err) {
                console.error("Admin load error:", err);
                toast.error("Failed to load admin data.");
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    const verifyUser = async (userId, verified) => {
        try {
            await api.patch(`/admin/users/${userId}/verify`, null, { params: { verified } });
            toast.success(`User ${verified ? "verified" : "unverified"} successfully`);
            setUsers((prev) =>
                prev.map((u) => (u.userId === userId ? { ...u, isVerified: verified } : u))
            );
        } catch (err) {
            console.error("Verify error:", err);
            toast.error("Failed to update verification status.");
        }
    };

    const changeRole = async (userId, role) => {
        try {
            await api.patch(`/admin/users/${userId}/role`, null, { params: { role } });
            toast.success(`Role updated to ${role}`);
            setUsers((prev) =>
                prev.map((u) => (u.userId === userId ? { ...u, role } : u))
            );
        } catch (err) {
            console.error("Role error:", err);
            toast.error("Failed to change role.");
        }
    };

    if (loading) return <div className="p-6">Loading...</div>;

    const lost = reports?.lostPosts || 0;
    const found = reports?.foundPosts || 0;
    const recovered = reports?.recoveredItems || 0;

    const pieData = [
        { name: "Lost items", value: lost, color: LOST_COLOR },
        { name: "Found items", value: found, color: FOUND_COLOR },
        { name: "Recovered items", value: recovered, color: RECOVERED_COLOR },
    ];

    const platformBars = [
        { name: "Users", value: reports?.totalUsers || 0 },
        { name: "Matches", value: reports?.totalMatches || 0 },
        { name: "Messages", value: reports?.totalMessages || 0 },
    ];

    const topCards = [
        {
            label: "Total Activity",
            helper: "Items reported",
            value: reports?.totalPosts || 0,
        },
        {
            label: "Success Stories",
            helper: "Items recovered",
            value: recovered,
        },
        {
            label: "Active Members",
            helper: "Registered users",
            value: reports?.totalUsers || 0,
        },
    ];

    return (
        <div className="max-w-6xl mx-auto p-6 space-y-8">
            <h1 className="text-3xl font-extrabold">Admin Dashboard</h1>

            {/* Summary cards */}
            <section className="grid gap-4 md:grid-cols-3">
                {topCards.map((c) => (
                    <div
                        key={c.label}
                        className="bg-white rounded-xl shadow border p-4 flex flex-col justify-between"
                    >
                        <div className="text-sm text-slate-500">{c.label}</div>
                        <div className="text-3xl font-bold mt-2">{c.value}</div>
                        <div className="text-xs text-slate-400 mt-1">{c.helper}</div>
                    </div>
                ))}
            </section>

            {/* Analytics charts */}
            <section className="bg-white p-6 rounded-xl shadow border">
                <h2 className="text-xl font-bold mb-4">Analytics</h2>

                <div className="grid md:grid-cols-2 gap-6">
                    {/* Pie chart */}
                    <div className="h-72">
                        <h3 className="font-semibold mb-2">Lost vs Found vs Recovered</h3>
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie
                                    data={pieData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={90}
                                    label={({ name, value }) => `${value}`}
                                >
                                    {pieData.map((entry, idx) => (
                                        <Cell key={entry.name} fill={entry.color} />
                                    ))}
                                </Pie>
                                <ReTooltip />
                                <ReLegend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>

                    {/* Bar chart */}
                    <div className="h-72">
                        <h3 className="font-semibold mb-2">Platform Overview</h3>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={platformBars}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis dataKey="name" />
                                <YAxis allowDecimals={false} />
                                <ReTooltip />
                                <Bar dataKey="value" fill="#6366f1" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </section>

            {/* User management */}
            <section className="bg-white p-6 rounded-xl shadow border">
                <h2 className="text-xl font-bold mb-4">User Management</h2>
                <div className="space-y-3">
                    {users.length === 0 ? (
                        <div className="text-slate-500">No users available.</div>
                    ) : (
                        users.map((u) => (
                            <div
                                key={u.userId}
                                className="flex items-center justify-between border rounded p-3"
                            >
                                <div>
                                    <div className="font-semibold">
                                        {u.firstName} {u.lastName}
                                    </div>
                                    <div className="text-sm text-slate-600">{u.email}</div>
                                    <div className="text-xs text-slate-500">
                                        Role: {u.role} | Verified: {String(u.isVerified)}
                                    </div>
                                </div>
                                <div className="flex gap-2">
                                    <button
                                        onClick={() => verifyUser(u.userId, !u.isVerified)}
                                        className="px-3 py-1 rounded bg-sky-600 text-white text-sm"
                                    >
                                        {u.isVerified ? "Unverify" : "Verify"}
                                    </button>
                                    <button
                                        onClick={() =>
                                            changeRole(
                                                u.userId,
                                                u.role === "ADMIN" ? "STUDENT" : "ADMIN"
                                            )
                                        }
                                        className="px-3 py-1 rounded bg-indigo-600 text-white text-sm"
                                    >
                                        Toggle role
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </section>
        </div>
    );
}
