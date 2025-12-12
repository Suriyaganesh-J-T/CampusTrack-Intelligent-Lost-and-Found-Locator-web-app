import React, { useState } from "react";
import { Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";

import Navbar from "./components/Navbar";
import MobileMenu from "./components/MobileMenu";
import ProtectedRoute from "./components/ProtectedRoute";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import PostList from "./pages/PostList";
import LostForm from "./pages/LostForm";
import FoundForm from "./pages/FoundForm";
import MatchesPage from "./pages/MatchesPage";
import MatchRequestsPage from "./pages/MatchRequestsPage";
import ChatPage from "./pages/ChatPage";
import AdminDashboard from "./pages/AdminDashboard";
import ChatSidebar from "./components/ChatSidebar.jsx";
import Profile from "./pages/Profile.jsx";

export default function App() {
    const [mobileOpen, setMobileOpen] = useState(false);

    return (
        <>
            <Toaster position="top-right" />
            <Navbar onOpenMobile={() => setMobileOpen((v) => !v)} />
            <MobileMenu open={mobileOpen} onClose={() => setMobileOpen(false)} />

            <main className="pt-4">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/posts" element={<PostList />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    <Route path="/report/lost" element={<ProtectedRoute><LostForm /></ProtectedRoute>} />
                    <Route path="/report/found" element={<ProtectedRoute><FoundForm /></ProtectedRoute>} />

                    <Route path="/matches" element={<ProtectedRoute><MatchesPage /></ProtectedRoute>} />
                    <Route path="/requests" element={<ProtectedRoute><MatchRequestsPage /></ProtectedRoute>} />
                    <Route path="/chat/:roomId" element={<ChatPage />} />

                    <Route path="/chats" element={<ChatSidebar />} />



                    <Route path="/admin" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
                    <Route
                        path="/profile"
                        element={<ProtectedRoute><Profile /></ProtectedRoute>}
                    />


                </Routes>
            </main>
        </>
    );
}
