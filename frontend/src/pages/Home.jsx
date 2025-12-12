import React from "react";
import { Link } from "react-router-dom";

export default function Home() {
    return (
        <div className="max-w-6xl mx-auto p-6 grid grid-cols-1 md:grid-cols-2 gap-8">
            <div>
                <h1 className="text-4xl font-extrabold mb-3">CampusTrack — Lost & Found made simple.</h1>
                <p className="text-slate-600 mb-6">Report lost or found items quickly. Our matching engine connects posts by tags, category, and location.</p>
                <div className="flex gap-3">
                    <Link to="/report/lost" className="px-4 py-2 rounded bg-red-600 text-white">Report Lost</Link>
                    <Link to="/report/found" className="px-4 py-2 rounded bg-green-600 text-white">Report Found</Link>
                </div>
                <div className="mt-6">
                    <Link to="/posts" className="text-sky-600">View all reports</Link>
                </div>
            </div>
            <div className="rounded-xl overflow-hidden">
                <img src="https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=1200&auto=format&fit=crop" alt="Campus" className="w-full h-full object-cover" />
            </div>
        </div>
    );
}
