import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { GoogleLogin } from '@react-oauth/google'
import { isAdminRole, useAuth } from '../context/AuthContext'
import { FaBus, FaEye, FaEyeSlash } from 'react-icons/fa'
import toast from 'react-hot-toast'

function Field({ label, type = 'text', value, onChange, placeholder, required, autoComplete }) {
  const [show, setShow] = useState(false)
  const isPassword = type === 'password'
  return (
    <div className="flex flex-col gap-1.5">
      <label className="font-display font-700 text-sm text-[#172033]">{label}</label>
      <div className="relative">
        <input
          type={isPassword ? (show ? 'text' : 'password') : type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          autoComplete={autoComplete}
          className="input-field pr-10"
        />
        {isPassword && (
          <button
            type="button"
            onClick={() => setShow(!show)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-[#D84E55]"
            aria-label={show ? 'Hide password' : 'Show password'}
          >
            {show ? <FaEyeSlash /> : <FaEye />}
          </button>
        )}
      </div>
    </div>
  )
}

function BusStripe() {
  return (
    <div className="absolute bottom-0 left-0 right-0 h-2 flex overflow-hidden rounded-b-3xl">
      {['#D84E55','#F59E0B','#059669','#DC2626','#D84E55','#F59E0B','#059669'].map((c, i) => (
        <div key={i} className="flex-1" style={{ background: c }} />
      ))}
    </div>
  )
}

function Divider() {
  return (
    <div className="flex items-center gap-3 my-1">
      <hr className="flex-1 border-gray-200" />
      <span className="text-xs text-gray-400 font-body">or continue with</span>
      <hr className="flex-1 border-gray-200" />
    </div>
  )
}

function getErrorMessage(err, fallback) {
  return (
    err.response?.data?.message ||
    err.response?.data?.error  ||
    err.message ||
    fallback
  )
}

export function LoginPage() {
  const { adminLogin, login, googleLogin } = useAuth()
  const navigate = useNavigate()
  const { state } = useLocation()
  const [form, setForm]       = useState({ email: '', password: '' })
  const [mode, setMode]       = useState('passenger')
  const [loading, setLoading] = useState(false)

  const set = (k) => (e) => setForm(f => ({ ...f, [k]: e.target.value }))
  const isAdminMode = mode === 'admin'

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const signIn = isAdminMode ? adminLogin : login
      const user   = await signIn(form.email.trim(), form.password)
      const fallbackPath = isAdminRole(user.role) ? '/admin' : '/search'
      navigate(state?.redirectTo && !isAdminRole(user.role) ? state.redirectTo : fallbackPath, { replace: true })
    } catch (err) {
      toast.error(getErrorMessage(err, 'Unable to sign in. Please check your credentials.'))
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const user = await googleLogin(credentialResponse.credential)
      navigate(state?.redirectTo || '/search', { replace: true })
    } catch (err) {
      toast.error(getErrorMessage(err, 'Google sign-in failed. Please try again.'))
    }
  }

  const handleGoogleError = () => {
    toast.error('Google sign-in was cancelled or failed.')
  }

  return (
    <div className="min-h-screen bg-[#F6F7FB] flex items-center justify-center p-4
                    bg-[radial-gradient(ellipse_at_top_right,#F59E0B/20,transparent_60%),radial-gradient(ellipse_at_bottom_left,#D84E55/15,transparent_60%)]">
      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 bg-[#172033] text-white px-5 py-2.5
                          rounded-full border border-[#D84E55] shadow-sm mb-6">
            <FaBus className="text-[#F59E0B]" />
            <span className="font-display font-800 text-lg tracking-tight">BookMyRoute</span>
          </div>
          <h1 className="text-4xl font-display text-[#172033] mb-2">Welcome back!</h1>
          <p className="text-gray-500 font-body">Sign in to continue your journey</p>
        </div>

        <div className="relative card p-8 pb-10">
          <div className="grid grid-cols-2 gap-2 mb-5">
            {[['passenger','Passenger'],['admin','Admin']].map(([key, label]) => (
              <button
                key={key}
                type="button"
                onClick={() => { setMode(key); setForm({ email: '', password: '' }) }}
                className={`px-4 py-2 rounded-xl border font-display font-700 text-sm transition-all ${
                  mode === key
                    ? 'bg-[#172033] text-white border-[#172033] shadow-sm'
                    : 'bg-white border-gray-200 text-gray-600 hover:border-[#172033]'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit} className="flex flex-col gap-5">
            <Field
              label="Email address"
              type="email"
              value={form.email}
              onChange={set('email')}
              placeholder={isAdminMode ? 'admin@bookmyroute.com' : 'you@example.com'}
              autoComplete="email"
              required
            />
            <Field
              label="Password"
              type="password"
              value={form.password}
              onChange={set('password')}
              placeholder={isAdminMode ? 'Admin password' : 'Password'}
              autoComplete="current-password"
              required
            />
            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full text-center mt-2 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {loading ? 'Signing in...' : isAdminMode ? 'Sign In as Admin' : 'Sign In'}
            </button>
          </form>

          {!isAdminMode && (
            <>
              <Divider />
              <div className="flex justify-center">
                <GoogleLogin
                  onSuccess={handleGoogleSuccess}
                  onError={handleGoogleError}
                  useOneTap
                  shape="rectangular"
                  theme="outline"
                  size="large"
                  text="signin_with"
                  width="100%"
                />
              </div>
            </>
          )}

          <p className="text-center text-sm text-gray-500 mt-6 font-body">
            New to BookMyRoute?{' '}
            <Link to="/register" className="text-[#D84E55] font-700 hover:underline">Create account</Link>
          </p>
          <BusStripe />
        </div>
      </div>
    </div>
  )
}

export function RegisterPage() {
  const { register, googleLogin } = useAuth()
  const navigate = useNavigate()
  const [form, setForm]       = useState({ name: '', email: '', phone: '', password: '', confirm: '' })
  const [loading, setLoading] = useState(false)

  const set = (k) => (e) => setForm(f => ({ ...f, [k]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.password !== form.confirm) { toast.error('Passwords do not match'); return }
    if (form.password.length < 8)       { toast.error('Password must be at least 8 characters'); return }
    setLoading(true)
    try {
      await register({ name: form.name, email: form.email.trim(), phone: form.phone, password: form.password })
      navigate('/search', { replace: true })
    } catch (err) {
      toast.error(getErrorMessage(err, 'Unable to create account. Please try again.'))
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      await googleLogin(credentialResponse.credential)
      navigate('/search', { replace: true })
    } catch (err) {
      toast.error(getErrorMessage(err, 'Google sign-up failed. Please try again.'))
    }
  }

  const handleGoogleError = () => {
    toast.error('Google sign-up was cancelled or failed.')
  }

  return (
    <div className="min-h-screen bg-[#F6F7FB] flex items-center justify-center p-4
                    bg-[radial-gradient(ellipse_at_top_left,#059669/20,transparent_60%),radial-gradient(ellipse_at_bottom_right,#DC2626/10,transparent_60%)]">
      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 bg-[#172033] text-white px-5 py-2.5
                          rounded-full border border-[#059669] shadow-sm mb-6">
            <FaBus className="text-[#F59E0B]" />
            <span className="font-display font-800 text-lg tracking-tight">BookMyRoute</span>
          </div>
          <h1 className="text-4xl font-display text-[#172033] mb-2">Create account</h1>
          <p className="text-gray-500 font-body">Join thousands of happy travellers</p>
        </div>

        <div className="relative card p-8 pb-10">
          <div className="flex justify-center mb-4">
            <GoogleLogin
              onSuccess={handleGoogleSuccess}
              onError={handleGoogleError}
              shape="rectangular"
              theme="outline"
              size="large"
              text="signup_with"
              width="100%"
            />
          </div>

          <Divider />

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <Field label="Full name"        value={form.name}     onChange={set('name')}     placeholder="Rahul Sharma"     autoComplete="name"         required />
            <Field label="Email address"    type="email"          value={form.email}    onChange={set('email')}    placeholder="you@example.com"  autoComplete="email"        required />
            <Field label="Phone number"     value={form.phone}    onChange={set('phone')}    placeholder="+91 98765 43210"  autoComplete="tel" />
            <Field label="Password"         type="password"       value={form.password} onChange={set('password')} placeholder="Min 8 characters" autoComplete="new-password" required />
            <Field label="Confirm password" type="password"       value={form.confirm}  onChange={set('confirm')}  placeholder="Repeat password"  autoComplete="new-password" required />

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full text-center mt-2 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6 font-body">
            Already have an account?{' '}
            <Link to="/login" className="text-[#D84E55] font-700 hover:underline">Sign in</Link>
          </p>
          <BusStripe />
        </div>
      </div>
    </div>
  )
}