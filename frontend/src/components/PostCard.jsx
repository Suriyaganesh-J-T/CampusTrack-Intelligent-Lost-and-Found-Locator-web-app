import React from "react";

export default function PostCard({ post }) {
    return (
        <div className="bg-white p-4 rounded shadow hover:shadow-lg transition-shadow">
            {/* Item name + type */}
            <div className="font-bold text-lg">
                {post.itemName}{" "}
                <span className="text-sm text-gray-500">({post.type})</span>
            </div>

            {/* Category */}
            <div className="text-sm text-gray-600">Category: {post.category || "-"}</div>

            {/* Place */}
            <div className="text-sm">Place: {post.place || "-"}</div>

            {/* Date reported */}
            <div className="text-sm">Date: {post.dateReported || "-"}</div>

            {/* Tags */}
            {post.tags && (
                <div className="mt-2">
                    Tags:{" "}
                    {post.tags.split(",").map((t) => (
                        <span
                            key={t}
                            className="inline-block mr-1 px-2 py-1 text-xs bg-gray-100 rounded"
                        >
                            {t.trim()}
                        </span>
                    ))}
                </div>
            )}

            {/* Image */}
            {post.imageUrl && (
                <img
                    src={`http://localhost:8080${post.imageUrl}`}
                    alt={post.itemName}
                    className="mt-3 rounded max-w-full"
                />
            )}

            {/* Status */}
            <div className="mt-2 text-sm">Status: {post.status}</div>

            {/* Best match score */}
            <div className="text-sm">Best match score: {post.lastMatchScore || "-"}</div>
        </div>
    );
}
