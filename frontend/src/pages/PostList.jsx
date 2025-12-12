import React, { useEffect, useState } from "react";
import api from "../services/api";

const CATEGORIES = ["All", "Electronics", "Documents", "Clothing", "Accessories", "Keys", "Other"];

export default function PostList() {
    const [posts, setPosts] = useState([]);
    const [type, setType] = useState("");
    const [category, setCategory] = useState("All");
    const [q, setQ] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => { load(); }, [type, category, q]);

    const load = async () => {
        try {
            setLoading(true);
            const params = {};
            if (type) params.type = type;
            if (category && category !== "All") params.category = category;
            if (q) params.q = q;
            const res = await api.get("/posts", { params });
            setPosts(Array.isArray(res.data) ? res.data : []);
        } catch (err) {
            console.error(err);
            alert("Failed to load posts");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-5xl mx-auto p-4">
            <div className="flex gap-3 items-center mb-4">
                <select value={type} onChange={(e) => setType(e.target.value)} className="border rounded px-2 py-1">
                    <option value="">All Types</option>
                    <option value="LOST">Lost</option>
                    <option value="FOUND">Found</option>
                </select>
                <select value={category} onChange={(e) => setCategory(e.target.value)} className="border rounded px-2 py-1">
                    {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
                <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search item, model..." className="flex-1 border rounded px-3 py-2" />
                <button onClick={() => { setType(""); setCategory("All"); setQ(""); }} className="px-3 py-1 border rounded">Reset</button>
            </div>

            {loading ? <div>Loading...</div> : (
                <div className="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                    {posts.map((p) => (
                        <div key={p.id} className="bg-white p-4 rounded shadow">
                            <div className="font-bold text-lg">{p.itemName} <span className="text-sm text-gray-500">({p.type})</span></div>
                            <div className="text-sm text-gray-600">Category: {p.category || "-"}</div>
                            <div className="text-sm">Place: {p.place || "-"}</div>
                            <div className="text-sm">Date: {p.dateReported || "-"}</div>
                            {p.tags && <div className="mt-2">Tags: {p.tags.split(",").map((t) => <span key={t} className="inline-block mr-1 px-2 py-1 text-xs bg-gray-100 rounded">{t.trim()}</span>)}</div>}
                            {p.imageUrl && <img src={`http://localhost:8080${p.imageUrl}`} alt="img" className="mt-3 rounded max-w-full" />}
                            <div className="mt-2 text-sm">Status: {p.status}</div>
                            <div className="text-sm">Best match score: {p.lastMatchScore || "-"}</div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
