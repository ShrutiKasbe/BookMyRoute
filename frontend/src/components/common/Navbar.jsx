import { Link, useLocation, useNavigate } from 'react-router-dom'
import { FaBars, FaBusAlt, FaSignOutAlt, FaTimes, FaUserCircle } from 'react-icons/fa'
import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
  const { pathname } = useLocation()
  const [open, setOpen] = useState(false)
  const [profileOpen, setProfileOpen] = useState(false)

  const handleLogout = () => {
    logout()
    setOpen(false)
    setProfileOpen(false)
    navigate('/login')
  }

  const navLink = (to, label, mobile = false) => (
    <Link
      to={to}
      onClick={() => mobile && setOpen(false)}
      className={`rounded-lg px-3 py-2 text-sm font-700 transition-colors ${
        pathname === to
          ? 'bg-[#d84e55]/10 text-[#d84e55]'
          : 'text-slate-600 hover:bg-slate-100 hover:text-[#172033]'
      }`}
    >
      {label}
    </Link>
  )

  return (
    <nav className="sticky top-0 z-50 border-b border-gray-200 bg-white/95 backdrop-blur">
      <div className="section-wrap flex h-16 items-center justify-between">
        <Link to="/" className="flex items-center gap-2" onClick={() => setOpen(false)}>
          <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-[#d84e55] text-white">
            <FaBusAlt />
          </span>
          <span className="text-xl font-800 tracking-tight text-[#172033]">
            BookMyRoute
          </span>
        </Link>

        <div className="hidden items-center gap-1 md:flex">
          {user ? (
            <>
              {navLink('/search', 'Search buses')}
              {!isAdmin && navLink('/my-bookings', 'My bookings')}
              {isAdmin && navLink('/admin', 'Admin')}

              <div className="relative ml-2">
                <button
                  onClick={() => setProfileOpen(!profileOpen)}
                  className="flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-700 text-[#172033] hover:border-[#d84e55]/40"
                >
                  <FaUserCircle className="text-[#d84e55]" />
                  <span>{user.name?.split(' ')[0] || 'Account'}</span>
                </button>
                {profileOpen && (
                  <div className="absolute right-0 top-12 z-50 min-w-[220px] rounded-lg border border-gray-200 bg-white py-2 shadow-lg">
                    <div className="border-b border-gray-100 px-4 py-3">
                      <p className="text-sm font-800 text-[#172033]">{user.name}</p>
                      <p className="text-xs text-slate-500">{user.email}</p>
                    </div>
                    <button
                      onClick={handleLogout}
                      className="flex w-full items-center gap-2 px-4 py-2.5 text-left text-sm font-700 text-red-600 hover:bg-red-50"
                    >
                      <FaSignOutAlt /> Logout
                    </button>
                  </div>
                )}
              </div>
            </>
          ) : (
            <>
              <Link to="/login" className="btn-outline px-4 py-2">Login</Link>
              <Link to="/register" className="btn-primary px-4 py-2">Register</Link>
            </>
          )}
        </div>

        <button
          type="button"
          className="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-200 text-[#172033] md:hidden"
          onClick={() => setOpen(!open)}
          aria-label={open ? 'Close navigation menu' : 'Open navigation menu'}
        >
          {open ? <FaTimes /> : <FaBars />}
        </button>
      </div>

      {open && (
        <div className="border-t border-gray-100 bg-white px-4 py-4 md:hidden">
          <div className="flex flex-col gap-2">
            {user ? (
              <>
                {navLink('/search', 'Search buses', true)}
                {!isAdmin && navLink('/my-bookings', 'My bookings', true)}
                {isAdmin && navLink('/admin', 'Admin', true)}
                <div className="mt-2 border-t border-gray-100 pt-3">
                  <p className="text-sm font-800 text-[#172033]">{user.name}</p>
                  <p className="mb-3 text-xs text-slate-500">{user.email}</p>
                  <button
                    onClick={handleLogout}
                    className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm font-700 text-red-600 hover:bg-red-50"
                  >
                    <FaSignOutAlt /> Logout
                  </button>
                </div>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setOpen(false)} className="btn-outline">Login</Link>
                <Link to="/register" onClick={() => setOpen(false)} className="btn-primary">Register</Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}
