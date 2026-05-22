import { Navigate, useLocation } from 'react-router-dom'
import { isAdminRole, useAuth } from '../../context/AuthContext'

export function ProtectedRoute({ children, adminOnly = false }) {
  const { user, loading } = useAuth()
  const location = useLocation()
  if (loading) return <div className="min-h-screen flex items-center justify-center"><Spinner /></div>
  if (!user) return <Navigate to="/login" replace state={{ redirectTo: `${location.pathname}${location.search}` }} />
  if (adminOnly && !isAdminRole(user.role)) return <Navigate to="/" replace />
  return children
}

export function Spinner({ size = 'md' }) {
  const s = size === 'sm' ? 'w-5 h-5' : size === 'lg' ? 'w-12 h-12' : 'w-8 h-8'
  return (
    <div className={`${s} border border-[#D84E55]/30 border-t-[#D84E55] rounded-full animate-spin`} />
  )
}
