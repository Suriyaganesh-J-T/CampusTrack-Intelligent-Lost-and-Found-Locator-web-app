// src/components/Navbar.jsx
import React, { useEffect, useState } from "react";
import { Link, NavLink } from "react-router-dom";
import wsService from "../services/wsService";

const parseJwt = (token) => {
    try {
        return JSON.parse(atob(token.split(".")[1]));
    } catch {
        return null;
    }
};

export default function Navbar({ onOpenMobile }) {
    const [open, setOpen] = useState(false);
    const [newRequests, setNewRequests] = useState(0);
    const [role, setRole] = useState(null);

    const token = localStorage.getItem("jwt");

    useEffect(() => {
        if (token) {
            const decoded = parseJwt(token);
            setRole(decoded?.role || null);
        } else {
            setRole(null);
        }
    }, [token]);

    // WebSocket badges: connect then subscribe, clean up subscription only
    useEffect(() => {
        if (!token) return;
        let unsub = null;
        wsService.connect(token).then(() => {
            // subscribe returns an unsubscribe function (promise resolves with fn)
            wsService.subscribe("/user/queue/match/request", (msg) => {
                try {
                    setNewRequests((n) => n + 1);
                } catch {}
            }).then((u) => {
                // some versions return immediate fn or a Promise resolved above;
                if (typeof u === "function") {
                    unsub = u;
                }
            }).catch(() => {
                // for older environment that returns function directly (defensive)
            });
        }).catch((err) => {
            console.warn("Navbar WS connect failed", err);
        });

        return () => {
            if (typeof unsub === "function") unsub();
        };
    }, [token]);

    const handleLogout = () => {
        // explicitly disconnect socket when the user logs out
        wsService.disconnect();
        localStorage.removeItem("jwt");
        window.location.href = "/login";
    };

    const isAdmin = role === "ADMIN";

    return (
        <header className="bg-white/60 backdrop-blur-sm sticky top-0 z-40 shadow-sm">
            <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <Link to="/" className="font-bold text-xl text-slate-800">
                        CampusTrack
                    </Link>
                    <span className="text-sm text-slate-500 hidden sm:inline">
            Lost &amp; Found
          </span>
                </div>

                {/* Desktop nav */}
                <nav className="hidden md:flex items-center gap-4 text-sm">
                    <NavLink to="/" className="text-slate-600">Home</NavLink>
                    <NavLink to="/posts" className="text-slate-600">All Reports</NavLink>

                    {token && !isAdmin && (
                        <>
                            <NavLink to="/report/lost" className="text-slate-600">Report Lost</NavLink>
                            <NavLink to="/report/found" className="text-slate-600">Report Found</NavLink>
                            <NavLink to="/matches" className="text-slate-600">Matches</NavLink>
                            <div className="relative">
                                <NavLink to="/requests" className="text-slate-600">Requests</NavLink>
                                {newRequests > 0 && (
                                    <span className="absolute -top-2 -right-3 bg-red-500 text-white text-xs px-1.5 py-0.5 rounded-full">
                    {newRequests}
                  </span>
                                )}
                            </div>
                        </>
                    )}

                    {token && isAdmin && <NavLink to="/admin" className="text-slate-600">Admin</NavLink>}
                </nav>

                <div className="flex items-center gap-2">
                    {token ? (
                        <>
                            <Link
                                to="/profile"
                                className="w-8 h-8 rounded-full bg-slate-800 text-white flex items-center justify-center text-xs font-semibold"
                                title="Profile"
                            >
                                {role === "ADMIN" ? "A" : "U"}
                            </Link>
                            <button onClick={handleLogout} className="px-3 py-1 rounded-md bg-slate-800 text-white text-sm">
                                Logout
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="px-3 py-1 rounded-md bg-white border text-sm">Login</Link>
                            <Link to="/register" className="px-3 py-1 rounded-md bg-sky-500 text-white text-sm">Register</Link>
                        </>
                    )}

                    <button
                        onClick={() => {
                            setOpen((v) => !v);
                            onOpenMobile && onOpenMobile();
                        }}
                        className="md:hidden p-2 rounded-md hover:bg-slate-100"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor">
                            <path strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
                                  d={open ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16M4 18h16"} />
                        </svg>
                    </button>
                </div>
            </div>
        </header>
    );
}
