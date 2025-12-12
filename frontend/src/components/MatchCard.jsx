import React from 'react';

export default function MatchCard({
                                      matchRecord,
                                      myPost,
                                      onSendRequest,
                                      onApproveMatch,
                                      onDeclineMatch,
                                      onOpenChat
                                  }) {
    if (!matchRecord || !myPost) return null;

    const {
        id,
        matchScore,
        status,
        displayStatus,
        chatRoomId,
        chatId,
        chatRequestId,
        lostPostId,
        lostPostName,
        lostPostType,
        lostPostPlace,
        foundPostId,
        foundPostName,
        foundPostType,
        foundPostPlace,
        lostUserId,
        lostUserName,
        foundUserId,
        foundUserName
    } = matchRecord;

    const isMyPostLost = String(myPost.id) === String(lostPostId);
    const otherPostName = isMyPostLost ? foundPostName : lostPostName;
    const otherPostType = isMyPostLost ? foundPostType : lostPostType;
    const otherPostPlace = isMyPostLost ? foundPostPlace : lostPostPlace;
    const receiverUserId = isMyPostLost ? foundUserId : lostUserId;
    const receiverUserName = isMyPostLost ? foundUserName : lostUserName;

    const effectiveStatus = displayStatus || status;
    const chatRoom = chatRoomId || chatId;
    const chatAvailable = effectiveStatus === "APPROVED" && chatRoom;

    const handleSend = () => {
        if (!receiverUserId || receiverUserId === myPost.user?.userId) return;
        onSendRequest(id, null, receiverUserId);
    };

    const handleApprove = () => {
        if (!chatRequestId) return;
        onApproveMatch(chatRequestId);
    };

    const handleDecline = () => {
        if (!chatRequestId) return;
        onDeclineMatch(chatRequestId);
    };

    const openChat = () => {
        if (!chatRoom) return;
        if (onOpenChat) onOpenChat(chatRoom);
    };


    return (
        <div className="bg-white p-5 md:p-6 rounded-xl shadow-lg border-2 border-slate-100 transition duration-300 hover:shadow-xl">
            <div className="flex justify-between items-start mb-4 border-b pb-3">
                <h2 className={`text-2xl font-bold ${otherPostType === 'LOST' ? 'text-blue-700' : 'text-green-700'}`}>
                    {otherPostType === 'LOST' ? 'Potential Lost Match' : 'Potential Found Match'}
                </h2>
                <span className="px-3 py-1 text-sm font-semibold rounded-full bg-blue-100 text-blue-800">
                    {otherPostType}
                </span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-slate-600">
                <div>
                    <p className="font-semibold text-sm text-slate-500 mb-1">
                        Matched Item (Owner: {receiverUserName || 'Unknown'})
                    </p>
                    <p className="text-lg font-medium text-slate-800">{otherPostName}</p>
                    <p className="mt-2 text-sm">
                        <span className="font-semibold">Location:</span> {otherPostPlace || 'N/A'}
                    </p>
                </div>

                <div>
                    <p className="font-semibold text-sm text-slate-500 mb-1">Your Item</p>
                    <p className="text-lg font-medium text-slate-800">{myPost.itemName}</p>
                    <p className="mt-2 text-sm">
                        <span className="font-semibold">Reported As:</span> {myPost.type}
                    </p>
                    <p className="text-sm">
                        <span className="font-semibold">Your Post ID:</span> {myPost.id}
                    </p>
                </div>
            </div>

            <div className="mt-6 pt-4 border-t flex justify-end space-x-3">
                {effectiveStatus === "REQUEST_SENT" ? (
                    <>
                        <button onClick={handleApprove} className="py-2 px-6 rounded-lg font-bold text-white bg-green-500 hover:bg-green-600">
                            Approve
                        </button>
                        <button onClick={handleDecline} className="py-2 px-6 rounded-lg font-bold text-white bg-red-500 hover:bg-red-600">
                            Decline
                        </button>
                    </>
                ) : effectiveStatus === "APPROVED" ? (
                    <button
                        onClick={openChat}
                        className="py-2 px-6 rounded-lg font-bold text-white bg-green-600 hover:bg-green-700"
                    >
                        💬 Open Chat
                    </button>
                ) : (
                    <button
                        onClick={handleSend}
                        disabled={receiverUserId === myPost.user?.userId}
                        className="py-2 px-6 rounded-lg font-bold text-white bg-blue-500 hover:bg-blue-600 disabled:bg-slate-400 disabled:cursor-not-allowed"
                    >
                        {receiverUserId === myPost.user?.userId ? "Invalid" : "Send Request"}
                    </button>
                )}
            </div>

            <p className="mt-2 text-xs text-slate-400 text-right">
                Match ID: {id} | Score: {(matchScore * 100).toFixed(1)}%
            </p>
        </div>
    );
}

