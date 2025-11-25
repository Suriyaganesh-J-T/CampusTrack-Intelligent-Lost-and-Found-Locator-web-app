import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import wsService from "../services/wsService";
import toast from "react-hot-toast";

export default function ChatPage() {
    const { roomId } = useParams();
    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const listRef = useRef();

    const token = localStorage.getItem("jwt");

    // Decode JWT to get userId
    const parseJwt = (token) => {
        try {
            return JSON.parse(atob(token.split(".")[1]));
        } catch {
            return null;
        }
    };
    const decoded = parseJwt(token);
    const userId = decoded?.userId;

    useEffect(() => {
        if (!token || !userId || !roomId) {
            toast.error("User not authenticated");
            return;
        }

        // 1️⃣ Load message history
        axios.get(`http://localhost:8080/api/chat/room/${roomId}/messages`, {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => setMessages(Array.isArray(res.data) ? res.data : []))
            .catch(err => {
                console.error(err);
                toast.error("Failed to load chat history");
            });

        // 2️⃣ Connect WebSocket and subscribe
        wsService.connect(() => {
            wsService.subscribe(`/topic/chat/${roomId}`, (msg) => {
                setMessages(prev => [...prev, msg]);
            });

            wsService.subscribe(`/topic/chat/${roomId}/meta`, (m) => {
                console.log("Chat meta:", m);
            });
        });

        // Disconnect on unmount
        return () => wsService.disconnect();
    }, [roomId, token, userId]);

    // Scroll to bottom on new message
    useEffect(() => {
        if (listRef.current) {
            listRef.current.scrollTop = listRef.current.scrollHeight;
        }
    }, [messages]);

    const sendMessage = () => {
        if (!text.trim()) return;

        const payload = { senderId: userId, content: text.trim() };
        wsService.send(`/app/chat/${roomId}`, payload);
        setText("");
    };

    return (
        <div className="max-w-3xl mx-auto p-4">
            <header className="mb-4">
                <h2 className="text-xl font-semibold">Chat Room #{roomId}</h2>
            </header>

            <div ref={listRef} className="bg-white border rounded p-4 h-96 overflow-auto mb-4">
                {messages.length === 0 ? (
                    <p className="text-sm text-slate-500">No messages yet</p>
                ) : (
                    messages.map((m, idx) => (
                        <div key={m.id || idx} className={`mb-3 flex ${m.sender?.id === userId ? 'justify-end' : 'justify-start'}`}>
                            <div className={`${m.sender?.id === userId ? 'bg-sky-500 text-white' : 'bg-gray-100 text-slate-800'} p-3 rounded-lg max-w-[70%]`}>
                                <div className="text-xs text-slate-500 mb-1">{m.sender?.name || 'User'}</div>
                                <div>{m.content}</div>
                                <div className="text-[10px] text-slate-400 mt-1">{new Date(m.createdAt).toLocaleString()}</div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            <div className="flex gap-2">
                <input
                    value={text}
                    onChange={e => setText(e.target.value)}
                    onKeyDown={e => { if (e.key === "Enter") sendMessage(); }}
                    className="flex-1 border rounded p-2"
                    placeholder="Type a message..."
                />
                <button onClick={sendMessage} className="px-4 py-2 rounded bg-sky-600 text-white">Send</button>
            </div>
        </div>
    );
}
