import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import MatchRequestCard from "../components/MatchRequestCard";
import toast from "react-hot-toast";

const Loader = ({ text = "Loading..." }) => (
    <div className="flex items-center justify-center p-8 bg-gray-50 rounded-xl shadow-inner">
        <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-sky-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span className="text-sky-600 font-medium">{text}</span>
    </div>
);

export default function MatchRequestsPage() {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const fetchRequests = useCallback(() => {
        setLoading(true);

        api.get("/chat-match/pending")
            .then((res) => setRequests(res.data || []))
            .catch((err) => {
                console.error("[RequestsPage] Fetch Error:", err);
                toast.error("Failed to load requests.");
                setRequests([]);
            })
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        fetchRequests();
    }, [fetchRequests]);

    const handleRequestAction = (requestId, action) => {
        if (action === "ACCEPTED") {
            api.post("/chat-match/accept", null, { params: { requestId } })
                .then((res) => {
                    toast.success("Request Accepted! Redirecting to chat...");
                    const chatId = res.data?.chatId;
                    if (chatId) navigate(`/chat/${chatId}`);
                })
                .catch(() => toast.error("Failed to accept request."));
        }

        if (action === "DECLINED") {
            api.post("/chat-match/decline", null, { params: { requestId } })
                .then(() => {
                    toast.success("Request Declined.");
                    setRequests((prev) => prev.filter((r) => r.id !== requestId));
                })
                .catch(() => toast.error("Failed to decline request."));
        }
    };

    return (
        <div className="p-6 max-w-4xl mx-auto bg-slate-50 min-h-screen">
            <h1 className="text-3xl font-extrabold mb-8 text-slate-800 border-b pb-2">
                Incoming Match Requests ({requests.filter(r => r.status === "PENDING").length} Pending)
            </h1>

            {loading ? (
                <Loader text="Fetching your match requests..." />
            ) : (
                <div className="space-y-4">
                    {requests.length === 0 ? (
                        <div className="bg-white p-6 rounded-xl shadow text-center text-slate-500 border border-dashed border-slate-300">
                            No incoming match requests at this time.
                        </div>
                    ) : (
                        requests.map((req) => (
                            <MatchRequestCard
                                key={req.id}
                                request={req}
                                onAction={handleRequestAction}
                            />
                        ))
                    )}
                </div>
            )}
        </div>
    );
}
