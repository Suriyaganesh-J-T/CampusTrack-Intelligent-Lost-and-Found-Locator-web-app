// File: src/pages/MatchRequestsPage.jsx

import React, { useEffect, useState } from "react";
import api from "../services/api"; // <-- Use api for interceptors
import toast from "react-hot-toast";
import MatchRequestCard from "../components/MatchRequestCard";
import useAuth from "../hooks/useAuth"; // <--- Import the new hook

export default function MatchRequestsPage() {
    const [requests, setRequests] = useState([]);
    const { userId } = useAuth(); // <--- Get userId from hook

    // Fetch pending requests
    useEffect(() => {
        if (!userId) return; // Prevent fetching if userId is null
        api.get(`/match/pending`, { params: { userId } }) // <-- Use api.get
            .then(res => {
                // ... rest of success logic ...
            })
            .catch(() => toast.error("Failed to load match requests"));
    }, [userId]);

    const handleAccept = (requestId) => {
        api.post(`/match/accept`, null, { params: { requestId } }) // <-- Use api.post
            .then(() => {
                // ... rest of success logic ...
            })
            .catch(() => toast.error("Failed to accept request"));
    };

    const handleDecline = (requestId) => {
        api.post(`/match/decline`, null, { params: { requestId } }) // <-- Use api.post
            .then(() => {
                // ... rest of success logic ...
            })
            .catch(() => toast.error("Failed to decline request"));
    };

    // ... rest of the component (HTML) ...
    return (
        <div className="match-requests-page p-4">
            <h1 className="text-xl font-semibold mb-4">Pending Match Requests</h1>
            {requests.length === 0 ? (
                <p>No pending requests</p>
            ) : (
                requests.map(request => (
                    <MatchRequestCard
                        key={request.id}
                        request={request}
                        onAccept={() => handleAccept(request.id)}
                        onDecline={() => handleDecline(request.id)}
                    />
                ))
            )}
        </div>
    );
}
