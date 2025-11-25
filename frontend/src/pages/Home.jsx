import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";
import Loader from "../components/Loader";
import PostCard from "../components/PostCard";

export default function Home() {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    const load = async () => {
        try {
            const res = await api.get("/posts");
            setPosts(res.data.slice().reverse().slice(0,6));
        } catch (err) {
            console.error("Home load err", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(); }, []);

    return (
        <div className="min-h-screen">
            {/* Hero */}
            <section className="bg-gradient-to-r from-sky-500 to-indigo-600 text-white py-16">
                <div className="max-w-6xl mx-auto px-4 flex flex-col md:flex-row items-center gap-8">
                    <div className="flex-1">
                        <h1 className="text-4xl md:text-5xl font-bold mb-3">CampusTrack — Lost & Found made simple</h1>
                        <p className="text-lg mb-6 max-w-xl">Quickly report lost or found items on campus and match them using our smart matching engine.</p>
                        <div className="flex gap-3">
                            <Link to="/report/lost" className="px-5 py-3 rounded-lg bg-white text-sky-600 font-semibold">Report Lost</Link>
                            <Link to="/report/found" className="px-5 py-3 rounded-lg bg-white text-sky-600 font-semibold">Report Found</Link>
                        </div>
                    </div>

                    <div className="flex-1 hidden md:block">
                        <div className="bg-white/10 p-6 rounded-2xl shadow-inner">
                            <img src="/placeholder.png" alt="hero" className="w-full h-56 object-cover rounded-lg" />
                        </div>
                    </div>
                </div>
            </section>

            {/* Latest posts */}
            <section className="py-10">
                <div className="max-w-6xl mx-auto px-4">
                    <div className="flex items-center justify-between mb-6">
                        <h2 className="text-2xl font-semibold">Latest reports</h2>
                        <Link to="/posts" className="text-sky-500">View all</Link>
                    </div>

                    {loading ? (
                        <Loader />
                    ) : posts.length === 0 ? (
                        <div className="text-center text-slate-500">No reports yet.</div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                            {posts.map(p => <PostCard post={p} key={p.id} />)}
                        </div>
                    )}
                </div>
            </section>
        </div>
    );
}
