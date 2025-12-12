import React from "react";
import { Link } from "react-router-dom";

export default function MobileMenu({ open, onClose }) {
    return (
        <div className={`fixed inset-0 z-50 transition-all ${open ? 'pointer-events-auto' : 'pointer-events-none'}`}>
            <div className={`absolute inset-0 bg-black/40 transition-opacity ${open ? 'opacity-100' : 'opacity-0'}`} onClick={onClose} />
            <aside className={`absolute right-0 top-0 h-full w-72 bg-white/90 backdrop-blur p-4 transform transition-transform ${open ? 'translate-x-0' : 'translate-x-full'}`}>
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold">Menu</h3>
                    <button onClick={onClose} className="p-1">✕</button>
                </div>
                <nav className="flex flex-col gap-3">
                    <Link to="/" onClick={onClose} className="text-slate-700">Home</Link>
                    <Link to="/report/lost" onClick={onClose} className="text-slate-700">Report Lost</Link>
                    <Link to="/report/found" onClick={onClose} className="text-slate-700">Report Found</Link>
                    <Link to="/posts" onClick={onClose} className="text-slate-700">All Reports</Link>
                    <Link to="/matches" onClick={onClose} className="text-slate-700">Matches</Link>
                    <Link to="/requests" onClick={onClose} className="text-slate-700">Requests</Link>
                    <Link to="/admin" onClick={onClose} className="text-slate-700">Admin</Link>
                </nav>
            </aside>
        </div>
    );
}
