import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../services/api";
import MatchCard from "../components/MatchCard";
import wsService from "../services/wsService";
import RecoverButton from "../components/RecoverButton";

const Loader = ({ text = "Loading matches..." }) => (
    <div className="flex items-center justify-center p-8 bg-gray-50 rounded-xl shadow-inner">
        <svg
            className="animate-spin -ml-1 mr-3 h-5 w-5 text-sky-500"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
        >
            <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
            ></circle>
            <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            ></path>
        </svg>
        <span className="text-sky-600 font-medium">{text}</span>
    </div>
);

export default function MatchesPage() {
    const [matchGroups, setMatchGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userId, setUserId] = useState(null);
    const navigate = useNavigate();

    const fetchAllMatches = useCallback(async () => {
        setLoading(true);
        try {
            const userPostsRes = await api.get("/posts/my-posts");
            const userPosts = Array.isArray(userPostsRes.data) ? userPostsRes.data : [];

            if (userPosts.length === 0) {
                setMatchGroups([]);
                setUserId("Unknown (No Posts)");
                return;
            }

            const currentUserId =
                userPosts[0].user?.userId || userPosts[0].user?.id || "N/A";
            setUserId(currentUserId);

            const promises = userPosts.map(async (myPost) => {
                const matchesRes = await api.get(`/match/for-post/${myPost.id}`);
                const matchRecords = Array.isArray(matchesRes.data)
                    ? matchesRes.data.filter((m) => m.status !== "DECLINED")
                    : [];
                return { userPost: myPost, matches: matchRecords };
            });

            const list = await Promise.all(promises);
            setMatchGroups(list.filter((grp) => grp.matches.length > 0));
        } catch (err) {
            console.error("Match fetch failed:", err);
            toast.error("Failed to load matches");
        } finally {
            setLoading(false);
        }
    }, []);

    // WebSocket auto-refresh for match changes
    useEffect(() => {
        const token = localStorage.getItem("jwt");
        if (!token) {
            fetchAllMatches();
            return;
        }

        let unsubNew = null;
        let unsubReq = null;

        wsService.connect(token)
            .then(() => wsService.subscribe("/user/queue/match/new", fetchAllMatches))
            .then((u) => (unsubNew = u))
            .catch(() => {});

        wsService.connect(token)
            .then(() => wsService.subscribe("/user/queue/match/request", fetchAllMatches))
            .then((u) => (unsubReq = u))
            .catch(() => {});

        fetchAllMatches();

        return () => {
            if (typeof unsubNew === "function") unsubNew();
            if (typeof unsubReq === "function") unsubReq();
        };
    }, [fetchAllMatches]);

    if (loading) return <Loader />;

    return (
        <div className="max-w-5xl mx-auto p-4 sm:p-6 font-sans">
            <h1 className="text-4xl font-extrabold text-slate-900 mb-2">
                Matched Items Overview
            </h1>

            <p className="text-slate-500 mb-8 pb-3 border-b">
                Viewing matches for{" "}
                <strong>Your User ID: {userId || "N/A"}</strong>
            </p>

            {matchGroups.length === 0 && (
                <div className="bg-blue-100 text-blue-700 border-l-4 border-blue-500 p-4 mb-6 rounded-lg shadow-md">
                    <p className="font-bold text-xl mb-1">No Active Posts Yet</p>
                    <p>Create Lost / Found posts to see matching suggestions.</p>
                </div>
            )}

            <div className="space-y-10">
                {matchGroups.map((group) => (
                    <div
                        key={group.userPost.id}
                        className="bg-slate-50 p-6 rounded-2xl shadow-inner border border-slate-200"
                    >
                        <div className="mb-6 border-b pb-3 flex items-center justify-between">
                            <div>
                                <h2 className="text-2xl font-bold text-slate-800">
                                    Matches for: {group.userPost.itemName}
                                </h2>
                                <span
                                    className={`text-sm font-medium px-2 py-0.5 rounded-full ${
                                        group.userPost.type === "LOST"
                                            ? "bg-red-100 text-red-700"
                                            : "bg-green-100 text-green-700"
                                    }`}
                                >
                                    {group.userPost.type} (Post ID: {group.userPost.id})
                                </span>
                            </div>
                            <span className="text-3xl font-extrabold text-sky-500">
                                {group.matches.length} matches
                            </span>
                        </div>

                        <div className="space-y-4">
                            {group.matches.map((match) => (
                                <MatchCard
                                    key={match.id}
                                    matchRecord={match}
                                    myPost={group.userPost}
                                    onSendRequest={async (matchId, _, receiverId) => {
                                        if (!receiverId || receiverId === userId) {
                                            toast.error("Cannot send request to yourself");
                                            return;
                                        }
                                        try {
                                            await api.post(`/chat-match/request/${matchId}`, null, {
                                                params: { receiverId }
                                            });

                                            toast.success("Request Sent!");
                                            fetchAllMatches();
                                        } catch {
                                            toast.error("Request failed");
                                        }
                                    }}
                                    onApproveMatch={async (requestId) => {
                                        try {
                                            const res = await api.post("/chat-match/accept", null, { params: { requestId } });

                                            const roomId = res.data?.chatId || res.data?.id;
                                            if (roomId) navigate(`/chat/${roomId}`);
                                            fetchAllMatches();
                                        } catch {
                                            toast.error("Approval failed");
                                        }
                                    }}
                                    onDeclineMatch={async (requestId) => {
                                        try {
                                            await api.post("/chat-match/decline", null, { params: { requestId } });

                                            fetchAllMatches();
                                        } catch {
                                            toast.error("Decline failed");
                                        }
                                    }}
                                    // 👇 FIXED — Chat button works now
                                    onOpenChat={(roomId) => navigate(`/chat/${roomId}`)}
                                />
                            ))}

                            {group.userPost.status !== "RECOVERED" && (
                                <RecoverButton
                                    postId={group.userPost.id}
                                    onRecovered={fetchAllMatches}
                                />
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
