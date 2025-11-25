import React, { useState } from "react";
import { Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";

import NavBar from "./components/NavBar"; // Path adjusted
import MobileMenu from "./components/MobileMenu"; // Path adjusted

// pages
import Home from "./pages/Home"; // Path adjusted
import PostList from "./pages/PostList"; // Path adjusted
import LostForm from "./pages/LostForm"; // Path adjusted
import FoundForm from "./pages/FoundForm"; // Path adjusted
import Register from "./pages/Register"; // Path adjusted
import Login from "./pages/Login"; // Path adjusted
import ProtectedRoute from "./components/ProtectedRoute"; // Path adjusted

import MatchesPage from "./pages/MatchesPage"; // Path adjusted
import ChatPage from "./pages/ChatPage"; // Path adjusted
import MatchRequestsPage from "./pages/MatchRequestsPage"; // Path adjusted
import Dashboard from "./pages/Dashboard"; // Path adjusted

export default function App() {
    const [mobileOpen, setMobileOpen] = useState(false);

    // example userId and roomId (Dashboard component should ideally use useAuth)
    // NOTE: This should eventually be replaced by the data from the useAuth hook.
    const userId = 5;
    const roomId = 1;

    return (
        <>
            <Toaster position="top-right" />

            <NavBar onOpenMobile={() => setMobileOpen((v) => !v)} />
            <MobileMenu open={mobileOpen} onClose={() => setMobileOpen(false)} />

            <main className="pt-4">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/posts" element={<PostList />} />

                    {/* The route for the functional requests page (MatchRequestsPage) */}
                    <Route
                        path="/requests"
                        element={
                            <ProtectedRoute>
                                <MatchRequestsPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/report/lost"
                        element={
                            <ProtectedRoute>
                                <LostForm />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/report/found"
                        element={
                            <ProtectedRoute>
                                <FoundForm />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/matches"
                        element={
                            <ProtectedRoute>
                                <MatchesPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/chat/:roomId"
                        element={
                            <ProtectedRoute>
                                <ChatPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<Login />} />

                    {/* Dashboard route with userId and roomId */}
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                {/* Note: Dashboard should be updated to use useAuth */}
                                <Dashboard userId={userId} roomId={roomId} />
                            </ProtectedRoute>
                        }
                    />
                </Routes>
            </main>
        </>
    );
}