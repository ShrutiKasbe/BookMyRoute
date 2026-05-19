import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/common/ProtectedRoute'
import Navbar from './components/common/Navbar'
import Footer from './components/common/Footer'
import ChatbotWidget from './components/common/ChatbotWidget'

import HomePage           from './pages/HomePage'
import { LoginPage, RegisterPage } from './pages/AuthPages'
import SearchPage         from './pages/SearchPage'
import BookingPage        from './pages/BookingPage'
import MyBookingsPage     from './pages/MyBookingsPage'
import AdminDashboardPage from './pages/AdminDashboardPage'
import ProfilePage        from './pages/ProfilePage'

export default function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen flex flex-col">
        <Navbar />
        <main className="flex-1">
          <Routes>
            {/* Public */}
            <Route path="/"         element={<HomePage />} />
            <Route path="/login"    element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Passenger-protected */}
            <Route path="/search" element={
              <ProtectedRoute><SearchPage /></ProtectedRoute>
            } />
            <Route path="/book" element={
              <ProtectedRoute><BookingPage /></ProtectedRoute>
            } />
            <Route path="/my-bookings" element={
              <ProtectedRoute><MyBookingsPage /></ProtectedRoute>
            } />
            <Route path="/profile" element={
              <ProtectedRoute><ProfilePage /></ProtectedRoute>
            } />

            {/* Admin-only */}
            <Route path="/admin" element={
              <ProtectedRoute adminOnly><AdminDashboardPage /></ProtectedRoute>
            } />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Footer />
        <ChatbotWidget />
      </div>
    </AuthProvider>
  )
}
