import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import api from '../services/api';
import useAuth from '../hooks/useAuth';

/**
 * Modal component to display a new match and allow the sender (Founder)
 * to send a connection request to the potential owner (Loser).
 * * @param {object} match - The match object containing lostPost, foundPost, etc.
 * @param {number} senderPostId - The ID of the post that triggered the match (the Founder's post).
 * @param {function} onClose - Function to close the modal.
 */
export default function MatchFoundModal({ match, senderPostId, onClose }) {
    const { userId } = useAuth();
    const navigate = useNavigate();
    const [isSending, setIsSending] = useState(false);

    if (!match) return null;

    // Determine if the current user is the owner of the Lost or Found post in the match
    // NOTE: This logic assumes that if the senderPostId matches the lostPost.id, the current user is the Loser.
    // However, this modal is designed to be triggered by the Founder (the person submitting the Found post).
    // The check below ensures we identify the OTHER post and user for display purposes.
    const isCurrentUserLostOwner = match.lostPost.id === parseInt(senderPostId); // Check if the post that triggered the match is the LOST post. (Should be false for a FoundForm trigger)

    // The match object typically contains both lost and found posts.
    // We use the other post's details for the modal summary.
    // If the triggering post (senderPostId) is the found one, the "other" post is the lost one.
    const otherPost = isCurrentUserLostOwner ? match.foundPost : match.lostPost;
    const otherUser = isCurrentUserLostOwner ? match.foundUser : match.lostUser;

    const lostItemName = match.lostPost.itemCategory;
    const foundItemName = match.foundPost.itemCategory;

    const sendRequest = async () => {
        if (!userId || isSending) return;

        setIsSending(true);

        // Determine the ID of the user who needs to receive the request (the other party)
        // If the current user's post (senderPostId) is the FOUND post, the receiver is the LOSER (lostUser).
        const receiverId = isCurrentUserLostOwner ? match.foundUser.id : match.lostUser.id;

        try {
            await api.post(`/match/request`, null, {
                params: {
                    matchId: match.id,
                    senderId: userId,
                    receiverId: receiverId,
                },
            });

            toast.success("Connection request sent! You'll be notified when the owner accepts.");
            onClose();
            // Redirect to the dashboard or post list
            navigate('/dashboard');
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Failed to send request.";
            toast.error(errorMessage);
            setIsSending(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full p-6 text-center transform transition-all scale-100">
                <div className="text-6xl mb-4 animate-bounce text-red-500">
                    💖
                </div>
                <h2 className="text-3xl font-extrabold text-red-600 mb-2">
                    IT'S A MATCH!
                </h2>
                <p className="text-lg text-slate-700 mb-4">
                    Your reported item matches a post by **{otherUser?.name || 'an anonymous user'}**!
                </p>

                <div className="bg-red-50 p-4 rounded-xl border border-red-200 mb-6">
                    <p className="font-semibold text-slate-800">
                        Item: {lostItemName} (Lost) vs {foundItemName} (Found)
                    </p>
                    <p className="text-sm text-slate-600">
                        Location: {otherPost.locationDescription || 'N/A'}
                    </p>
                </div>

                <p className="text-sm text-slate-600 mb-6">
                    To start a private conversation, you must send a request. The owner of the **Lost** item must accept your request before chatting is enabled.
                </p>

                <div className="flex justify-center gap-4">
                    <button
                        onClick={sendRequest}
                        disabled={isSending}
                        className="flex items-center justify-center bg-red-600 hover:bg-red-700 text-white font-bold py-3 px-6 rounded-full transition duration-300 shadow-lg disabled:opacity-50"
                    >
                        {isSending ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                                Sending Request...
                            </>
                        ) : (
                            "Send Connection Request"
                        )}
                    </button>
                    <button
                        onClick={onClose}
                        disabled={isSending}
                        className="bg-gray-200 hover:bg-gray-300 text-slate-700 font-medium py-3 px-6 rounded-full transition duration-300 disabled:opacity-50"
                    >
                        Maybe Later
                    </button>
                </div>
            </div>
        </div>
    );
}