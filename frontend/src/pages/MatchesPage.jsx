import React, { useEffect, useState } from "react";
import api from "../services/api";
import toast from "react-hot-toast";
import MatchCard from "../components/MatchCard";

export default function MatchesPage() {
    const [matches, setMatches] = useState([]);

    const postId = localStorage.getItem("userPostId") || 1;
    const userId = localStorage.getItem("userId") || 1;

    useEffect(() => {
        api.get(`/match/for-post/${postId}`)
            .then(res => {
                if (Array.isArray(res.data)) {
                    setMatches(res.data);
                } else {
                    setMatches([]);
                    toast.error("Invalid matches response");
                }
            })
            .catch(err => {
                console.log(err);
                toast.error("Failed to load matches");
            });
    }, [postId]);

    const sendRequest = (matchRecord) => {
        const receiverId =
            matchRecord.lostPost.id === parseInt(postId)
                ? matchRecord.foundUser.id
                : matchRecord.lostUser.id;

        api.post(`/match/request`, null, {
            params: {
                matchId: matchRecord.id,
                senderId: userId,
                receiverId: receiverId,
            },
        })
            .then(() => {
                toast.success("Match request sent!");
                setMatches(prev => prev.filter(m => m.id !== matchRecord.id));
            })
            .catch(() => toast.error("Failed to send request"));
    };

    return (
        <div className="matches-page p-4">
            <h1 className="text-xl font-semibold mb-4">Suggested Matches</h1>
            {matches.length === 0 ? (
                <p>No matches available</p>
            ) : (
                matches.map(match => (
                    <MatchCard
                        key={match.id}
                        match={match}
                        onSendRequest={() => sendRequest(match)}
                    />
                ))
            )}
        </div>
    );
}
