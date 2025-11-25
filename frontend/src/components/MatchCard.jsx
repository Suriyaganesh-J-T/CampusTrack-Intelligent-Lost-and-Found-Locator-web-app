import React from "react";
import useAuth from "../hooks/useAuth"; // Corrected path assumption

/**
 * Displays a potential match between a lost and found item, allowing the user
 * to send a connection request.
 * * @param {object} match - The match object containing lostPost, foundPost, lostUser, foundUser, and matchScore.
 * @param {function} onSendRequest - Handler function for sending a chat request.
 * @param {number} currentPostId - The ID of the post for which the matches are being displayed.
 */
export default function MatchCard({ match, onSendRequest, currentPostId }) {
    const { userId } = useAuth();

    // Determine if the current user owns the LOST item in this match.
    // This is required to determine which party (Lost or Found) the card represents.
    const isUserLostOwner = match.lostPost.id === parseInt(currentPostId);

    // The post owned by the OTHER user (the match)
    const matchedPost = isUserLostOwner ? match.foundPost : match.lostPost;
    const matchedUser = isUserLostOwner ? match.foundUser : match.lostUser;

    const actionText = isUserLostOwner
        ? "Contact Finder"
        : "Contact Owner"; // If viewing matches for a Found Post, the match is a Lost Post (Owner)

    const typeLabel = isUserLostOwner ? "Found Item" : "Lost Item";
    const typeColor = isUserLostOwner ? "text-green-600" : "text-red-600";
    const isRequestSent = match.status === 'REQUEST_SENT' || match.status === 'ACCEPTED'; // Assuming backend handles match status

    return (
        <div className="bg-white border border-slate-200 p-4 rounded-xl shadow-md flex flex-col sm:flex-row justify-between items-start sm:items-center transition duration-300 hover:shadow-lg">
            <div className="mb-3 sm:mb-0">
                <div className="flex items-center gap-2">
                    <span className={`text-xs font-bold uppercase tracking-wider ${typeColor}`}>
                        {typeLabel}
                    </span>
                    <span className="text-xs text-slate-500">
                        (Match Score: **{(match.matchScore * 100).toFixed(0)}%**)
                    </span>
                </div>

                <h2 className="text-xl font-bold text-slate-800 mt-1">
                    {matchedPost.itemName}
                </h2>

                <p className="text-sm text-slate-600">
                    Location: {matchedPost.place || 'Not specified'}
                </p>
                <p className="text-xs text-slate-500 mt-1">
                    Posted by: {matchedUser?.name || 'Anonymous User'}
                </p>
            </div>

            <div className="flex-shrink-0">
                <button
                    onClick={onSendRequest}
                    disabled={isRequestSent}
                    className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-white font-semibold px-4 py-2 rounded-full transition duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed shadow-md"
                >
                    {isRequestSent ? "Request Pending" : actionText}
                </button>
            </div>
        </div>
    );
}