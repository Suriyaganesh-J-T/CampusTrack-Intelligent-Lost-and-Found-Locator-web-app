import React from "react";

export default function RequestCard({ request, onAccept, onDecline }) {
    const isNew = !!request._isNew;
    return (
        <div className={`request-card border p-3 rounded mb-2 flex justify-between items-center transition-shadow ${isNew ? "shadow-lg ring-2 ring-sky-300" : ""}`}>
            <div>
                <h2 className="font-semibold">{request.match?.foundPost?.itemName || request.match?.foundPost?.itemName || "Match"}</h2>
                <p className="text-sm">From: {request.sender?.name || "Unknown"}</p>
                <p className="text-sm">Score: {(request.match?.matchScore ?? request.matchScore ?? 0)}</p>
            </div>
            <div className="flex gap-2">
                <button onClick={onAccept} className="bg-green-500 text-white px-3 py-1 rounded">Accept</button>
                <button onClick={onDecline} className="bg-red-500 text-white px-3 py-1 rounded">Decline</button>
            </div>
        </div>
    );
}
