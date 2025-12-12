import React from "react";

export default function MatchRequestCard({ request, onAction }) {
    if (!request) return null;
    const { id, senderName, receiverName, status, postSummary, createdAt } = request;

    return (
        <div className="bg-white p-4 rounded-xl shadow border flex items-center justify-between">
            <div>
                <div className="font-semibold">From: {senderName} → To: {receiverName}</div>
                <div className="text-sm text-slate-600">Status: {status}</div>
                {postSummary && <div className="text-sm text-slate-500 mt-1">{postSummary}</div>}
                <div className="text-xs text-slate-400">{createdAt ? new Date(createdAt).toLocaleString() : ""}</div>
            </div>
            <div className="flex gap-2">
                <button onClick={() => onAction(id, "ACCEPTED")} className="px-3 py-1 rounded bg-green-600 text-white">Accept</button>
                <button onClick={() => onAction(id, "DECLINED")} className="px-3 py-1 rounded bg-red-600 text-white">Decline</button>
            </div>
        </div>
    );
}
