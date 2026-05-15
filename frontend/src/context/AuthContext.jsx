import { createContext, useContext, useState, useEffect } from 'react'
import axios from 'axios'
import toast from 'react-hot-toast'

const AuthContext = createContext(null)

const TOKEN_KEY = 'bmr_token'
const USER_KEY  = 'bmr_user'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

function normalizeRole(rawRole) {
  if (!rawRole) return 'USER'
  const role = String(rawRole).trim().toUpperCase()
  if (role === 'ROLE_ADMIN' || role === 'ADMIN') return 'ADMIN'
  if (role === 'ROLE_USER' || role === 'PASSENGER' || role === 'CUSTOMER') return 'USER'
  return role.replace(/^ROLE_/, '')
}

function isAdminRole(role) {
  return normalizeRole(role) === 'ADMIN'
}

function buildUser(data, fallback = {}) {
  const source = data?.user ?? data?.account ?? data?.profile ?? data ?? {}
  const role =
    source?.role ??
    source?.userRole ??
    source?.authority ??
    source?.authorities?.[0]?.authority ??
    source?.authorities?.[0] ??
    data?.role ??
    data?.userRole ??
    data?.authority ??
    data?.authorities?.[0]?.authority ??
    data?.authorities?.[0] ??
    fallback.role

  return {
    id: source?.userId ?? source?.id ?? data?.userId ?? data?.id ?? fallback.id,
    name: source?.name ?? data?.name ?? fallback.name ?? source?.email ?? data?.email ?? fallback.email ?? 'User',
    email: source?.email ?? data?.email ?? fallback.email,
    role: normalizeRole(role),
  }
}

function unwrapResponse(resData) {
  return resData?.data ?? resData?.result ?? resData
}

function findToken(data) {
  if (typeof data === 'string') return data
  return (
    data?.accessToken ??
    data?.token ??
    data?.jwt ??
    data?.bearerToken ??
    data?.auth?.accessToken ??
    data?.auth?.token ??
    data?.user?.accessToken ??
    data?.user?.token
  )
}

function getUserPayload(data) {
  if (typeof data === 'string') return {}
  return data
}

function readSavedUser() {
  try {
    const saved = localStorage.getItem(USER_KEY)
    return saved ? JSON.parse(saved) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const restoreSession = async () => {
      const token = localStorage.getItem(TOKEN_KEY)
      const saved = readSavedUser()

      if (token && saved) {
        try {
          const normalized = buildUser(saved)
          setUser(normalized)
          localStorage.setItem(USER_KEY, JSON.stringify(normalized))

          const res = await api.get('/auth/me', {
            headers: { Authorization: `Bearer ${token}` }
          })
          const fresh = res.data?.data
          if (fresh) {
            const updated = buildUser(fresh, normalized)
            setUser(updated)
            localStorage.setItem(USER_KEY, JSON.stringify(updated))
          }
        } catch {
          localStorage.removeItem(TOKEN_KEY)
          localStorage.removeItem(USER_KEY)
          setUser(null)
        }
      }

      setLoading(false)
    }

    restoreSession()
  }, [])

  const loginWithEndpoint = async (endpoint, email, password) => {
    const res = await api.post(endpoint, { email, password })
    const data = unwrapResponse(res.data)
    if (!data) throw new Error('Invalid response from server')

    const token = findToken(data)
    if (!token) throw new Error('Login response did not include an access token')

    const userObj = buildUser(getUserPayload(data), { email })
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(userObj))
    setUser(userObj)
    toast.success(`Welcome back, ${userObj.name}!`)
    return userObj
  }

  const login = async (email, password) => {
    return loginWithEndpoint('/auth/login', email, password)
  }

  const adminLogin = async (email, password) => {
    return loginWithEndpoint('/auth/admin/login', email, password)
  }

  const register = async (payload) => {
    const res = await api.post('/auth/register', payload)
    const data = unwrapResponse(res.data)
    if (!data) throw new Error('Invalid response from server')

    const token = findToken(data)
    if (!token) throw new Error('Register response did not include an access token')

    const userObj = buildUser(getUserPayload(data), { name: payload.name, email: payload.email })
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(userObj))
    setUser(userObj)
    toast.success("Account created! Let's roll")
    return userObj
  }

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
    toast.success('Logged out successfully')
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, adminLogin, register, logout, isAdmin: isAdminRole(user?.role) }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
export { isAdminRole, normalizeRole }
