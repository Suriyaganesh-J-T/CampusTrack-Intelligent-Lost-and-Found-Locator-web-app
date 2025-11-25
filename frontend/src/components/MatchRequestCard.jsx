// File: src/components/MatchRequestCard.jsx
import React from "react";

export default function MatchRequestCard({ request, onAccept, onDecline }) {
    const userPost = request.lostUser.id === parseInt(localStorage.getItem("userId"))
        ? request.lostPost
        : request.foundPost;

    const sender = request.lostUser.id === parseInt(localStorage.getItem("userId"))
        ? request.foundUser
        : request.lostUser;

    return (
        <div className="request-card border p-3 rounded mb-2 flex justify-between items-center">
            <div>
                <h2 className="font-semibold">{userPost.itemName}</h2>
                <p>From: {sender.name}</p>
                <p>Score: {(request.matchScore * 100).toFixed(1)}%</p>
                <p>Status: {request.status}</p>
            </div>
            <div className="flex gap-2">
                <button
                    onClick={onAccept}
                    className="bg-green-500 text-white px-3 py-1 rounded"
                >
                    Accept
                </button>
                <button
                    onClick={onDecline}
                    className="bg-red-500 text-white px-3 py-1 rounded"
                >
                    Decline
                </button>
            </div>
        </div>
    );
}
