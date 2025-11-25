import React, { useState, useEffect } from "react";
import { Link, NavLink } from "react-router-dom";
import { wsService } from "../services/wsService"; // note the braces


export default function NavBar({ onOpenMobile }) {
    const [open, setOpen] = useState(false);
    const [newRequests, setNewRequests] = useState(0);

    const token = localStorage.getItem("jwt");

    // Decode JWT to get userId
    const parseJwt = (token) => {
        try {
            return JSON.parse(atob(token.split(".")[1]));
        } catch {
            return null;
        }
    };
    const decoded = parseJwt(token);
    const userId = decoded?.userId;

    useEffect(() => {
        if (!userId) return;

        wsService.connect(() => {
            wsService.subscribe(`/topic/match/request/${userId}`, (msg) => {
                setNewRequests(prev => prev + 1);
            });
        });
    }, [userId]);

    return (
        <header className="bg-white/60 backdrop-blur-sm sticky top-0 z-40 shadow-sm">
            <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">

                <div className="flex items-center gap-3">
                    <Link to="/" className="font-bold text-xl text-slate-800">CampusTrack</Link>
                    <span className="text-sm text-slate-500 hidden sm:inline">Lost & Found</span>
                </div>

                <nav className="hidden md:flex items-center gap-4">
                    <NavLink to="/" className="text-slate-600">Home</NavLink>
                    <NavLink to="/report/lost" className="text-slate-600">Report Lost</NavLink>
                    <NavLink to="/report/found" className="text-slate-600">Report Found</NavLink>
                    <NavLink to="/posts" className="text-slate-600">All Reports</NavLink>

                    {token && userId && (
                        <>
                            <NavLink to="/matches" className="text-slate-600">Matches</NavLink>

                            <div className="relative">
                                <NavLink to="/requests" className="text-slate-600">Requests</NavLink>

                                {newRequests > 0 && (
                                    <span className="absolute -top-2 -right-3 bg-red-500 text-white text-xs px-1.5 py-0.5 rounded-full">
                                        {newRequests}
                                    </span>
                                )}
                            </div>

                            <NavLink to="/chat/1" className="text-slate-600">Chat</NavLink>
                        </>
                    )}
                </nav>

                <div className="flex items-center gap-2">
                    {token ? (
                        <button
                            onClick={() => {
                                localStorage.removeItem("jwt");
                                window.location.href = "/";
                            }}
                            className="px-3 py-1 rounded-md bg-slate-800 text-white text-sm"
                        >
                            Logout
                        </button>
                    ) : (
                        <>
                            <Link to="/login" className="px-3 py-1 rounded-md bg-white border text-sm">Login</Link>
                            <Link to="/register" className="px-3 py-1 rounded-md bg-sky-500 text-white text-sm">Register</Link>
                        </>
                    )}

                    <button
                        onClick={() => { setOpen(v => !v); onOpenMobile && onOpenMobile(); }}
                        className="md:hidden p-2 rounded-md hover:bg-slate-100"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor">
                            <path strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
                                  d={open ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16M4 18h16"}
                            />
                        </svg>
                    </button>
                </div>
            </div>
        </header>
    );
}
