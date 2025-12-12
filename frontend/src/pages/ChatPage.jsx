// src/pages/ChatPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";
import wsService from "../services/wsService";
import useAuth from "../hooks/useAuth";
import ChatSidebar from "../components/ChatSidebar";

export default function ChatPage() {
    const { roomId } = useParams();
    const { userId } = useAuth();

    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");

    const bottomRef = useRef(null);

    // Load past messages
    useEffect(() => {
        let cancelled = false;

        async function loadMessages() {
            try {
                const res = await api.get(`/chat/room/${roomId}/messages`);

                if (!cancelled) {
                    const msgs = (res.data || []).map((m) => ({
                        ...m,
                        localKey: `history-${m.id}` // unique history key
                    }));
                    setMessages(msgs);
                }
            } catch (err) {
                console.error("Failed to load messages", err);
            }
        }

        loadMessages();
        return () => (cancelled = true);
    }, [roomId]);

    // WS Live updates
    useEffect(() => {
        const token = localStorage.getItem("jwt");
        if (!token) return;

        let unsubscribe = null;

        wsService.connect(token)
            .then(() =>
                wsService.subscribe(`/topic/chat/${roomId}`, (msgFrame) => {
                    try {
                        const body = JSON.parse(msgFrame.body);

                        const liveMsg = {
                            ...body,
                            localKey: `live-${body.id || Date.now()}-${Math.random()}`
                        };

                        setMessages((prev) => [...prev, liveMsg]);
                    } catch (err) {
                        console.warn("WS parse issue", err);
                    }
                })
            )
            .then((u) => {
                if (typeof u === "function") unsubscribe = u;
            });

        return () => {
            if (unsubscribe) unsubscribe();
        };
    }, [roomId]);

    // Scroll to bottom on message update
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    // Send message
    const sendMessage = (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        wsService.send(`/app/chat.send/${roomId}`, {
            senderId: userId,
            content: input.trim()
        });

        setInput("");
    };

    // message bubble CSS classes
    const isMine = (m) => m.senderId === userId;

    return (
        <div className="flex h-[calc(100vh-80px)] bg-[#0d1117]">
            <ChatSidebar />

            <div className="flex-1 p-6 flex flex-col text-white">
                <h1 className="text-xl font-bold mb-4">Chat Room #{roomId}</h1>

                <div className="flex-1 overflow-y-auto bg-[#161b22] rounded-xl p-4 space-y-3 border border-gray-700">
                    {messages.map((msg) => (
                        <div
                            key={msg.id || msg.localKey}  // ← FIXED: NO DUPLICATES
                            className={
                                isMine(msg)
                                    ? "sent bg-blue-600 text-white p-2 rounded-lg self-end max-w-xs"
                                    : "received bg-gray-700 text-white p-2 rounded-lg max-w-xs"
                            }
                        >
                            {msg.content}
                        </div>
                    ))}

                    <div ref={bottomRef} />
                </div>

                <form onSubmit={sendMessage} className="mt-4 flex gap-3">
                    <input
                        className="flex-1 bg-[#21262d] border border-gray-700 rounded-lg px-3 py-2 text-sm text-white"
                        placeholder="Type a message..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                    />
                    <button className="px-4 py-2 rounded-lg bg-[#1f6feb] text-white font-semibold">
                        Send
                    </button>
                </form>
            </div>
        </div>
    );
}
